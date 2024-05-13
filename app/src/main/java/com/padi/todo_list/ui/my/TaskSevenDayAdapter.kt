package com.padi.todo_list.ui.my

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.data.local.model.EventTaskEntity


class TaskSevenDayAdapter :
    BaseBindingAdapter<EventTaskEntity, BaseBindingViewHolder<EventTaskEntity>>(COMPARATOR) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseBindingViewHolder<EventTaskEntity> {
        return TaskSevenDayViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<EventTaskEntity>, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun setData(data: List<EventTaskEntity>) {
        submitList(data)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<EventTaskEntity>() {
            override fun areItemsTheSame(
                oldItem: EventTaskEntity,
                newItem: EventTaskEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: EventTaskEntity,
                newItem: EventTaskEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}