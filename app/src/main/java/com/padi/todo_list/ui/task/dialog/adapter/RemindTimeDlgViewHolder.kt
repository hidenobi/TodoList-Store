package com.padi.todo_list.ui.task.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.RowSpinnerOffsetTimeBinding
import com.padi.todo_list.ui.task.model.OffsetTimeUI

class RemindTimeDlgViewHolder(
    private val binding: RowSpinnerOffsetTimeBinding,
    private val onClickItem: (OffsetTimeUI) -> Unit
) : BaseBindingViewHolder<OffsetTimeUI>(binding) {
    override fun bind(item: OffsetTimeUI, position: Int) {
        binding.apply {
            setVariable(BR.remindTime, item)
            cbTime.setOnDebounceClickListener(100) {
                onClickItem.invoke(item)
            }
            executePendingBindings()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickItem: (OffsetTimeUI) -> Unit
        ): RemindTimeDlgViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_spinner_offset_time, parent, false)
            val binding = RowSpinnerOffsetTimeBinding.bind(view)
            return RemindTimeDlgViewHolder(binding, onClickItem)
        }
    }

}
