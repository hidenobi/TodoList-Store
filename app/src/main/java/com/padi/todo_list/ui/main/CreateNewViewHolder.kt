package com.padi.todo_list.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.R
import com.padi.todo_list.databinding.RowCreatNewCategoriBinding
import com.padi.todo_list.expandable_recyclerview.ExpandableAdapter

class CreateNewViewHolder (val binding: RowCreatNewCategoriBinding) : ExpandableAdapter.ViewHolder(binding.root) {
    companion object {
        fun create(
            parent: ViewGroup,
        ): CreateNewViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_creat_new_categori, parent, false)
            val binding = RowCreatNewCategoriBinding.bind(view)
            return CreateNewViewHolder(binding)
        }
    }
}