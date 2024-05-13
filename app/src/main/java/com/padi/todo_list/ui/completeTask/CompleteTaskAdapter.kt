package com.padi.todo_list.ui.completeTask

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.TitleDateUI
import com.padi.todo_list.ui.task.model.UiModel


class CompleteTaskAdapter(
    private val onClickCheck: (UiModel, Int) -> Unit,
    private val onClickFlag: (ImageView, EventTaskUI, Int) -> Unit,
    private val onClickItem: (EventTaskUI, Int) -> Unit,
) :
    BaseBindingAdapter<UiModel, BaseBindingViewHolder<UiModel>>(COMPARATOR) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseBindingViewHolder<UiModel> {
        return when (viewType) {
            R.layout.row_event_task -> CompleteTaskViewHolder.create(
                parent,
                onClickCheck,
                onClickFlag,
                onClickItem,
            )

            R.layout.row_date_complete_task -> TitleDateViewHolder.create(parent)
            else -> throw IllegalAccessException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<UiModel>, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun setData(data: List<UiModel>) {
        submitList(data)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EventTaskUI -> R.layout.row_event_task
            is TitleDateUI -> R.layout.row_date_complete_task
            else -> throw IllegalAccessException("unknown view type")
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(
                oldItem: UiModel,
                newItem: UiModel
            ): Boolean {
                return if (oldItem is TitleDateUI && newItem is TitleDateUI) oldItem.date == newItem.date
                else if (oldItem is EventTaskUI && newItem is EventTaskUI) oldItem.id == newItem.id
                else true
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: UiModel,
                newItem: UiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}