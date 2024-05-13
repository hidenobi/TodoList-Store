package com.padi.todo_list.ui.task.dialog.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.ui.task.model.RepeatOptions

class RepeatOptionAdapter(var onClickItem: (RepeatOptions) -> Unit) :
    BaseBindingAdapter<RepeatOptions, RepeatOptionViewHolder>(COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepeatOptionViewHolder {
        return RepeatOptionViewHolder.create(parent, onClickItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<RepeatOptions>) {
        submitList(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RepeatOptionViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<RepeatOptions>() {
            override fun areItemsTheSame(
                oldItem: RepeatOptions, newItem: RepeatOptions
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: RepeatOptions,
                newItem: RepeatOptions,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}