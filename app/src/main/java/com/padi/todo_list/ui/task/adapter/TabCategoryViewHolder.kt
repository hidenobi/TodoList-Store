package com.padi.todo_list.ui.task.adapter

import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.ItemcatygoryBinding
import com.padi.todo_list.ui.task.model.TabCategory

class TabCategoryViewHolder(
    private val binding: ItemcatygoryBinding,
    private val onClickListener: OnClickTabTitleListener
) : BaseViewHolder<TabCategory>(binding) {
    override fun bind(item: TabCategory) {
        val context = binding.root.context
        val resources = binding.root.resources
        binding.apply {
            event = item
            itemTabTitle.setOnDebounceClickListener {
                onClickListener.onClick(item)

            }
            if (item.isSelected){
                itemTabTitle.setBackgroundResource(R.drawable.round_background_select)
                tvMain.setTextColor(resources.getColor(R.color.white,null))
            }else{
                itemTabTitle.setBackgroundResource(R.drawable.round_background)
                tvMain.setTextColor(resources.getColor(R.color.grey_1,null))
            }
        }
        super.bind(item)
    }
}