package com.padi.todo_list.ui.language

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.padi.todo_list.common.base.BaseBindingAdapter

class LanguageAdapter(var onClickItem: (LanguageModel) -> Unit) :
    BaseBindingAdapter<LanguageModel, LanguageViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<LanguageModel>() {
            override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean {
                return oldItem.codeLang == newItem.codeLang
            }

            override fun areContentsTheSame(
                oldItem: LanguageModel,
                newItem: LanguageModel,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        return LanguageViewHolder.create(parent, onClickItem)
    }

    override fun setData(data: List<LanguageModel>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}