package com.padi.todo_list.ui.manageCategory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.common.base.BaseBindingAdapter
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.databinding.ItemManageCatygoryBinding
import com.padi.todo_list.ui.task.model.SubTaskUI
import com.padi.todo_list.ui.task.model.TabCategory

class ManageCategoryAdapter(
    private var listCategories: ArrayList<TabCategory>,
    private val showDialog: (item: TabCategory, ImageView, Int) -> Unit,
    private val onDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
) : BaseBindingAdapter<TabCategory, ManageCategoryViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageCategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemManageCatygoryBinding.inflate(layoutInflater)
        return ManageCategoryViewHolder(binding, showDialog, onDrag)
    }


    override fun setData(data: List<TabCategory>) {
        listCategories.clear()
        listCategories.addAll(data)
        submitList(data)
    }

    override fun onBindViewHolder(holder: ManageCategoryViewHolder, position: Int) {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        holder.itemView.layoutParams = layoutParams
        holder.bind(getItem(position), position)
    }
    fun getData(): List<TabCategory> = listCategories
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<TabCategory>() {
            override fun areItemsTheSame(
                oldItem: TabCategory,
                newItem: TabCategory
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TabCategory,
                newItem: TabCategory,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}