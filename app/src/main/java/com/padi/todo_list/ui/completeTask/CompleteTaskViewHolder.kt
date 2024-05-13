package com.padi.todo_list.ui.completeTask

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.RowEventTaskBinding
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.UiModel

class CompleteTaskViewHolder(
    private val binding: RowEventTaskBinding,
    private val onClickCheck: (UiModel, Int) -> Unit,
    private val onClickFlag: (ImageView, EventTaskUI, Int) -> Unit,
    private val onClickItem: (EventTaskUI, Int) -> Unit,
) :
    BaseBindingViewHolder<UiModel>(binding) {
    override fun bind(item: UiModel, position: Int) {
        binding.apply {
            setVariable(BR.event, item)
            setVariable(BR.onCompleteSrc, true)
            if (item is EventTaskUI) {
                cbTask.setOnCheckedChangeListener {
                    onClickCheck.invoke(item, bindingAdapterPosition)
                }
                flags.setOnDebounceClickListener {
                    onClickFlag.invoke(flags, item, bindingAdapterPosition)
                }
                itemTask.setOnDebounceClickListener {
                    onClickItem.invoke(item, bindingAdapterPosition)
                }
                swipeLayout.setLockDrag(true)
            }
            executePendingBindings()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickCheck: (UiModel, Int) -> Unit,
            onClickFlag: (ImageView, EventTaskUI, Int) -> Unit,
            onClickItem: (EventTaskUI, Int) -> Unit,
        ): CompleteTaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_event_task, parent, false)
            val binding = RowEventTaskBinding.bind(view)
            return CompleteTaskViewHolder(binding, onClickCheck, onClickFlag, onClickItem)
        }
    }
}