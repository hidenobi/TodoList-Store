package com.padi.todo_list.ui.manageCategory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.ItemManageCatygoryBinding
import com.padi.todo_list.ui.task.model.TabCategory

class ManageCategoryViewHolder(
    private val binding: ItemManageCatygoryBinding,
    private val showDialog: (item: TabCategory, ImageView, Int) -> Unit,
    private val onDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,

    ) : BaseBindingViewHolder<TabCategory>(binding) {
    override fun bind(item: TabCategory, position:Int) {
        binding.apply {
            setVariable(BR.category, item)
            executePendingBindings()
            menuMore.setOnDebounceClickListener {
                showDialog.invoke(item,menuMore,position)
            }
            itemTabTitle.setOnLongClickListener {
                onDrag?.invoke(this@ManageCategoryViewHolder)
                true
            }
        }
    }
    companion object {
        fun create(
            parent: ViewGroup,
            showDialog: (item:TabCategory,ImageView,Int) -> Unit
        ): ManageCategoryViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_catygory, parent, false)
            val binding = ItemManageCatygoryBinding.bind(view)
            return ManageCategoryViewHolder(binding, showDialog)
        }
    }
}