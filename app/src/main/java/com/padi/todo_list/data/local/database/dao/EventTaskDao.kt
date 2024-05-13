package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.EventTaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
abstract class EventTaskDao : BaseDao<EventTaskEntity> {
    @Query("SELECT * FROM event_task_entity")
    abstract fun getAllEventTasks(): Maybe<List<EventTaskEntity>>
    @Query("SELECT COUNT(*) FROM event_task_entity")
    abstract fun countAllEventTasks(): Maybe<Int>

    @Query("SELECT * FROM event_task_entity WHERE category_id = :categoryId")
    abstract fun getEventTasksByCategoryId(categoryId: Long): Maybe<List<EventTaskEntity>>

    @Query("SELECT * FROM event_task_entity WHERE id = :taskID")
    abstract fun getEvenByID(taskID: Long): Maybe<EventTaskEntity>
    @Query("SELECT * FROM event_task_entity WHERE is_star = 1")
    abstract fun getStarTask(): Maybe<List<EventTaskEntity>>
    @Query("UPDATE event_task_entity SET is_star = CASE WHEN is_star = 0 THEN 1 ELSE 0 END WHERE id = :taskId")
    abstract fun updateIsStar(taskId: Long)

    @Query("UPDATE event_task_entity SET event_name = :name WHERE id = :taskId")
    abstract fun updateEventTaskName(taskId: Long?, name: String): Completable

    @Query("SELECT COUNT(*) FROM event_task_entity WHERE category_id = :categoryId")
    abstract fun getEventTasksCountByCategoryId(categoryId: Long?): Maybe<Long>

    @Query("DELETE FROM event_task_entity WHERE id = :eventTaskId")
    abstract fun deleteTasksById(eventTaskId: Long?): Completable

    @Query("SELECT * FROM event_task_entity WHERE event_name LIKE '%' || :search || '%' ORDER BY  event_name")
    abstract fun searchEventTasksByName(search: String?): Maybe<List<EventTaskEntity>>

    @Query("SELECT * FROM event_task_entity WHERE category_id = :categoryId AND event_name LIKE '%' || :search || '%' ORDER BY event_name")
    abstract fun searchEventTasksByName(categoryId: Long, search: String): Maybe<List<EventTaskEntity>>

    @Query("SELECT * FROM event_task_entity WHERE is_completed = 1 ORDER BY date_complete DESC")
    abstract fun getCompleteTask(): Maybe<List<EventTaskEntity>>

    @Query("DELETE FROM event_task_entity WHERE is_completed = 1")
    abstract fun deleteCompletedTasks(): Completable

    @Query("SELECT * FROM event_task_entity WHERE date_complete BETWEEN :startWeek AND :endWeek")
    abstract fun getTaskCompleteByRange(startWeek: Long, endWeek: Long): Maybe<List<EventTaskEntity>>

    @Query("SELECT * FROM event_task_entity WHERE due_date BETWEEN :startTime AND :endTime AND is_completed = 0 AND date_complete = -1 ORDER BY category_id DESC")
    abstract fun getTaskPendingByRange(startTime: Long, endTime: Long): Maybe<List<EventTaskEntity>>

    @Query("SELECT * FROM event_task_entity WHERE is_completed = 0 AND date_complete = -1 ORDER BY category_id DESC")
    abstract fun getAllPendingTask(): Maybe<List<EventTaskEntity>>

    @Query("UPDATE event_task_entity SET is_completed = :isComplete , date_complete = :timeComplete WHERE id = :taskId")
    abstract fun markDoneTask(taskId: Long?, timeComplete: Long?, isComplete: Int?): Completable

    @Query("DELETE FROM reminder_time_entity WHERE event_task_id =:taskID ")
    abstract fun deleteRemind(taskID: Long): Completable

    @Query("DELETE FROM event_task_entity WHERE category_id =:categoryId ")
    abstract fun deleteTaskByCateId(categoryId: Long): Completable

    @Query("UPDATE event_task_entity SET is_note = :isNote WHERE id = :taskId")
    abstract fun setStateNoteTask(taskId: Long, isNote: Boolean): Completable

    @Query("UPDATE event_task_entity SET has_file = :hasFile WHERE id = :taskId")
    abstract fun setStateFileTask(taskId: Long, hasFile: Boolean): Completable

    @Query("UPDATE event_task_entity SET has_subtask = :hasSubTask WHERE id = :taskId")
    abstract fun setStateSubTask(taskId: Long, hasSubTask: Boolean): Completable

    @Query("UPDATE event_task_entity SET used_create_note = :used WHERE id = :taskId")
    abstract fun usedCreatedNote(taskId: Long, used: Boolean): Completable
}