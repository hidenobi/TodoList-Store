package com.padi.todo_list.ui.detail_task

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.databinding.RowSubTaskBinding
import com.padi.todo_list.ui.task.model.SubTaskUI
import timber.log.Timber

class SubTaskViewHolder(
    private val binding: RowSubTaskBinding,
    private val onChangeStatus: (Int) -> Unit,
    private val onChangeContentSubtask: (String, Int) -> Unit,
    private val onDelete: (Int) -> Unit,
    private val onDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
    private val onFocus: ((Int, Boolean) -> Unit)? = null
) : BaseBindingViewHolder<SubTaskUI>(binding) {
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            onChangeContentSubtask.invoke(s?.toString() ?: "", binding.edtSubTask.tag as Int)
        }
    }
    init {
        binding.edtSubTask.addTextChangedListener(textWatcher)
        binding.edtSubTask.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.imgDeleteSubtask.setImageResource(R.drawable.ic_delete_subtask)
            } else {
                binding.imgDeleteSubtask.setImageResource(R.drawable.ic_menu_grey)
            }
            onFocus?.invoke(binding.edtSubTask.tag as Int, b)
        }
    }

    override fun bind(item: SubTaskUI, position: Int) {
        binding.apply {
            setVariable(BR.subtask, item)
            executePendingBindings()
            imgDeleteSubtask.setOnClickListener {
                if (item.focused && !item.isEventComplete) {
                    onDelete.invoke(position)
                }
            }
            imgDeleteSubtask.setOnLongClickListener {
                if(!item.focused && !item.isEventComplete){
                    onDrag?.invoke(this@SubTaskViewHolder)
                }
                true
            }
            if (item.focused) {
                edtSubTask.requestFocus()
            }
            imgTickSubtask.setOnClickListener {
                if (!item.isEventComplete){
                    onChangeStatus.invoke(position)
                }
            }
        }
    }

    fun setTag(position: Int){
        binding.edtSubTask.tag = position
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onChangeStatus: (Int) -> Unit,
            onChangeContentSubtask: (String, Int) -> Unit,
            onDelete: (Int) -> Unit,
            onDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
            onFocus: ((Int, Boolean) -> Unit)? = null,
        ): SubTaskViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_sub_task, parent, false)
            val binding = RowSubTaskBinding.bind(view)
            return SubTaskViewHolder(
                binding,
                onChangeStatus,
                onChangeContentSubtask,
                onDelete,
                onDrag,
                onFocus,
            )
        }
    }
}