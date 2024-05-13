package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.data.local.model.SubtaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
abstract class NoteDao : BaseDao<NoteEntity> {
    @Query("SELECT * FROM note_entity WHERE event_task_id = :eventId")
    abstract fun getNote(eventId: Long): Maybe<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNote(objs: NoteEntity): Completable

    @Query("DELETE FROM note_entity WHERE id = :id")
    abstract fun deleteNote( id: Long): Completable
}