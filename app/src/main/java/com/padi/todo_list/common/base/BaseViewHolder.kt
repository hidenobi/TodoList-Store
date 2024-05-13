package com.padi.todo_list.common.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
abstract class BaseViewHolder<T>(
    private val binding: ViewDataBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    open fun bind(item: T) {
        binding.executePendingBindings()
    }
}