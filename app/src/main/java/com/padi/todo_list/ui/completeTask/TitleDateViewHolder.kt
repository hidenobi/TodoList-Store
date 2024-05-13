package com.padi.todo_list.ui.completeTask

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.R
import com.padi.todo_list.BR
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.databinding.RowDateCompleteTaskBinding
import com.padi.todo_list.ui.task.model.TitleDateUI
import com.padi.todo_list.ui.task.model.UiModel

class TitleDateViewHolder(
    private val binding: RowDateCompleteTaskBinding,
) :
    BaseBindingViewHolder<UiModel>(binding) {
    override fun bind(item: UiModel, position: Int) {
        binding.apply {
            if (item is TitleDateUI) {
                setVariable(BR.date, item)
                executePendingBindings()
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ): TitleDateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_date_complete_task, parent, false)
            val binding = RowDateCompleteTaskBinding.bind(view)
            return TitleDateViewHolder(binding)
        }
    }
}