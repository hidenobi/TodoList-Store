package com.padi.todo_list.ui.task.model

import android.os.Parcelable
import com.padi.todo_list.data.local.model.SubtaskEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubTaskUI(
    val id: Long,
    var content: String,
    var isDone: Boolean = false,
    var focused: Boolean = true,
    var isEventComplete: Boolean = false,
): Parcelable {
    companion object {
        fun newInstance(entity: SubtaskEntity, isComplete: Boolean = false): SubTaskUI =
            SubTaskUI(id = entity.id, content = entity.name, isDone = entity.completed, focused = false, isEventComplete = isComplete)
    }
}