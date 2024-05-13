package com.padi.todo_list.ui.my

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.github.mikephil.charting.data.PieEntry
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.common.base.BaseBindingViewHolder


class LabelAdapter :
    BaseBindingAdapter<PieEntry, BaseBindingViewHolder<PieEntry>>(COMPARATOR) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseBindingViewHolder<PieEntry> {
        return LabelViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<PieEntry>, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun setData(data: List<PieEntry>) {
        submitList(data)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<PieEntry>() {
            override fun areItemsTheSame(
                oldItem: PieEntry,
                newItem: PieEntry
            ): Boolean {
                return oldItem.value == newItem.value
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: PieEntry,
                newItem: PieEntry
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}