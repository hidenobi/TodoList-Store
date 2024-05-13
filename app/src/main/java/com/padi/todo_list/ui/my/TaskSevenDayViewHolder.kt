package com.padi.todo_list.ui.my

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.databinding.ItemTaskSevenDayBinding

class TaskSevenDayViewHolder(
    private val binding: ItemTaskSevenDayBinding,
) :
    BaseBindingViewHolder<EventTaskEntity>(binding) {
    override fun bind(item: EventTaskEntity, position: Int) {
        binding.apply {
            setVariable(BR.event, item)
            executePendingBindings()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ): TaskSevenDayViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task_seven_day, parent, false)
            val binding = ItemTaskSevenDayBinding.bind(view)
            return TaskSevenDayViewHolder(binding)
        }
    }
}