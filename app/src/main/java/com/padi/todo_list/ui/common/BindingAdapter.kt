package com.padi.todo_list.ui.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.load
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.common.utils.BundleKey.DAY_MONTH_YEAR
import com.padi.todo_list.common.utils.BundleKey.MONTH_DAY_YEAR
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.common.utils.DateTimeFormatUtils
import com.padi.todo_list.common.utils.DateTimeFormatUtils.getTimeFormatValue
import com.padi.todo_list.common.utils.RepeatConstants
import com.padi.todo_list.common.utils.TimeStatusTask
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.calculateRemindMillis
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.ui.task.enum_class.DateSelectionEnum
import com.padi.todo_list.ui.task.enum_class.RemindUnitEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@BindingAdapter("app:srcSelect")
fun setImageResourceSelect(imageView: ImageView, isSelected: Boolean) {
    if (isSelected) {
        imageView.setImageResource(R.drawable.ic_down)
    } else {
        imageView.setImageResource(R.drawable.ic_up)
    }
}
@BindingAdapter("app:setImageNotification")
fun setImageNotification(imageView: ImageView, hasRemind: Boolean) {
    if (hasRemind) {
        imageView.setImageResource(R.drawable.ic_notification_task)
    }
}
@BindingAdapter("app:setImageRepeat")
fun setImageRepeat(imageView: ImageView, repeat: Int) {
    if (repeat != RepeatConstants.NO_REPEAT) {
        imageView.setImageResource(R.drawable.ic_repeat_task)
        imageView.visible()
    } else {
        imageView.gone()
    }
}
@BindingAdapter("app:setImageState")
fun setImageState(imageView: ImageView, isNote: Boolean) {
    if (isNote) {
        imageView.visible()
    } else {
        imageView.gone()
    }
}
@BindingAdapter("app:setBitMap")
fun setBitMap(imageView: ImageView,image: Bitmap?){
    if (image != null) {
        imageView.setImageBitmap(image)
        imageView.visible()
    } else {
        imageView.gone()
    }
}

@BindingAdapter("app:setVisible")
fun setVisible(imageView: ImageView,uriFile: Uri?){
    if (uriFile == null) {
        imageView.gone()
    } else {
        imageView.visible()
    }
}
@BindingAdapter("app:checked")
fun checked(view : View, isChecked: Boolean) {
    if (isChecked){
        view.visible()
    }else{
        view.gone()
    }
}

@BindingAdapter("app:textFormatDate")
fun textFormatDate(textView: TextView, dueDate: Long) {
    val dateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault())
    val formatDate = when (AppPrefs.getOptionDay()) {
        DAY_MONTH_YEAR -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_3.value))
        MONTH_DAY_YEAR -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_4.value))
        else -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_4.value))
    }
    textView.text = formatDate.toString()
}
@BindingAdapter("app:textFormatDateDeTail")
fun textFormatDateDeTail(textView: TextView, dueDate: Long) {
    val dateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault())
    val formatDate = when (AppPrefs.getOptionDay()) {
        DAY_MONTH_YEAR -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_7.value))
        MONTH_DAY_YEAR -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_8.value))
        else -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_6.value))
    }
    textView.text = formatDate.toString()
}
@BindingAdapter("app:textFormatTimeDeTail")
fun textFormatTimeDeTail(textView: TextView, dueDate: Long) {
    val dateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault())
    val formatDate = when (AppPrefs.getTimeFormat()) {
        1 -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_1.value))
        2 -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_9.value))
        else -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_1.value))
    }
    textView.text = formatDate.toString()
}

@BindingAdapter(value = ["app:detailTaskParam1", "app:detailTaskParam2"], requireAll = true)
fun setDeTailTaskTimeText(textView: TextView, dueDate: Long, dateTimeTask: Int) {
        if (dateTimeTask == TimeStatusTask.NO_TIME) {
            textView.text = textView.context.getString(R.string.not_available)
        } else {
            val dateTime =
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dueDate),
                    ZoneId.systemDefault()
                )
            val formatDate = when (AppPrefs.getTimeFormat()) {
                1 -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_1.value))
                2 -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_9.value))
                else -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_1.value))
            }
            textView.text = formatDate.toString()
        }
}

