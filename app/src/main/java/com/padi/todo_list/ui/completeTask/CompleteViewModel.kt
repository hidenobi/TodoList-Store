package com.padi.todo_list.ui.completeTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.TitleDateUI
import com.padi.todo_list.ui.task.model.UiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CompleteViewModel @Inject constructor(
    private val taskDao: EventTaskDao,
    private val newTaskRepo: NewTaskRepositoryImpl,
    private val widgetSettingRepo: WidgetSettingRepositoryImpl,
) : BaseViewModel() {
    private val _listTask = MutableLiveData<List<EventTaskEntity>>()
    val listTask: LiveData<List<UiModel>> = _listTask
        .map { listTask ->
            val newList = arrayListOf<UiModel>()
            var previousDate = ""
            listTask.forEachIndexed { index, task ->
                val dateStr = task.dateComplete.formatLongToTime(DateTimeFormat.DATE_TIME_FORMAT_3)
                if (dateStr != previousDate) {
                    newList.add(TitleDateUI.newInstance(dateStr, index == 0))
                    previousDate = dateStr
                }
                newList.add(EventTaskUI.newInstance(task))
            }
            newList
        }

    fun getAllTasks() {
        taskDao.getCompleteTask().subscribeOn(SchedulerProvider.io())
            .map { it }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = { _listTask.postValue(it) },
                onError = {})
            .add(subscriptions)
    }

    fun updateFlagType(newFlagType: Int, position: Int) {
        listTask.value?.let { tasks ->
            if (position in tasks.indices) {
                val item = tasks[position]
                if (item is EventTaskUI) {
                    item.id?.let { itemId ->
                        taskDao.getEvenByID(itemId)
                            .flatMapCompletable { entity ->
                                entity.flagType = newFlagType
                                taskDao.update(entity)
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(SchedulerProvider.ui())
                            .subscribe()
                            .add(subscriptions)
                    }
                }
            }
        }
    }

    fun updateIsChecked(position: Int) {
        listTask.value?.getOrNull(position)?.let { item ->
            if (item is EventTaskUI) {
                item.id?.let { itemId ->
                    taskDao.getEvenByID(itemId)
                        .flatMapCompletable { entity ->
                            entity.dateComplete =
                                if (!entity.isCompleted) System.currentTimeMillis() else -1
                            entity.isCompleted = !entity.isCompleted
                            taskDao.update(entity)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            getAllTasks()
                        }
                        .add(subscriptions)
                }
            }
        }
    }
    private val _deleteAll = MutableLiveData<Boolean>()
    val deleteAll: LiveData<Boolean> = _deleteAll
    fun deleteAllTask() {
        taskDao.deleteCompletedTasks()
            .subscribeOn(Schedulers.io())
            .subscribe{
                _deleteAll.postValue(true)
            }
            .add(subscriptions)
    }

    fun updateStandardWidget() {
        widgetSettingRepo.getPrevData(
            AppPrefs.scopeTime,
            AppPrefs.scopeCategory,
            AppPrefs.showCompletedTask
        )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = { inputData ->
                    WidgetStandard.forceUpdateAll(
                        Application.getInstance().applicationContext,
                        inputData as ArrayList
                    )
                    Timber.d("updateStandardWidget success")
                },
                onError = {
                    Timber.d("updateStandardWidget error: ${it.message}")
                },
            )
            .add(subscriptions)
    }

    private val _eventTaskInput = MutableLiveData<EventTaskEntity>()
    val eventTaskInputLiveData: LiveData<EventTaskEntity> = _eventTaskInput
    fun getEventTaskInput(id: Long) {
        newTaskRepo.getTaskById(id)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui()).subscribeBy(
                onSuccess = {
                    _eventTaskInput.value = it
                    Timber.d("getEventTaskInput success")
                },
                onError = {
                    Timber.d("getEventTaskInput - error: ${it.message}")
                }
            ).add(subscriptions)
    }

}