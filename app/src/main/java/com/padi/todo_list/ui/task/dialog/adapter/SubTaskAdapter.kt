package com.padi.todo_list.ui.task.dialog.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.ui.task.model.SubTaskUI

class SubTaskAdapter(
    var onTickSubtask: (SubTaskUI) -> Unit,
    var onChangeContentSubtask: (String, Int) -> Unit,
    var onClickDelete: (SubTaskUI) -> Unit,
) :
    BaseBindingAdapter<SubTaskUI, SubTaskViewHolder>(COMPARATOR) {
    private var lastItemPosition: Int = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        return SubTaskViewHolder.create(
            parent,
            onTickSubtask,
            onChangeContentSubtask,
            onClickDelete
        )
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    override fun onViewAttachedToWindow(holder: SubTaskViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition == lastItemPosition) {
            holder.requestFocusOnEditText()
        }
        holder.addOnTextChange()
    }

    override fun onViewDetachedFromWindow(holder: SubTaskViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.removeOnTextChange()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<SubTaskUI>) {
        lastItemPosition = data.size - 1
        submitList(data)
        notifyDataSetChanged()
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<SubTaskUI>() {
            override fun areItemsTheSame(
                oldItem: SubTaskUI,
                newItem: SubTaskUI
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SubTaskUI,
                newItem: SubTaskUI,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}