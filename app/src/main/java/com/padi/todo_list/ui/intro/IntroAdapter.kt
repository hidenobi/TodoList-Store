package com.padi.todo_list.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import com.padi.todo_list.common.base.BaseRecyclerAdapter
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.databinding.ItemViewPagerBinding

class IntroAdapter(
    listItem: ArrayList<IntroModel>,
) : BaseRecyclerAdapter<IntroModel>(listItem) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<IntroModel> {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemViewPagerBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding)
    }

    inner class ImageViewHolder(private val binding: ItemViewPagerBinding) :
        BaseViewHolder<IntroModel>(binding) {
        override fun bind(item: IntroModel) {
            super.bind(item)
            binding.imageView.setImageResource(item.idImage)
            binding.tvContent.text = item.content
            binding.tvTitle.text = item.title
        }
    }

}