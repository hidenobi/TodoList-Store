package com.padi.todo_list.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_entity")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "event_task_id")
    var taskID: Long = -1,
    @ColumnInfo(name = "title_name")
    var title: String? = null,
    @ColumnInfo(name = "content_name")
    var content: String? = null
)
