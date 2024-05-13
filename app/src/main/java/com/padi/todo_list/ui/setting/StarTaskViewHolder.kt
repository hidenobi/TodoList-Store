package com.padi.todo_list.ui.setting

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.R
import com.padi.todo_list.BR
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.RowStarTaskBinding
import com.padi.todo_list.ui.task.model.EventTaskUI

class StarTaskViewHolder(
    private val binding: RowStarTaskBinding,
    private val onClickCheck: (EventTaskUI, Int) -> Unit,
    private val onClickStar: (EventTaskUI, Int) -> Unit,
) :
    BaseBindingViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI,position: Int) {
        binding.apply {
            setVariable(BR.event, item)
            setVariable(BR.onStartSrc, true)
            if (item.isCompleted && !txtNameTask.paint.isStrikeThruText) {
                txtNameTask.paintFlags = txtNameTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (!item.isCompleted && txtNameTask.paint.isStrikeThruText) {
                txtNameTask.paintFlags = txtNameTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            cbTask.setOnDebounceClickListener {
                onClickCheck.invoke(item, position)
            }
            star.setOnDebounceClickListener {
                onClickStar.invoke(item, position)
            }
            executePendingBindings()

        }

    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickCheck: (EventTaskUI, Int) -> Unit,
            onClickStar: (EventTaskUI, Int) -> Unit,
        ): StarTaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_star_task, parent, false)
            val binding = RowStarTaskBinding.bind(view)
            return StarTaskViewHolder(binding, onClickCheck, onClickStar)
        }
    }
}