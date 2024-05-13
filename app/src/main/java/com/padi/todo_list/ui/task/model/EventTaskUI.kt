package com.padi.todo_list.ui.task.model

import com.padi.todo_list.common.utils.TODAY
import com.padi.todo_list.data.local.model.EventTaskEntity
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.TimeStatusTask

@Parcelize
data class EventTaskUI(
    var id: Long? = 0,
    var categoryId:Long? = 0,
    var name: String? = null,
    var dueDate: Long? = 0,
    var typeTask: Int = TODAY,
    val isTitle: Boolean = false,
    var isSelected: Boolean = false,
    var isStar: Boolean = false,
    var isCompleted :Boolean = false,
    var flagType: Int? = 0,
    val taskCount: Long?= 0,
    val isTextComplete: Boolean= false,
    val dateComplete: Long= -1,
    var isNote :Boolean = false,
    var hasFile :Boolean = false,
    var timeStatus :Int = TimeStatusTask.NO_TIME,
    var dateStatus :Int = DateStatusTask.HAS_DATE,
    var repeat: Int = 0,
    var hasReminder: Boolean = false,
    var hasSubTask: Boolean = false,
    var usedCreateNote: Boolean = false,
): Parcelable, UiModel(){
    companion object {
        fun newInstance(
            entity: EventTaskEntity,
            isTitle: Boolean? = false,
            isTextComplete: Boolean? = false,
            typeTask: Int? = TODAY
        ): EventTaskUI =
            EventTaskUI(
                id = entity.id,
                categoryId= entity.categoryId,
                name = entity.name,
                dueDate = entity.dueDate,
                isTitle = isTitle!!,
                typeTask = typeTask!!,
                isStar = entity.isStar,
                flagType = entity.flagType,
                isCompleted = entity.isCompleted,
                isTextComplete = isTextComplete?:false,
                dateComplete = entity.dateComplete,
                isNote = entity.isNote,
                hasFile = entity.hasFile,
                timeStatus = entity.timeStatus,
                dateStatus = entity.dateStatus,
                repeat =  entity.repeat,
                hasReminder = entity.hasRemind,
                hasSubTask = entity.hasSubTask,
                usedCreateNote = entity.usedCreateNote,
                )
    }
}