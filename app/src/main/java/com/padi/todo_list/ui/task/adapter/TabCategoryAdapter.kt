package com.padi.todo_list.ui.task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseRecyclerAdapter
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.ItemcatygoryBinding
import com.padi.todo_list.ui.task.model.TabCategory

class TabCategoryAdapter(
    var listTabTitle: ArrayList<TabCategory>,
    private val onClickListener: OnClickTabTitleListener
) : BaseRecyclerAdapter<TabCategory>(listTabTitle) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<TabCategory> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemcatygoryBinding.inflate(layoutInflater)
        return TabCategoryViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<TabCategory>, position: Int) {
        val marginStartEnd =
            holder.itemView.resources.getDimensionPixelSize(R.dimen.margin_item_tab_title_end)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, marginStartEnd, 0)
        holder.itemView.layoutParams = layoutParams
        super.onBindViewHolder(holder, position)
    }

    fun setData(listCategory: ArrayList<TabCategory>) {
        listTabTitle.clear()
        listTabTitle.addAll(listCategory)
        notifyDataSetChanged()
    }
    fun updateCategoryAtPosition(position: Int, newTabCategory: TabCategory) {
            listTabTitle[position] = newTabCategory
            notifyItemChanged(position)
    }

}

interface OnClickTabTitleListener {
    fun onClick(item: TabCategory)
}