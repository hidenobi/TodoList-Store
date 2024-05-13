package com.padi.todo_list.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.databinding.RowCategoryMenuBinding
import com.padi.todo_list.ui.task.model.TabCategory

class CategoryMenuViewHolder(
    private val binding: RowCategoryMenuBinding,
    private val onClick: (TabCategory) -> Unit,
) :
    BaseBindingViewHolder<TabCategory>(binding) {
    override fun bind(item: TabCategory, position: Int) {
        binding.apply {
            setVariable(BR.event, item)
            itemCategory.setOnDebounceClickListener {
                onClick.invoke(item)
            }
            executePendingBindings()
            onClick.invoke(item)
        }

    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (TabCategory) -> Unit
        ): CategoryMenuViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_category_menu, parent, false)
            val binding = RowCategoryMenuBinding.bind(view)
            return CategoryMenuViewHolder(binding, onClick)
        }
    }
}