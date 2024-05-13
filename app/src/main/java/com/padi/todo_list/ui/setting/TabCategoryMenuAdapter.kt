package com.padi.todo_list.ui.setting

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.ui.task.model.TabCategory

class TabCategoryMenuAdapter(
    private val onClick: (TabCategory) -> Unit
) :
    BaseBindingAdapter<TabCategory, CategoryMenuViewHolder>(COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMenuViewHolder {
        return CategoryMenuViewHolder.create(parent, onClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<TabCategory>) {
        submitList(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryMenuViewHolder, position: Int) {
        holder.bind(getItem(position),position)

    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<TabCategory>() {
            override fun areItemsTheSame(
                oldItem: TabCategory,
                newItem: TabCategory
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TabCategory,
                newItem: TabCategory,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}