package com.padi.todo_list.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.data.local.model.AlarmModel
import com.padi.todo_list.databinding.RowOverlayserviceInfoBinding

class OverlayInfoViewHolder(private val binding: RowOverlayserviceInfoBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(alarmModel: AlarmModel) {
        binding.tvContent.text = alarmModel.name
        binding.tvTime.text =
            alarmModel.remindTime.formatLongToTime(DateTimeFormat.DATE_TIME_FORMAT_1)
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ): OverlayInfoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_overlayservice_info, parent, false)
            val binding = RowOverlayserviceInfoBinding.bind(view)
            return OverlayInfoViewHolder(binding)
        }
    }

}
