package com.padi.todo_list.ui.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.expandable_recyclerview.ExpandableAdapter
import com.padi.todo_list.ui.task.model.TabCategory
import timber.log.Timber

class LeftMenuAdapter(
    private val categories: MutableList<TabCategory>,
    private val menus: MutableList<MenuItem>,
    private val onClickItem: (String) -> Unit,
    private val onCategoryClick: (TabCategory,Int) -> Unit,
    private val onCreateNewClick: () -> Unit,
    private var getALlTask: String
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun getChildCount(groupPosition: Int): Int {
        return if (groupPosition == 1) return categories.size + 1 else 0
    }

    override fun getGroupCount(): Int = menus.size

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        if (groupPosition != 1) return
        if (holder is ChildViewHolder) {
            Timber.d("name: ${categories[childPosition].name} - count: ${categories[childPosition].taskCount.toString()}")
            holder.binding.txtCategory.text = categories[childPosition].name
            holder.binding.textCount.text = categories[childPosition].taskCount.toString()
            holder.binding.root.setOnDebounceClickListener {
                onCategoryClick.invoke(categories[childPosition],childPosition)
            }
            if (childPosition == 0){
                holder.binding.textCount.text = getALlTask
            }

            Timber.d("child: $childPosition - ${categories[childPosition].name}")
        } else if (holder is CreateNewViewHolder) {
            holder.binding.itemCategory.setOnDebounceClickListener {
                onCreateNewClick.invoke()
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        (holder as? GroupViewHolder)?.apply {
            binding.tvName.text = menus[groupPosition].name
            binding.icon.setImageResource(menus[groupPosition].icon)
            binding.arrowImage.rotation = if (expand) 180.0f else 0f
            binding.arrowImage.isVisible = groupPosition == 1
            if (groupPosition != 1) {
                binding.root.setOnClickListener {
                    onClickItem.invoke(menus[groupPosition].name)
                }
            }
        }
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        Timber.d("onCreateChildViewHolder: $viewType")
        return if (viewType == -1) {
            ChildViewHolder.create(viewGroup)
        } else {
            CreateNewViewHolder.create(viewGroup)
        }
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
        GroupViewHolder.create(viewGroup)

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        Timber.d("getChildItemViewType: $groupPosition - $childPosition")
        return if (groupPosition == 1 && childPosition == categories.size) 0 else -1
    }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        holder as GroupViewHolder
        val arrowImage = holder.binding.arrowImage
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 180f)
                .setDuration(animDuration)
                .start()
            // 不要使用这种动画，Item离屏之后，动画会取消
//            arrowImage.animate()
//                .setDuration(animDuration)
//                .rotation(0f)
//                .start()
        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                .setDuration(animDuration)
                .start()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCategory(data: List<TabCategory>) {
        categories.clear()
        categories.addAll(data)
        notifyDataSetChanged()
    }
}


