package com.padi.todo_list.ui.detail_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.SingleLiveEvent
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.toUri
import com.padi.todo_list.common.extension.transformToListSubtask
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.FileImageEntity
import com.padi.todo_list.data.local.repository.AlarmRepositoryImpl
import com.padi.todo_list.data.local.repository.ManageCategoryRepository
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.ui.SharedActivityViewModel
import com.padi.todo_list.ui.task.model.FileImageUI
import com.padi.todo_list.ui.task.model.NoteUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.SubTaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject

const val KEY_MARK_DONE = "KEY_MARK_DONE"
const val KEY_DUPLICATE = "KEY_DUPLICATE"
const val KEY_SHARE = "KEY_SHARE"
const val KEY_DELETE = "KEY_DELETE"

@HiltViewModel
class DetailTaskViewModel @Inject constructor(
    private val alarmRepo: AlarmRepositoryImpl,
    private val newTaskRepo: NewTaskRepositoryImpl,
    private val widgetSettingRepo: WidgetSettingRepositoryImpl,
    private val manageCatRepo: ManageCategoryRepository,
    private val taskDao: EventTaskDao,
) : SharedActivityViewModel(alarmRepo, newTaskRepo, widgetSettingRepo, manageCatRepo) {

    private val _newTask = SingleLiveEvent<SubTaskUI>()
    val newTask: LiveData<SubTaskUI> = _newTask

    private val _eventNote = MutableLiveData<NoteUI>()
    val eventNoteLiveData: LiveData<NoteUI> = _eventNote

    private var taskId: Long? = null

    private val _eventFileImageUI = MutableLiveData<FileImageUI>()
    val eventFileImageUI: LiveData<FileImageUI> = _eventFileImageUI

    fun updateNameTask(item: EventTaskEntity, name: String) = item.let {
        item.id.let { it1 ->
            newTaskRepo.updateNameTask(it1, name).subscribeBy()
                .add(subscriptions)
        }
    }

    fun deleteSubtask(id: Long) {
        taskId?.let { taskId ->
            newTaskRepo.deleteSubTaskById(id)
                .andThen(
                    newTaskRepo.getAllSubTask(taskId)
                        .flatMapCompletable { subtasks ->
                            newTaskRepo.setStateSubTask(taskId, subtasks.isNotEmpty())
                        }
                )
                .subscribeOn(SchedulerProvider.io())
                .subscribeBy(
                    onComplete = {
                        Timber.i("Delete subtask successfully")
                    },
                    onError = { error ->
                        Timber.e("Delete subtask error: $error")
                    }
                ).addTo(subscriptions)
        }
    }

    fun getListSubTask(idTask: Long, isComplete: Boolean) {
        taskId = idTask
        newTaskRepo.getAllSubTask(idTask)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    val subs = it.map { SubTaskUI.newInstance(it, isComplete) }
                    _listSubTask.postValue(subs as ArrayList)
                }
            )
            .addTo(subscriptions)
    }

    fun saveSubTask(data: List<SubTaskUI>) {
        taskId?.let {
            newTaskRepo.insertSubtask(data.transformToListSubtask(it))
                .andThen(newTaskRepo.setStateSubTask(it, data.isNotEmpty()))
                .subscribeOn(SchedulerProvider.io())
                .subscribeBy(
                    onComplete = {},
                    onError = {
                        Timber.e("insert subtask error")
                    }
                ).addTo(subscriptions)
        }
    }

    override fun addOneSubTask() {
        _newTask.value = SubTaskUI(System.currentTimeMillis(), "", isDone = false, focused = true)
    }

    fun move(from: Int, target: Int, data: List<SubTaskUI>) {
        if (from >= data.size || target >= data.size) return
        if (from < target) {
            for (i in from until target) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in from downTo target + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        saveSubTask(data)
    }

    fun getData(idTask: Long) {
        Timber.d("getData")
        newTaskRepo.getNote(idTask)
            .map {
                NoteUI(
                    id = it.first().id,
                    idTask = it.first().taskID,
                    titLe = it.first().title,
                    content = it.first().content,
                    )
            }
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = {
                Timber.d("getData: ${it.id}")
                _eventNote.postValue(it)
            },
                onError = {
                    Timber.e("error: ${it.message}")
                    _eventNote.postValue(NoteUI(id = 0))
                })
            .addTo(subscriptions)

        newTaskRepo.getFile(idTask)
            .map {
                FileImageUI(
                    id = it.first().id,
                    idTask = it.first().taskID,
                    image = it.first().images?.firstOrNull()?.toUri()
                )
            }
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = {
                _eventFileImageUI.postValue(it)
            },
                onError = {
                    Timber.e("error: ${it.message}")
                })
            .addTo(subscriptions)
    }

    fun saveImage(image: String, idTask: Long) {
        val currentID = _eventFileImageUI.value?.id
        Timber.d("before image uri: ${eventFileImageUI.value?.image}, id: $currentID")
        val file = FileImageEntity(images = listOf(image), taskID = idTask, id = idTask)
        newTaskRepo.insertImage(file)
            .andThen(newTaskRepo.setStateFileTask(idTask, true))
            .subscribeOn(SchedulerProvider.io())
            .andThen(
                newTaskRepo.getFile(idTask)
                    .map {
                        FileImageUI(image = it.first().images?.firstOrNull()?.toUri())
                    }
            )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    Timber.d("after image uri: ${it.image}, id: ${it.id}")
                    _eventFileImageUI.postValue(it)
                },
                onError = {
                    Timber.e("get file error: ${it.message}")
                }
            )
            .addTo(subscriptions)
    }

    fun deleteImage(idTask: Long) {
        newTaskRepo.deleteImage(idTask)
            .andThen(newTaskRepo.setStateFileTask(idTask, false))
            .subscribeOn(SchedulerProvider.io())
            .andThen(
                newTaskRepo.getFile(idTask)
            )
            .map {
                FileImageUI(
                    image = it.first().images?.firstOrNull()?.toUri()
                )
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _eventFileImageUI.postValue(it)
                })
            .addTo(subscriptions)
    }

    private val _markDone = MutableLiveData<Boolean>()
    val markDone: LiveData<Boolean> = _markDone
    fun markDoneTask(idTask: Long, isComplete: Boolean) {
        newTaskRepo.updateTaskDone(
            idTask,
            if (isComplete) System.currentTimeMillis() else -1,
            if (isComplete) 1 else 0
        )
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onComplete = {
                    _eventTask.postValue(_eventTask.value!!.copy(isCompleted = isComplete))
                    _markDone.postValue(isComplete)
                },
                onError = {
                    Timber.e("Mark Done Task Error!")
                },
            )
            .addTo(subscriptions)
    }


    private val _deleteTask = MutableLiveData<Boolean>()
    val deleteTask: LiveData<Boolean> = _deleteTask
    fun deleteTask(idTask: Long) {
        newTaskRepo.deleteTaskById(idTask)
            .subscribeOn(SchedulerProvider.io())
            .andThen(
                newTaskRepo.deleteAllSubTaskById(idTask)
            )
            .andThen(newTaskRepo.deleteImage(idTask))
            .andThen(newTaskRepo.deleteNote(idTask))
            .andThen(newTaskRepo.deleteRemindByIdTask(idTask))
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onComplete = {
                    _deleteTask.postValue(true)
                },
                onError = {
                    Timber.e("Delete Task unsuccessful!")
                },
            )
            .addTo(subscriptions)
    }

    private val _duplicate = MutableLiveData<EventTaskEntity>()
    val duplicate: LiveData<EventTaskEntity> = _duplicate
    fun duplicateTask(textTitle: String, idTask: Long) {
        Maybe.zip(
            newTaskRepo.getTaskById(idTask),
            newTaskRepo.getAllSubTask(idTask),
            newTaskRepo.getNote(idTask),
            newTaskRepo.getFile(idTask),
        ) { a, b, c, d ->
            Quadruple(a, b, c, d)
        }
            .flatMap { quadruple ->
                val task = quadruple.first.apply {
                    name += textTitle
                    id = 0
                    isCompleted = false
                }
                newTaskRepo.insertTask(task)
                    .flatMapMaybe { newIdTask ->
                        val listSub = quadruple.second.map {
                            it.copy(taskId = newIdTask, id = 0)
                        }
                        newTaskRepo.insertSubtask(listSub)
                            .andThen(
                                if (quadruple.third.isNotEmpty())
                                    newTaskRepo.insertNote(
                                        quadruple.third.first().apply {
                                            taskID = newIdTask
                                            id = 0
                                        })
                                else
                                    Completable.complete()
                            )
                            .andThen(
                                if (quadruple.fourth.isNotEmpty())
                                    newTaskRepo.insertImage(
                                        quadruple.fourth.first().apply {
                                            taskID = newIdTask
                                            id = 0
                                        })
                                else
                                    Completable.complete()
                            ).andThen(
                                newTaskRepo.duplicateReminderOfTask(idTask, newIdTask)
                            )
                            .andThen(Maybe.just(task.copy(id = newIdTask)))
                    }
            }
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _duplicate.postValue(it)
                },
                onError = {
                    Timber.e("Get Task Unsuccessfully: ${it.message}")
                }
            )
            .addTo(subscriptions)
    }

    fun updateTask(
        task: EventTaskEntity,
        listOffset: ArrayList<OffsetTimeUI>? = null,
        isUpdateWithListOffset: Boolean
    ) {
        val input = task
        if (isUpdateWithListOffset) {
        input.hasRemind = !listOffset.isNullOrEmpty() && listOffset.any { it.isChecked }
        }
        newTaskRepo.updateTask(input, listOffset)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _eventTask.postValue(it)
                    Timber.d("updateCategoryForTask - success:")
                },
                onError = {
                    Timber.e("updateCategoryForTask - error: ${it.message}")
                }
            ).add(subscriptions)
    }
}
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)