@BindingAdapter("app:convertLongToDateStr")
fun convertLongToDateStr(textView: TextView, dueDate: Long) {
    val dateTime = Instant.ofEpochMilli(dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    val formatDate = dateTime.format(DateTimeFormatter.ofPattern(getTimeFormatValue(true).value))
    textView.text = formatDate.toString()
}

@BindingAdapter("app:imageRes")
fun setImageResource(imageView: ImageView, @DrawableRes imageRes: Int) {
    imageView.setImageResource(imageRes)
}
@BindingAdapter("app:srcLanguage")
fun srcLanguage(imageView: ImageView, isSelected: Boolean) {
    if (isSelected) {
        imageView.setImageResource(R.drawable.ic_radio_button_checked)
    } else {
        imageView.setImageResource(R.drawable.ic_radio_button_unchecked)
    }
}

@BindingAdapter("app:setImageStar")
fun setImageStar(textView: TextView, isStar: Boolean) {
    val drawableResID = if (isStar) {
        R.drawable.ic_star_select
    } else {
        R.drawable.ic_star
    }
    textView.setCompoundDrawablesWithIntrinsicBounds(0, drawableResID, 0, 0)
}
@BindingAdapter("app:setStarTask")
fun setStarTask(textView: ImageView, isStar: Boolean) {
    val drawableResID = if (isStar) {
        R.drawable.ic_star_select
    } else {
        R.drawable.ic_star
    }
    textView.setImageResource(drawableResID)
}

@BindingAdapter("app:setImageFlag")
fun setImageFlag(imageView: ImageView, flagType: Int?) {
    when (flagType) {
        1 -> imageView.setImageResource(R.drawable.ic_flag_select_1)
        2 -> imageView.setImageResource(R.drawable.ic_flag_select_2)
        3 -> imageView.setImageResource(R.drawable.ic_flag_select_3)
        4 -> imageView.setImageResource(R.drawable.ic_flag_select_4)
        5 -> imageView.setImageResource(R.drawable.ic_flag_select_5)
        6 -> imageView.setImageResource(R.drawable.ic_flag_select_6)
        7 -> imageView.setImageResource(R.drawable.ic_flag_select_7)
        8 -> imageView.setImageResource(R.drawable.ic_flag_select_8)
        9 -> imageView.setImageResource(R.drawable.ic_flag_select_9)
        10 -> imageView.setImageResource(R.drawable.ic_flag_select_10)
        11 -> imageView.setImageResource(R.drawable.ic_flag_select_11)
        12 -> imageView.setImageResource(R.drawable.ic_flag_select_12)
        13 -> imageView.setImageResource(R.drawable.ic_flag_select_13)
        14 -> imageView.setImageResource(R.drawable.ic_flag_select_14)
        15 -> imageView.setImageResource(R.drawable.ic_flag_select_15)
        16 -> imageView.setImageResource(R.drawable.ic_flag_select_16)
        17 -> imageView.setImageResource(R.drawable.ic_flag_select_17)
        18 -> imageView.setImageResource(R.drawable.ic_flag_select_18)
        19 -> imageView.setImageResource(R.drawable.ic_flag_select_19)
        20 -> imageView.setImageResource(R.drawable.ic_flag_select_20)
        else -> imageView.setImageResource(R.drawable.ic_flag_unselect)
    }
}

@BindingAdapter("app:icon")
fun setIcon(imageView: ImageView, @DrawableRes iconResId: Int) {
    imageView.setImageResource(iconResId)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:infoRemindText")
fun setInfoOffsetTimeText(
    textview: TextView, offsetTimeUI: OffsetTimeUI
) {
    textview.text = offsetTimeUI.text
}

@SuppressLint("SetTextI18n")
@BindingAdapter(
    value = ["app:numberPicker", "app:unitPicker", "app:clockModel"],
    requireAll = true
)
fun setErrorOffsetTimeText(
    textview: TextView,
    number: Int,
    unit: RemindUnitEnum,
    clockModel: ClockModel,
) {
    val milliRemind = calculateRemindMillis(clockModel, number, unit)
    if (milliRemind <= Instant.now().toEpochMilli()) {
        textview.visibility = View.VISIBLE
    } else {
        textview.visibility = View.GONE
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter(
    "app:countNumberText",
)
fun setCountNumberText(
    textview: TextView,
    count: Int,
) {
    textview.text = "${count}/50"
}

@SuppressLint("SetTextI18n")
@BindingAdapter(
    "app:taskDateTimeFormatText"
)
fun setTaskDateTimeFormatText(
    textview: TextView,
    model: EventTaskUI,
) {
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
    textview.text = "$strDate $strTime"
}
@BindingAdapter(
    "app:strikeEventText"
)
fun setStrikeEventText(
    textview: TextView,
    eventTaskUI: EventTaskUI,
) {
    if (eventTaskUI.isCompleted) {
        textview.text = eventTaskUI.name
        textview.paintFlags = textview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textview.text = eventTaskUI.name
        textview.paintFlags = textview.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("subtaskMode")
fun setModeSubtask(imageView: ImageView, isDelete: Boolean) {
    if (isDelete) {
        imageView.setImageResource(R.drawable.ic_delete_subtask)
    } else {
        imageView.setImageResource(R.drawable.ic_menu_grey)
    }
}

@BindingAdapter("contentSubTask")
fun setContentSubtask(edContent: EditText, isTick: Boolean) {
    if (isTick) {
        edContent.paintFlags = edContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        edContent.paintFlags = edContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("imageSubTask")
fun setStatusSubtask(imageView: ImageView, isDelete: Boolean) {
    if (isDelete) {
        imageView.setImageResource(R.drawable.checked)
    } else {
        imageView.setImageResource(R.drawable.unchecked)
    }
}

@BindingAdapter("uriGlide")
fun loadPhoto(imageView: ImageView, uri: Uri?) {
    imageView.load(uri)
}