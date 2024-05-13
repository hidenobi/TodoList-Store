package com.padi.todo_list.ui.widget_setting.adapter

import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.RowTitleTaskWsPrevBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class TitleTaskWSPRevViewHolder(private val binding: RowTitleTaskWsPrevBinding) :
    BaseViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI) {
        binding.event = item
        super.bind(item)
    }

}