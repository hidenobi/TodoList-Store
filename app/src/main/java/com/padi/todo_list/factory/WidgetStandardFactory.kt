package com.padi.todo_list.factory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.extension.parcelableArrayList
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.DateTimeFormatUtils
import com.padi.todo_list.common.utils.TimeStatusTask
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.receiver.widget.WidgetStandard.Companion.KEY_UPDATE_TASK
import com.padi.todo_list.receiver.widget.WidgetStandard.Companion.MARK_DONE_TASK_ACTION
import com.padi.todo_list.receiver.widget.WidgetStandard.Companion.WIDGET_STANDARD_DATA
import com.padi.todo_list.ui.task.model.EventTaskUI
import timber.log.Timber

class WidgetStandardFactory(
    private val context: Context,
    private val intent: Intent,
) : RemoteViewsService.RemoteViewsFactory {
    private var data = ArrayList<EventTaskUI>()
    override fun onCreate() {
        Timber.d("WidgetStandardFactory: onCreate")
        val bundle = intent.getBundleExtra(WIDGET_STANDARD_DATA)
        val inputData: ArrayList<EventTaskUI>? =
            bundle?.parcelableArrayList(WIDGET_STANDARD_DATA)
        if (!inputData.isNullOrEmpty()) {
            data.clear()
            data.addAll(inputData)
            Timber.d("WidgetStandardFactory: onCreate output ${data.size}")
        }
    }

    override fun onDataSetChanged() {
    }

    override fun onDestroy() {
        data.clear()
    }

    override fun getCount(): Int {
        return if (data.isEmpty()) 1 else data.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        Timber.d("WidgetStandardFactory: getViewAt")
        val bundle = Bundle().apply { putParcelable(KEY_UPDATE_TASK, data[position]) }
        val fillInIntent = Intent().apply {
            action = MARK_DONE_TASK_ACTION
            putExtras(bundle)
        }
//        content
        val remoteViewsContent =
            RemoteViews(context.packageName, R.layout.row_content_standard_widget).apply {
                setTextViewStrike(R.id.tvContent, data[position])
                setTextViewDueDate(R.id.tvTime, data[position])
                setImageViewBitmap(R.id.imgCheck, data[position].isCompleted)
                setOnClickFillInIntent(R.id.imgCheck, fillInIntent)
            }
//        title
        val remoteViewsTitle =
            RemoteViews(context.packageName, R.layout.row_title_standard_widget).apply {
                setTextViewText(R.id.tvTitle, data[position].name)
            }

        return when (data[position].isTitle) {
            true -> {
                remoteViewsTitle
            }

            else -> {
                remoteViewsContent
            }
        }


    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun RemoteViews.setImageViewBitmap(viewId: Int, isCompleted: Boolean) {
        val bitmap = Glide.with(context)
            .asBitmap()
            .load(
                if (isCompleted) {
                    R.drawable.checked
                } else {
                    R.drawable.unchecked
                }
            )
            .circleCrop()
            .submit()
            .get()
        this.setImageViewBitmap(viewId, bitmap)
    }

    private fun RemoteViews.setTextViewDueDate(viewId: Int, model: EventTaskUI) {
        val strTime =
            if (model.timeStatus == TimeStatusTask.HAS_TIME) {
                model.dueDate?.formatLongToTime(DateTimeFormatUtils.getTimeFormatValue())
            } else {
                ""
            }
        val strDate =
            if (!UtilsJ.isToday(model.dueDate!!) && (model.dateStatus == DateStatusTask.HAS_DATE)) {
                model.dueDate?.formatLongToTime(DateTimeFormatUtils.getDateFormatValue(model.dueDate!!))
            } else {
                ""
            }
        this.setTextViewText(viewId, "$strDate $strTime")
    }

    private fun RemoteViews.setTextViewStrike(viewId: Int, model: EventTaskUI) {
        if (model.isCompleted) {
            val spannableString = SpannableString(model.name)
            spannableString.setSpan(StrikethroughSpan(), 0, model.name!!.length, 0)
            setTextViewText(R.id.tvContent, spannableString)
            this.setTextViewText(viewId, spannableString)
        } else {
            this.setTextViewText(viewId, model.name)
        }
    }

}