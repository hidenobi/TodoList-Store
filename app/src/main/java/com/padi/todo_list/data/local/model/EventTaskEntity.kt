package com.padi.todo_list.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.RepeatConstants
import com.padi.todo_list.common.utils.TimeStatusTask
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "event_task_entity")
data class EventTaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "category_id")
    var categoryId: Long? = CategoryConstants.NO_CATEGORY_ID,
    @ColumnInfo(name = "event_name")
    var name: String? = null,
    @ColumnInfo(name = "due_date")
    var dueDate: Long? = System.currentTimeMillis(),
    @ColumnInfo(name = "repeat")
    var repeat: Int = RepeatConstants.NO_REPEAT,
    @ColumnInfo(name = "notes")
    var notes: String? = null,
    @ColumnInfo(name = "flag_type")
    var flagType: Int? = 0,
    @ColumnInfo(name = "is_star")
    var isStar: Boolean = false,
    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,
    @ColumnInfo(name = "date_complete")
    var dateComplete: Long = -1L,
    @ColumnInfo(name = "is_note")
    var isNote: Boolean = false,
    @ColumnInfo(name = "has_file")
    var hasFile: Boolean = false,
    @ColumnInfo(name = "has_remind")
    var hasRemind: Boolean = false,
    @ColumnInfo(name = "has_subtask")
    var hasSubTask: Boolean = false,
    @ColumnInfo(name = "time_status")
    var timeStatus: Int = TimeStatusTask.NO_TIME,
    @ColumnInfo(name = "date_status")
    var dateStatus: Int = DateStatusTask.NO_DATE,
    @ColumnInfo(name = "used_create_note")
    var usedCreateNote: Boolean = false,
    @Ignore
    var nextAlarm: Long = -1,
    @Ignore
    var remindTime: Long = -1,
    @Ignore
    var isRemindModel: Boolean = false,
) : Parcelable
