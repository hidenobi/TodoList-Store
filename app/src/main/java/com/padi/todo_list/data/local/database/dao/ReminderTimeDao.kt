package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
abstract class ReminderTimeDao : BaseDao<ReminderTimeEntity> {
    @Query("SELECT * FROM reminder_time_entity")
    abstract fun getAllReminderTime(): Maybe<List<ReminderTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllReminderTime(list: ArrayList<ReminderTimeEntity>): Completable

    @Query("SELECT * FROM reminder_time_entity WHERE event_task_id =:eventTaskId")
    abstract fun getReminderByEventId(eventTaskId:Long): Maybe<List<ReminderTimeEntity>>

    @Query("DELETE FROM reminder_time_entity WHERE event_task_id =:eventTaskId ")
    abstract fun deleteRemindByTaskId(eventTaskId: Long): Completable
}