package com.padi.todo_list.data.local.repository

import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.extension.transformToListReminderTime
import com.padi.todo_list.common.utils.OffSetTimeValue
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.database.dao.FileImageDao
import com.padi.todo_list.data.local.database.dao.NoteDao
import com.padi.todo_list.data.local.database.dao.ReminderTimeDao
import com.padi.todo_list.data.local.database.dao.SubtaskDao
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.FileImageEntity
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import com.padi.todo_list.data.local.model.SubtaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NewTaskRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val eventTaskDao: EventTaskDao,
    private val reminderTimeDao: ReminderTimeDao,
    private val subtaskDao: SubtaskDao,
    private val noteDao: NoteDao,
    private val fileImageDao: FileImageDao
) : NewTaskRepository {

    override fun getAllCategories(): Maybe<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override fun insertTask(eventTaskEntity: EventTaskEntity): Single<Long> {
        return eventTaskDao.insert(eventTaskEntity)
    }

    override fun insertCategory(categoryEntity: CategoryEntity, listSize: Int): Single<Long> {
        return categoryDao.insert(categoryEntity.copy(position = listSize))
    }

    override fun getCategory(id: Long): Single<CategoryEntity> {
        return categoryDao.getCategory(id)
    }

    override fun insertReminderTime(list: ArrayList<ReminderTimeEntity>): Completable {
        return reminderTimeDao.insertAllReminderTime(list)
    }

    override fun insertSubtask(list: List<SubtaskEntity>): Completable {
        return subtaskDao.insertAllSubtask(list)
    }

    override fun getAllSubTask(idTask:Long): Maybe<List<SubtaskEntity>> {
        return subtaskDao.getAllSubtasks(idTask)
    }

    override fun getNote(idTask: Long): Maybe<List<NoteEntity>> {
        return noteDao.getNote(idTask)
    }

    override fun updateNote(obj: NoteEntity): Completable {
        return noteDao.update(obj)
    }

    override fun insertNote(obj: NoteEntity): Completable {
        return noteDao.insertNote(obj)
    }

    override fun deleteNote(id: Long): Completable {
        return noteDao.deleteNote(id)
    }

    override fun insertImage(obj: FileImageEntity): Completable {
        return fileImageDao.insertImage(obj)
    }

    override fun deleteImage( eventId: Long): Completable {
        return fileImageDao.deleteImage(eventId)
    }

    override fun getFile(idTask: Long): Maybe<List<FileImageEntity>> {
        return fileImageDao.getFile(idTask)
    }

    override fun deleteSubTaskById(id: Long): Completable  =subtaskDao.deleteById(id)

    override fun updateTaskDone(idTask: Long, timeComplete: Long, isComplete: Int): Completable = eventTaskDao.markDoneTask(idTask, timeComplete, isComplete)

    override fun deleteTaskById(idTask: Long): Completable = eventTaskDao.deleteTasksById(idTask)

    override fun deleteAllSubTaskById(idTask: Long): Completable = subtaskDao.deleteAllById(idTask)
    override fun getTaskById(idTask: Long): Maybe<EventTaskEntity> = eventTaskDao.getEvenByID(idTask)

    override fun updateNameTask(id: Long, name: String): Completable{
        return eventTaskDao.updateEventTaskName(id, name)
            .subscribeOn(SchedulerProvider.io())

    }

    override fun updateDueDateEventTask(
        task: EventTaskEntity,
        listOffset: ArrayList<OffsetTimeUI>
    ): Maybe<List<EventTaskUI>> {
        val remindList = listOffset.transformToListReminderTime(
            task.id, task.dueDate!!
        )
        return eventTaskDao.update(task).andThen(eventTaskDao.deleteRemind(task.id))
            .andThen(insertReminderTime(remindList))
            .andThen(
                eventTaskDao.getAllEventTasks()
            ).map {
                UtilsJ.getPrevData(
                    AppPrefs.scopeTime,
                    AppPrefs.scopeCategory,
                    AppPrefs.showCompletedTask,
                    it
                )
            }
    }

    override fun getReminderByEventId(eventTaskId: Long): Maybe<List<OffsetTimeUI>> {
        return reminderTimeDao.getReminderByEventId(eventTaskId)
            .map { data ->
                val condition = data.isNotEmpty()
                val resultList = OffsetTimeUI.getData(true)
                if (condition) {
                    data.forEach { entity ->
                        val index =
                            resultList.indexOfFirst { item -> item.offsetTime == entity.offsetTime }
                        if (index != -1) {
                            resultList[index].isChecked = true
                            resultList[index].text = getTextByOffset(entity)
                        } else {
                            resultList.last().isChecked = true
                            resultList.last().text = getTextByOffset(entity, true)
                        }
                    }
                }

                resultList
            }
    }

    override fun duplicateReminderOfTask(oldTaskId: Long, newTaskId: Long): Completable {
        return reminderTimeDao.getReminderByEventId(oldTaskId).flatMapCompletable { output ->
            val input = output.map { it.copy(eventTaskId = newTaskId, reminderTimeId = 0) }
            if (!input.isNullOrEmpty()) {
                reminderTimeDao.insertAllReminderTime(input as ArrayList)
            } else {
                Completable.complete()
            }
        }
    }

    private fun getTextByOffset(model: ReminderTimeEntity, isCustom: Boolean = false): String {
        return if (isCustom) {
            UtilsJ.customRemindText(model.remindTime)
        } else {
            when (model.offsetTime) {
                OffSetTimeValue.ZERO -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.same_with_due_date)
                }

                OffSetTimeValue.FIVE_MINUTES -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.five_minnutes_before)
                }

                OffSetTimeValue.TEN_MINUTES -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.ten_minnutes_before)
                }

                OffSetTimeValue.FIFTEEN_MINUTES -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.fifteen_minnutes_before)
                }

                OffSetTimeValue.THIRTY_MINUTES -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.thirty_minnutes_before)
                }

                else -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.one_day_before)
                }
            }
        }
    }
    override fun updateTask(
        task: EventTaskEntity,
        listOffset: ArrayList<OffsetTimeUI>?
    ): Maybe<EventTaskEntity> {
        return if (listOffset != null) {
            val remindList = listOffset.transformToListReminderTime(task.id, task.dueDate!!)
            eventTaskDao.deleteRemind(task.id)
                .andThen(insertReminderTime(remindList))
                .andThen(eventTaskDao.update(task))
                .andThen(eventTaskDao.getEvenByID(task.id))
        } else {
            eventTaskDao.update(task).andThen(eventTaskDao.getEvenByID(task.id))
        }
    }
    override fun getTabTaskCounts(): Maybe<Int> {
        return eventTaskDao.countAllEventTasks()
    }

    override fun deleteTaskByCateId(categoryId: Long): Completable = eventTaskDao.deleteTaskByCateId(categoryId)
    override fun getEventTasksByCategoryId(categoryId: Long): Maybe<List<EventTaskEntity>> =
        eventTaskDao.getEventTasksByCategoryId(categoryId)

    override fun deleteRemindByIdTask(taskId: Long): Completable =
        reminderTimeDao.deleteRemindByTaskId(taskId)

    override fun setStateNoteTask(taskId: Long, isNote: Boolean): Completable =
        eventTaskDao.setStateNoteTask(taskId, isNote)

    override fun setStateFileTask(taskId: Long, hasFile: Boolean): Completable =
        eventTaskDao.setStateFileTask(taskId, hasFile)

    override fun setStateSubTask(taskId: Long, hasSubTask: Boolean): Completable =
        eventTaskDao.setStateSubTask(taskId, hasSubTask)

    override fun usedCreatedNote(taskId: Long, used: Boolean): Completable =
        eventTaskDao.usedCreatedNote(taskId, used)
}