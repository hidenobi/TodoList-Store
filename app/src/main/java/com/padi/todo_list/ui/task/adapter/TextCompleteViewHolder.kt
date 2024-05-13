package com.padi.todo_list.ui.task.adapter

import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.RowTextCompleteTaskBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class TextCompleteViewHolder(
    private val binding: RowTextCompleteTaskBinding,
    private val onClickListener: OnClickListener
) : BaseViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI) {
        super.bind(item)
        binding.apply {
            textCompleteTask.setOnDebounceClickListener {
                onClickListener.onClick(item)
            }
        }
    }
}