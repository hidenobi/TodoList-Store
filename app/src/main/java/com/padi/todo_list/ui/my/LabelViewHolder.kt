package com.padi.todo_list.ui.my

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.mikephil.charting.data.PieEntry
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingViewHolder
import com.padi.todo_list.common.utils.COLOR_PIE_CHART
import com.padi.todo_list.databinding.ItemLabelPieBinding

class LabelViewHolder(
    private val binding: ItemLabelPieBinding,
) :
    BaseBindingViewHolder<PieEntry>(binding) {
    override fun bind(item: PieEntry, position: Int) {
        binding.apply {
            tvLabel.text = item.label
            tvCountCate.text = item.y.toInt().toString()
            ivLabel.setColorFilter(COLOR_PIE_CHART[position], PorterDuff.Mode.SRC_IN)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ): LabelViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_label_pie, parent, false)
            val binding = ItemLabelPieBinding.bind(view)
            return LabelViewHolder(binding)
        }
    }
}