package com.padi.todo_list.ui.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseRecyclerAdapter
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.ItemFlagBinding
import com.padi.todo_list.ui.task.lib.SwipeRevealLayout
import com.padi.todo_list.ui.task.model.FlagUI

class FlagAdapter(listItem: ArrayList<FlagUI>, val listener: ItemClickListener) :
    BaseRecyclerAdapter<FlagUI>(listItem) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FlagUI> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFlagBinding.inflate(layoutInflater)
        return FlagViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<FlagUI>, position: Int) {
        super.onBindViewHolder(holder, position)
        val marginStartEnd =
            holder.itemView.resources.getDimensionPixelSize(R.dimen.margin_item_tab_title_end)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, marginStartEnd, 0, 0)
        holder.itemView.layoutParams = layoutParams
    }

}

interface ItemClickListener {
    fun onClick(view: View, position: Int)

}