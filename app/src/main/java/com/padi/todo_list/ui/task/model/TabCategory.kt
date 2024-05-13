package com.padi.todo_list.ui.task.model

import android.os.Parcelable
import com.padi.todo_list.data.local.model.CategoryEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class TabCategory(
    var id: Long = 0,
    var name: String = "",
    var isSelected: Boolean = false,
    var taskCount: Int? = 0,
    var position: Int = 0
) : Parcelable {
    companion object {
        fun newInstance(entity: CategoryEntity, taskCount: Int? = 0): TabCategory =
            TabCategory(
                id = entity.id,
                name = entity.name,
                taskCount = taskCount,
                position = entity.position
            )
    }
}
