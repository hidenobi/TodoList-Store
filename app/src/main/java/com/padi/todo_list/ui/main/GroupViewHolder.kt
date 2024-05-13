package com.padi.todo_list.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.R
import com.padi.todo_list.databinding.ItemGroupMenuBinding
import com.padi.todo_list.expandable_recyclerview.ExpandableAdapter

class GroupViewHolder(
    val binding: ItemGroupMenuBinding
): ExpandableAdapter.ViewHolder(binding.root) {

    companion object {
        fun create(
            parent: ViewGroup,
        ): GroupViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group_menu, parent, false)
            val binding = ItemGroupMenuBinding.bind(view)
            return GroupViewHolder(binding)
        }
    }
}