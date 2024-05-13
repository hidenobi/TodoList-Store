package com.padi.todo_list.ui.task.dialog.adapter

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.databinding.RowSubTaskBinding
import com.padi.todo_list.ui.task.model.SubTaskUI


class SubTaskViewHolder(
    private val binding: RowSubTaskBinding,
    private val onTickSubtask: (SubTaskUI) -> Unit,
    private val onChangeContentSubtask: (String, Int) -> Unit,
    private val onClickDelete: (SubTaskUI) -> Unit,
) : BaseBindingViewHolder<SubTaskUI>(binding) {

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            onChangeContentSubtask.invoke(s?.toString() ?: "", adapterPosition)
        }
    }

    override fun bind(item: SubTaskUI, position:Int) {
        binding.apply {
            setVariable(BR.subtask, item)
            if (item.isDone && !edtSubTask.paint.isStrikeThruText) {
                edtSubTask.paintFlags = edtSubTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (!item.isDone && edtSubTask.paint.isStrikeThruText) {
                edtSubTask.paintFlags = edtSubTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            imgDeleteSubtask.setOnClickListener {
                onClickDelete.invoke(item)
            }
            imgTickSubtask.setOnClickListener {
                onTickSubtask.invoke(item)
            }
            executePendingBindings()
        }
    }

    fun addOnTextChange() {
        binding.edtSubTask.addTextChangedListener(textWatcher)
    }

    fun removeOnTextChange() {
        binding.edtSubTask.removeTextChangedListener(textWatcher)
    }

    fun requestFocusOnEditText() {
        binding.edtSubTask.requestFocus()
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClickDone: (SubTaskUI) -> Unit,
            onChangeContentSubtask: (String, Int) -> Unit,
            onClickDelete: (SubTaskUI) -> Unit,
        ): SubTaskViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_sub_task, parent, false)
            val binding = RowSubTaskBinding.bind(view)
            return SubTaskViewHolder(
                binding,
                onClickDone,
                onChangeContentSubtask,
                onClickDelete
            )
        }
    }

}