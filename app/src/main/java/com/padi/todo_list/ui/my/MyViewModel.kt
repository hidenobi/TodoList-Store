package com.padi.todo_list.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.utils.UtilsJ.getListOfDays
import com.padi.todo_list.common.utils.UtilsJ.getStartAndEndOfWeek
import com.padi.todo_list.common.utils.UtilsJ.localDateTimeToMillis
import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.SortedMap
import javax.inject.Inject

const val KEY_ONE_WEEK = "KEY_ONE_WEEK"
const val KEY_ONE_MONTH = "KEY_ONE_MONTH"
const val KEY_ALL = "KEY_ALL"
const val KEY_NOW = "KEY_NOW"
const val KEY_NEXT = "KEY_NEXT"
const val KEY_BACK = "KEY_BACK"

@HiltViewModel
class MyViewModel @Inject constructor(
    private val taskDao: EventTaskDao,
    private val categoryDao: CategoryDao,
) : BaseViewModel() {
    private val _listTask = MutableLiveData<List<EventTaskEntity>>()
    val listTask: LiveData<List<EventTaskEntity>> = _listTask
    fun getAllTasks() {
        taskDao.getAllEventTasks().subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listTask.postValue(it)
                }).add(subscriptions)
    }

    internal var numWeek = 0
    internal fun getDayOfWeek(type: String = KEY_NOW) {
        numWeek = when(type) {
            KEY_NOW -> numWeek
            KEY_NEXT -> numWeek - 1
            else -> numWeek + 1
        }.apply {
            getStartAndEndOfWeek(this).let { list ->
                val start = localDateTimeToMillis(list.first())
                val end = localDateTimeToMillis(list.last())
                getTaskOfTime(start, end, list)
            }
        }
    }

    private val _listTaskCmp = MutableLiveData<Pair<List<EventTaskEntity>, List<LocalDateTime>>>()
    val listTaskCmp: LiveData<SortedMap<LocalDateTime, Int>> = _listTaskCmp.map {
        it.second.associateWith { day ->
            it.first.count { task ->
                Instant.ofEpochMilli(task.dateComplete)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime().toLocalDate() == day.toLocalDate()
            }
        }.toSortedMap()
    }

    private val _listTaskCmp7Day = MutableLiveData<List<EventTaskEntity>>()
    val listTaskCmp7Day: LiveData<List<EventTaskEntity>> = _listTaskCmp7Day
    private fun getTaskOfTime(start: Long, end: Long, listDay: List<LocalDateTime>) {
        taskDao.getTaskCompleteByRange(start, end)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    it?.let { list ->
                        _listTaskCmp.postValue(Pair(list, listDay))
                    }
                }).add(subscriptions)
    }

    fun getTaskSevenDay() {
        getListOfDays(7).let { list ->
            val start = localDateTimeToMillis(list.first())
            val end = localDateTimeToMillis(list.last())
            taskDao.getTaskPendingByRange(start, end)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.ui())
                .subscribeBy(
                    onSuccess = { listTask ->
                        listTask?.let { list ->
                            _listTaskCmp7Day.postValue(list.sortedBy { it.dueDate })
                        }
                    }).add(subscriptions)
        }
    }

    private val _listTaskPending = MutableLiveData<Pair<List<EventTaskEntity>, List<CategoryEntity>>>()
    val listTaskPending: LiveData<Pair<List<EventTaskEntity>, List<CategoryEntity>>> = _listTaskPending

    fun handlePieChart(key: String) {
        val calendar = Calendar.getInstance()
        val filter = when (key) {
            KEY_ONE_WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                taskDao.getTaskPendingByRange(0, calendar.timeInMillis)
            }
            KEY_ONE_MONTH -> {
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                taskDao.getTaskPendingByRange(0, calendar.timeInMillis)
            }
            else -> taskDao.getAllPendingTask()
        }

        fetchZip2Data(
            filter,
            categoryDao.getAllCategories(),
        ) { listTask, listCategory ->
            _listTaskPending.postValue(Pair(listTask,listCategory))
        }
    }
}