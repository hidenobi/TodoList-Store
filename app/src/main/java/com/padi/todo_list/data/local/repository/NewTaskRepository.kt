package com.padi.todo_list.data.local.repository

import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.FileImageEntity
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import com.padi.todo_list.data.local.model.SubtaskEntity
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface NewTaskRepository {
    fun getAllCategories(): Maybe<List<CategoryEntity>>
    fun insertTask(eventTaskEntity: EventTaskEntity): Single<Long>
    fun insertCategory(categoryEntity: CategoryEntity, listSize: Int): Single<Long>
    fun getCategory(id: Long): Single<CategoryEntity>
    fun insertReminderTime(list: ArrayList<ReminderTimeEntity>): Completable
    fun insertSubtask(list: List<SubtaskEntity>): Completable
    fun getAllSubTask(idTask:Long): Maybe<List<SubtaskEntity>>
    fun getNote(idTask:Long): Maybe<List<NoteEntity>>
    fun updateNote(obj: NoteEntity): Completable
    fun insertNote(obj: NoteEntity): Completable
    fun deleteNote(id: Long): Completable
    fun deleteSubTaskById(id: Long): Completable
    fun insertImage(obj: FileImageEntity): Completable
    fun deleteImage(eventId: Long): Completable
    fun getFile(idTask: Long): Maybe<List<FileImageEntity>>
    fun updateTaskDone(idTask: Long, timeComplete: Long, isComplete: Int): Completable
    fun deleteTaskById(idTask: Long): Completable
    fun deleteAllSubTaskById(idTask: Long): Completable
    fun getTaskById(idTask: Long): Maybe<EventTaskEntity>
    fun getReminderByEventId(eventTaskId:Long): Maybe<List<OffsetTimeUI>>
    fun updateNameTask(id: Long, name: String): Completable
    fun updateDueDateEventTask(task: EventTaskEntity, listOffset: ArrayList<OffsetTimeUI>): Maybe<List<EventTaskUI>>
    fun updateTask(task: EventTaskEntity, listOffset: ArrayList<OffsetTimeUI>?): Maybe<EventTaskEntity>
    fun deleteTaskByCateId(categoryId: Long): Completable
    fun getEventTasksByCategoryId(categoryId: Long): Maybe<List<EventTaskEntity>>
    fun deleteRemindByIdTask(taskId: Long): Completable
    fun getTabTaskCounts(): Maybe<Int>
    fun setStateNoteTask(taskId: Long, isNote: Boolean): Completable
    fun setStateFileTask(taskId: Long, hasFile: Boolean): Completable
    fun setStateSubTask(taskId: Long, hasSubTask: Boolean): Completable
    fun usedCreatedNote(taskId: Long, used: Boolean): Completable
    fun duplicateReminderOfTask(oldTaskId: Long, newTaskId: Long): Completable
}