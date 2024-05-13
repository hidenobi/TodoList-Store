package com.padi.todo_list.ui.task.dialog.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.ui.task.model.OffsetTimeUI

class ReminderTimeOffsetAdapter(var onClickItem: (OffsetTimeUI) -> Unit) :
    BaseBindingAdapter<OffsetTimeUI, RemindTimeDlgViewHolder>(COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindTimeDlgViewHolder {
        return RemindTimeDlgViewHolder.create(parent, onClickItem)
    }

    override fun onBindViewHolder(holder: RemindTimeDlgViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<OffsetTimeUI>) {
        submitList(data)
        notifyDataSetChanged()
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<OffsetTimeUI>() {
            override fun areItemsTheSame(
                oldItem: OffsetTimeUI, newItem: OffsetTimeUI
            ): Boolean {
                return oldItem.position == newItem.position
            }

            override fun areContentsTheSame(
                oldItem: OffsetTimeUI,
                newItem: OffsetTimeUI,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}