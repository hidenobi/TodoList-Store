package com.padi.todo_list.ui.task.adapter

import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.ItemFlagBinding
import com.padi.todo_list.ui.task.model.FlagUI

class FlagViewHolder(
    private val binding: ItemFlagBinding,
    val listener: ItemClickListener
) : BaseViewHolder<FlagUI>(binding) {
    override fun bind(item: FlagUI) {
        super.bind(item)

        binding.apply {
            event = item
            updateIcFlag.setOnClickListener() {
                listener.onClick(itemView, adapterPosition)
            }
        }
    }
}
