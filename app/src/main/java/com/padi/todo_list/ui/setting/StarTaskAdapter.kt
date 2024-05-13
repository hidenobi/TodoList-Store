package com.padi.todo_list.ui.setting

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.ui.task.model.EventTaskUI

class StarTaskAdapter(
    private val onClickCheck: (EventTaskUI, Int) -> Unit,
    private val onClickStar: ( EventTaskUI, Int) -> Unit,
) :
    BaseBindingAdapter<EventTaskUI, StarTaskViewHolder>(COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarTaskViewHolder {
        return StarTaskViewHolder.create(parent, onClickCheck, onClickStar)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<EventTaskUI>) {
        submitList(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: StarTaskViewHolder, position: Int) {
        holder.bind(getItem(position),position)

    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<EventTaskUI>() {
            override fun areItemsTheSame(
                oldItem: EventTaskUI,
                newItem: EventTaskUI
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: EventTaskUI,
                newItem: EventTaskUI,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}