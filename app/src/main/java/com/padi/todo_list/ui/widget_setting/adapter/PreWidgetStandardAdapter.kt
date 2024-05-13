package com.padi.todo_list.ui.widget_setting.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.common.base.BaseRecyclerAdapter
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.RowContentTaskWsPrevBinding
import com.padi.todo_list.databinding.RowTitleTaskWsPrevBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class PreWidgetStandardAdapter(
    private val listData: ArrayList<EventTaskUI>,
) : BaseRecyclerAdapter<EventTaskUI>(listData) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<EventTaskUI> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.CONTENT.ordinal -> {
                val binding = RowContentTaskWsPrevBinding.inflate(layoutInflater)
                ContentTaskWSPRevViewHolder(binding)
            }

            else -> {
                val binding = RowTitleTaskWsPrevBinding.inflate(layoutInflater)
                TitleTaskWSPRevViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            listData[position].isTitle -> ViewType.TITLE.ordinal
            else -> ViewType.CONTENT.ordinal
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listTask: ArrayList<EventTaskUI>) {
        listData.clear()
        listData.addAll(listTask)
        notifyDataSetChanged()
    }
}

enum class ViewType {
    TITLE, CONTENT
}
