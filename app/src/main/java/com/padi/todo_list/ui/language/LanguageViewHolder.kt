package com.padi.todo_list.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.BR
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.databinding.RowItemLanguageBinding

class LanguageViewHolder(
    private val binding: RowItemLanguageBinding,
    private val onClickItem: (LanguageModel) -> Unit
) :BaseBindingViewHolder<LanguageModel>(binding){
    override fun bind(item: LanguageModel, position: Int) {
        binding.apply {
            setVariable(BR.model,item)
            executePendingBindings()
            root.setOnClickListener{
                onClickItem.invoke(item)
            }
        }
    }
    companion object {
        fun create(parent: ViewGroup, onClickItem: (LanguageModel) -> Unit): LanguageViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_language, parent, false)
            val binding = RowItemLanguageBinding.bind(view)
            return LanguageViewHolder(binding, onClickItem)
        }
    }

}