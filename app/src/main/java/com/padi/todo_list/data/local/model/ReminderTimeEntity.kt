package com.padi.todo_list.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reminder_time_entity")
data class ReminderTimeEntity(
    @PrimaryKey(autoGenerate = true) var reminderTimeId: Long = 0,
    @ColumnInfo(name = "event_task_id") var eventTaskId: Long,
    @ColumnInfo(name = "remind_time") var remindTime: Long,
    @ColumnInfo(name = "off_set") var offsetTime: Long,
) : Serializable
