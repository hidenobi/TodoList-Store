package com.padi.todo_list.ui.task.adapter

import android.graphics.Paint
import android.widget.ImageView
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseViewHolder
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.common.utils.COMPLETED
import com.padi.todo_list.common.utils.FUTURE
import com.padi.todo_list.common.utils.PREVIOUS
import com.padi.todo_list.common.utils.TODAY
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.RowEventTaskBinding
import com.padi.todo_list.ui.task.model.EventTaskUI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class TasksViewHolder(
    private val binding: RowEventTaskBinding,
    private val onClickListener: OnClickListener,
    private val onFlagClick: (ImageView, EventTaskUI, Int) -> Unit,
    private val onDeleteClick: (EventTaskUI, Int) -> Unit,
    private val onStarClick: ( EventTaskUI, Int) -> Unit,
    private val onCalenderClick: (EventTaskUI,Int) -> Unit
) : BaseViewHolder<EventTaskUI>(binding) {
    override fun bind(item: EventTaskUI) {
        binding.apply {
            event = item
            if (item.isCompleted && !txtNameTask.paint.isStrikeThruText) {
                txtNameTask.paintFlags = txtNameTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else if (!item.isCompleted && txtNameTask.paint.isStrikeThruText) {
                txtNameTask.paintFlags = txtNameTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            flags.setOnDebounceClickListener(750) {
                onFlagClick.invoke(flags, item, adapterPosition)
            }
            itemTask.setOnDebounceClickListener(750) {
                onClickListener.onClickItem(item,adapterPosition)
            }
            cbTask.setOnDebounceClickListener(750) {
                onClickListener.onClickCheckbox(item,adapterPosition)
            }
            calender.setOnDebounceClickListener(750){
                onCalenderClick.invoke(item,adapterPosition)
            }
            star.setOnDebounceClickListener(750){
                item.isStar = !item.isStar
                event = item

                if(item.isStar) showToast(root.context,root.context.getString(R.string.toast_star))
                else showToast(root.context,root.context.getString(R.string.toast_unstarred))
                swipeLayout.close(true)
                onStarClick.invoke(item,adapterPosition)

            }
            delete.setOnDebounceClickListener(750){
                onDeleteClick.invoke(item,adapterPosition)
            }
            when (UtilsJ.checkIsToDayType(item.dueDate!!)) {
                PREVIOUS -> {
                    if (!item.isCompleted) {
                        txtDay.setTextColor(root.context.getColor(R.color.red_E83434))
                    }
                }
                TODAY -> {
                    val timeNow = Instant.now().toEpochMilli()
                    if (item.dueDate!! < timeNow){
                        txtDay.setTextColor(root.context.getColor(R.color.red_E83434))
                    }
                }

            }
        }
        super.bind(item)
    }

}