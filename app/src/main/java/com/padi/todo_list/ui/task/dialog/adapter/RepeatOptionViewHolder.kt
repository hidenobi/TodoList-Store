package com.padi.todo_list.ui.task.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.databinding.RowRepeatOptionBinding
import com.padi.todo_list.ui.task.model.RepeatOptions

class RepeatOptionViewHolder(
    private val binding: RowRepeatOptionBinding,
    private val onClickItem: (RepeatOptions) -> Unit
) : BaseBindingViewHolder<RepeatOptions>(binding) {
    override fun bind(item: RepeatOptions, position: Int) {
        binding.apply {
            setVariable(BR.repeatOption, item)
            root.setOnClickListener {
                onClickItem.invoke(item)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickItem: (RepeatOptions) -> Unit
        ): RepeatOptionViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_repeat_option, parent, false)
            val binding = RowRepeatOptionBinding.bind(view)
            return RepeatOptionViewHolder(binding, onClickItem)
        }
    }

}
