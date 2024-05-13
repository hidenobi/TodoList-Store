package com.padi.todo_list.ui.task.adapter

import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.RowTitleEventTaskBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class TitleTasksViewHolder(private val binding: RowTitleEventTaskBinding,
                           private val onClickListener: OnClickListener
) : BaseViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI) {
        super.bind(item)
        binding.apply {
            event = item
            itemTitleTime.setOnDebounceClickListener(750) {
                item.isSelected = !item.isSelected
                event = item
                onClickListener.onClick(item)

            }

        }
    }

}