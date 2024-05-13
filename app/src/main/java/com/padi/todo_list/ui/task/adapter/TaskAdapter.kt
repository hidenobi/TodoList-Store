package com.padi.todo_list.ui.task.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseRecyclerAdapter
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.RowEventTaskBinding
import com.padi.todo_list.databinding.RowTextCompleteTaskBinding
import com.padi.todo_list.databinding.RowTitleEventTaskBinding
import com.padi.todo_list.ui.task.lib.SwipeRevealLayout
import com.padi.todo_list.ui.task.lib.ViewBinderHelper
import com.padi.todo_list.ui.task.model.EventTaskUI
import timber.log.Timber


class TaskAdapter(
    private val listHomeTask: ArrayList<EventTaskUI>,
    private val onClickListener: OnClickListener,
    private val onFlagClick: (ImageView, EventTaskUI, Int) -> Unit,
    private val onDeleteClick: (EventTaskUI, Int) -> Unit,
    private val onStarClick: (EventTaskUI, Int) -> Unit,
    private val onCalenderClick: (EventTaskUI,Int) -> Unit,
) : BaseRecyclerAdapter<EventTaskUI>(listHomeTask) {
    private val  binderHelper:ViewBinderHelper= ViewBinderHelper()
    private lateinit var swipeLayout: SwipeRevealLayout
    private lateinit var itemTask: ConstraintLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<EventTaskUI> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            EventType.ITEM.ordinal -> {
                val binding = RowEventTaskBinding.inflate(layoutInflater)
                TasksViewHolder(binding, onClickListener, onFlagClick,onDeleteClick,onStarClick,onCalenderClick)
            }
            EventType.COMPLETE.ordinal -> {
                val binding = RowTextCompleteTaskBinding.inflate(layoutInflater)
                TextCompleteViewHolder(binding, onClickListener)
            }
            else -> {
                val binding = RowTitleEventTaskBinding.inflate(layoutInflater)
                TitleTasksViewHolder(binding, onClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<EventTaskUI>, position: Int) {
        super.onBindViewHolder(holder, position)
        val layoutParams =
            RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                if (!getItem(position).isSelected) ViewGroup.LayoutParams.WRAP_CONTENT else 0
            )
        holder.itemView.layoutParams = layoutParams
        if (!getItem(position).isSelected) {
            (holder.itemView as? SwipeRevealLayout)?.close(false)
        }
        if (holder is TasksViewHolder) {
            swipeLayout = holder.itemView.findViewById(R.id.swipe_layout)
            itemTask = holder.itemView.findViewById(R.id.itemTask)
            val data = listHomeTask[position]
            binderHelper.setOpenOnlyOne(true)
            binderHelper.bind(swipeLayout, itemTask, data.toString(), position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            listHomeTask[position].isTitle -> EventType.TITLE.ordinal
            listHomeTask[position].isTextComplete -> EventType.COMPLETE.ordinal
            else -> EventType.ITEM.ordinal
        }
    }

    fun setData(listTask: ArrayList<EventTaskUI>) {
        listHomeTask.clear()
        listHomeTask.addAll(listTask)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): EventTaskUI {
        return listHomeTask[position]
    }
    fun restoreStates(inState: Bundle?) {
        binderHelper.restoreStates(inState)
    }
    fun saveStates(outState: Bundle?) {
        binderHelper.saveStates(outState)
    }
    fun removeItem(position: Int){
        listHomeTask.removeAt(position)
        notifyItemRemoved(position)
    }
    fun addItem(position: Int){
        notifyItemRemoved(position)
    }

}

enum class EventType {
    TITLE, ITEM, COMPLETE
}

interface OnClickListener {
    fun onClick(item: EventTaskUI)
    fun onClickItem(item: EventTaskUI,position: Int)
    fun onClickCheckbox(item: EventTaskUI,position: Int)
}
