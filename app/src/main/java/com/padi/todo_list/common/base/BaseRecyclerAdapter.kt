package com.padi.todo_list.common.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(
    private val listItem: ArrayList<T>,
) : RecyclerView.Adapter<BaseViewHolder<T>>() {
    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(listItem[position])
    }
}