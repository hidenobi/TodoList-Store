package com.padi.todo_list.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.R
import com.padi.todo_list.databinding.RowCategoryMenuBinding
import com.padi.todo_list.expandable_recyclerview.ExpandableAdapter

class ChildViewHolder(val binding: RowCategoryMenuBinding) : ExpandableAdapter.ViewHolder(binding.root) {
    companion object {
        fun create(
            parent: ViewGroup,
        ): ChildViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_category_menu, parent, false)
            val binding = RowCategoryMenuBinding.bind(view)
            return ChildViewHolder(binding)
        }
    }
}