package com.padi.todo_list.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_image_entity")
data class FileImageEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "event_task_id")
    var taskID: Long = -1,
    @ColumnInfo(name = "images")
    var images: List<String>? = null
)
