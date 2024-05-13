package com.padi.todo_list.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.padi.todo_list.ui.task.model.SubTaskUI

@Entity(tableName = "subtask_entity")
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "event_id")
    var taskId: Long,
    @ColumnInfo(name = "completed")
    var completed: Boolean = false,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "order")
    var order: Int
)
