package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.FileImageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

@Dao
abstract class FileImageDao : BaseDao<FileImageEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertImage(objs: FileImageEntity): Completable

    @Query("SELECT * FROM file_image_entity WHERE event_task_id = :eventId")
    abstract fun getFile(eventId: Long): Maybe<List<FileImageEntity>>

    @Query("DELETE FROM file_image_entity WHERE event_task_id = :eventId")
    abstract fun deleteImage( eventId: Long): Completable
}