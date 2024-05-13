package com.padi.todo_list.ui.detail_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.data.local.repository.NewTaskRepository
import com.padi.todo_list.ui.task.model.NoteUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val newTaskRepo: NewTaskRepository

) : BaseViewModel() {
    private val note = NoteUI()
    private val _eventNote = MutableLiveData<NoteUI>().apply {
        value = note
    }
    val eventNoteLiveData: LiveData<NoteUI> = _eventNote

    fun addOrUpdateNote(title: String, content: String?, idTask: Long) {
        val output = NoteEntity(id = idTask,content = content, title = title, taskID = idTask)
        newTaskRepo.insertNote(output)
            .andThen(newTaskRepo.setStateNoteTask(idTask, true))
            .andThen(newTaskRepo.usedCreatedNote(idTask, true))
            .subscribeOn(SchedulerProvider.io())
            .subscribeBy(
                onComplete = {}
            )
            .addTo(subscriptions)
    }

    fun deleteNote(idTask: Long){
        newTaskRepo.deleteNote(idTask)
            .andThen(newTaskRepo.setStateNoteTask(idTask, false))
            .subscribeOn(SchedulerProvider.io())
            .subscribeBy(
                onComplete = {},
                onError = {
                    Timber.e("delete note error: ${it.message}")
                }
            )
            .addTo(subscriptions)
    }

    fun updateNote(title: String, content: String, idTask: Long) {
        newTaskRepo.getNote(idTask)
            .subscribeOn(SchedulerProvider.io())
            .flatMapCompletable { existingNote ->
                existingNote.first().content = content
                existingNote.first().title = title
                newTaskRepo.updateNote(existingNote.first())
            }
            .subscribeBy()
            .addTo(subscriptions)
    }

    fun getNote(idTask: Long) {
        newTaskRepo.getNote(idTask)
            .subscribeOn(SchedulerProvider.io())
            .map {
                NoteUI(
                    id = it.first().id,
                    idTask = it.first().taskID,
                    titLe = it.first().title,
                    content = it.first().content
                )
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = {
                _eventNote.postValue(it)
            })
            .addTo(subscriptions)
    }
}