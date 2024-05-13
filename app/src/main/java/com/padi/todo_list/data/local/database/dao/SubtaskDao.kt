package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.SubtaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
abstract class SubtaskDao : BaseDao<SubtaskEntity> {
    @Query("SELECT * FROM subtask_entity WHERE event_id = :eventId ORDER BY `order`")
    abstract fun getAllSubtasks(eventId: Long): Maybe<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllSubtask(objs: List<SubtaskEntity>): Completable

    @Query("DELETE FROM subtask_entity WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("DELETE FROM subtask_entity WHERE event_id = :eventTaskId")
    abstract fun deleteAllById(eventTaskId: Long): Completable
}