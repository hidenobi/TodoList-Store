package com.padi.todo_list.common.utils

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchListener {
    fun onRowMoved(fromPosition: Int, toPosition: Int)
    fun onRowSelected(viewHolder: RecyclerView.ViewHolder?)
    fun onRowClear(viewHolder: RecyclerView.ViewHolder?)
}