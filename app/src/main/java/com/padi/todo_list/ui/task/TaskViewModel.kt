package com.padi.todo_list.ui.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.transformToTaskUI
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.TabCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val taskDao: EventTaskDao,
    private val newTaskRepo: NewTaskRepositoryImpl,
) : BaseViewModel() {

    private val _listTab = MutableLiveData<ArrayList<TabCategory>>()
    val listTab: LiveData<ArrayList<TabCategory>> = _listTab

    val _listTask = MutableLiveData<ArrayList<EventTaskUI>>()
    val listTask: LiveData<ArrayList<EventTaskUI>> = _listTask.map { list ->
            val newList = ArrayList(list)
            if (list.any { it.isCompleted }) {
                newList.add(EventTaskUI.newInstance(EventTaskEntity(), isTextComplete = true))
            }
            newList
    }
    private val _listTaskSearch = MutableLiveData<ArrayList<EventTaskUI>>()
    val listTaskSearch: LiveData<ArrayList<EventTaskUI>> = _listTaskSearch.map { list ->
        val newList = ArrayList(list)
        if (list.any { it.isCompleted }) {
            newList.add(EventTaskUI.newInstance(EventTaskEntity(), isTextComplete = true))
        }
        newList
    }

    fun getAllCategory(idCategory: Long? = CategoryConstants.NO_CATEGORY_ID, textALL: String) {
        categoryDao.getAllCategories()
            .subscribeOn(SchedulerProvider.io())
            .map { list ->
                val default = list.map {
                    TabCategory.newInstance(it)
                }
                val tempList = ArrayList<TabCategory>()
                tempList.add(
                    0,
                    TabCategory(
                        CategoryConstants.NO_CATEGORY_ID,
                        textALL
                    )
                )
                tempList.addAll(default)
                if (tempList.any { item -> item.id == idCategory }) {
                    tempList.firstOrNull { m -> m.id == idCategory }?.isSelected = true
                }
                tempList
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    Timber.d( "getAllCategory: ${it.size}")
                    _listTab.postValue(it)
                },
                onError = {
                    Timber.d("getAllCategory error: ${it.message}")
                }).add(subscriptions)
    }

    fun getAllTasks() {
        taskDao.getAllEventTasks().subscribeOn(SchedulerProvider.io())
            .map {
                (it as ArrayList).transformToTaskUI()
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listTask.postValue(it)
                    _listTaskSearch.postValue(it)
                },
                onError = {

                }).add(subscriptions)
    }

    fun filterTasksByName(id: Long, name: String) {
        Timber.d("search function: $id - $name")
        val option = if (id == -1L) {
            taskDao.searchEventTasksByName(name)
        } else {
            taskDao.searchEventTasksByName(id, name)
        }
        option.subscribeOn(SchedulerProvider.io())
            .map { it ->
                it.map { EventTaskUI.newInstance(it) }
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listTaskSearch.postValue(it as ArrayList<EventTaskUI>?)
                },
                onError = {error ->
                    Timber.e("filterTasksByName: $error")
                }).add(subscriptions)
    }

    fun getEventTasksByCategoryId(id: Long) {
        taskDao.getEventTasksByCategoryId(id).subscribeOn(SchedulerProvider.io())
            .map {
                (it as ArrayList).transformToTaskUI()
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listTask.postValue(it)
                    _listTaskSearch.postValue(it)
                },
                onError = {

                }).add(subscriptions)
    }

    fun deleteEventTaskById(item: EventTaskUI) = item.let {
        item.id?.let { it1 ->
            taskDao.deleteTasksById(it1)
                .andThen(newTaskRepo.deleteRemindByIdTask(it1))
                .andThen(newTaskRepo.deleteAllSubTaskById(it1))
                .andThen(newTaskRepo.deleteNote(it1))
                .andThen(newTaskRepo.deleteImage(it1))
                .andThen(taskDao.getAllEventTasks())
                .defaultIfEmpty(emptyList())
                .map {
                    (it as ArrayList).transformToTaskUI()
                }
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.ui())
                .subscribeBy(
                    onSuccess = {
                        _listTask.postValue(it)
                        _listTaskSearch.postValue(it)
                    },
                    onError = {}

                )
                .add(subscriptions)
        }
    }

    fun updateEventTask(position: Int) {
        val itemList = _listTask.value
        itemList?.let { tasks ->
            if (position >= 0 && position < tasks.size) {
                val item = tasks[position]
                item.id?.let { itemId ->
                    taskDao.getEvenByID(itemId)
                        .flatMapCompletable { entity ->
                            entity.isStar = !entity.isStar
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
    fun updateIsChecked(position: Int) {
        val itemList = _listTask.value
        itemList?.let { tasks ->
            tasks.getOrNull(position)?.let { item ->
                item.id?.let { itemId ->
                    taskDao.getEvenByID(itemId)
                        .flatMapCompletable { entity ->
                            entity.dateComplete =
                                if (!entity.isCompleted) System.currentTimeMillis() else -1
                            entity.repeat = 0
                            entity.isCompleted = !entity.isCompleted
                            taskDao.update(entity)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                        .add(subscriptions)
                }
            }
        }
    }

    fun updateFlagType(newFlagType: Int, position: Int) {
        val itemList = _listTask.value // Lấy ra danh sách các EventTaskUI từ LiveData
        itemList?.let { tasks ->
            if (position >= 0 && position < tasks.size) { // Kiểm tra vị trí hợp lệ
                val item = tasks[position] // Lấy ra EventTaskUI tương ứng với vị trí
                item.id?.let { itemId ->
                    // Tìm và cập nhật EventTaskEntity tương ứng trong cơ sở dữ liệu
                    taskDao.getEvenByID(itemId)
                        .flatMapCompletable { entity ->
                            entity.flagType = newFlagType
                            taskDao.update(entity)

                        }
                        .subscribeOn(Schedulers.io()) // Thực hiện trên luồng IO
                        .observeOn(SchedulerProvider.ui()) // Xử lý kết quả trên luồng UI
                        .subscribe()
                        .add(subscriptions)
                }
            }
        }

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
    fun updateDueDateEventTask(task: EventTaskEntity, list: ArrayList<OffsetTimeUI>) {
        newTaskRepo.updateDueDateEventTask(task, list).subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = { emit ->
                    WidgetStandard.forceUpdateAll(
                        Application.getInstance().applicationContext,
                        emit as ArrayList
                    )
                    getAllTasks()
                    Timber.d("updateDueDateTask-success:")
                },
                onComplete = {
                    Timber.d("updateDueDateTask-complete:")
                },
                onError = {
                    Timber.d("updateDueDateTask-error: ${it.message}")
                }
            )
            .add(subscriptions)
    }
    private val _duplicate = MutableLiveData<EventTaskEntity>()
    val duplicate: LiveData<EventTaskEntity> = _duplicate
    fun repeatTask( idTask: Long) {
        Maybe.zip(
            newTaskRepo.getTaskById(idTask),
            newTaskRepo.getAllSubTask(idTask)
        ) { a, b ->
            Pair(a, b)
        }
            .flatMap { pair ->
                val task = pair.first.apply {
                    id = 0
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dueDate!!
                    when(repeat){
                        1 ->{
                            calendar.add(Calendar.HOUR_OF_DAY, 1)
                            dueDate = calendar.timeInMillis
                        }
                        2 ->{
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                            dueDate = calendar.timeInMillis
                        }
                        3 ->{
                            calendar.add(Calendar.WEEK_OF_MONTH, 1)
                            dueDate = calendar.timeInMillis
                        }
                        4 ->{
                            calendar.add(Calendar.MONTH, 1)
                            dueDate = calendar.timeInMillis
                        }
                        5 ->{
                            calendar.add(Calendar.YEAR, 1)
                            dueDate = calendar.timeInMillis
                        }
                    }
                    isCompleted = false
                }
                newTaskRepo.insertTask(task)
                    .flatMapMaybe { idTask ->
                        val listSub = pair.second.map {
                            it.copy(taskId = idTask,id = 0)
                        }
                        newTaskRepo.insertSubtask(listSub)
                            .andThen(Maybe.just(task.copy(id = idTask)))
                    }
            }
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _duplicate.postValue(it)
                },
                onError = {
                    Timber.e("Get Task Unsuccessfully")
                }
            )
            .addTo(subscriptions)
    }

}