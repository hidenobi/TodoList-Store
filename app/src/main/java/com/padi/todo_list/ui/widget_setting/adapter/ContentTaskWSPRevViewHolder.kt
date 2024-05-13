package com.padi.todo_list.ui.widget_setting.adapter

import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.RowContentTaskWsPrevBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class ContentTaskWSPRevViewHolder(private val binding: RowContentTaskWsPrevBinding) :
    BaseViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI) {
        binding.event = item
        super.bind(item)
    }
}