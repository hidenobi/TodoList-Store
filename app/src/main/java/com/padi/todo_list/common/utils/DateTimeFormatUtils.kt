package com.padi.todo_list.common.utils

import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.ui.task.model.ClockModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

object DateTimeFormatUtils {
    fun getTimeFormatValue(isFullFormat: Boolean = false): DateTimeFormat {
        val timeFormat = AppPrefs.getTimeFormat()
        return when (timeFormat) {
            TimeFormatValue.FORMAT_24 -> if (isFullFormat) DateTimeFormat.DATE_TIME_FORMAT_12 else DateTimeFormat.DATE_TIME_FORMAT_1
            TimeFormatValue.FORMAT_12 -> if (isFullFormat) DateTimeFormat.DATE_TIME_FORMAT_14 else DateTimeFormat.DATE_TIME_FORMAT_9
            else -> if (UtilsJ.isSystemFormat24()) {
                if (isFullFormat) DateTimeFormat.DATE_TIME_FORMAT_12 else DateTimeFormat.DATE_TIME_FORMAT_1
            } else {
                if (isFullFormat) DateTimeFormat.DATE_TIME_FORMAT_14 else DateTimeFormat.DATE_TIME_FORMAT_9
            }
        }
    }

    fun getDateFormatValue(dueDate: Long): DateTimeFormat {
        val currentYear = LocalDateTime.now().year
        val dueDateYear = LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault()).year
        return if (AppPrefs.getOptionDay() == BundleKey.DAY_MONTH_YEAR) {
//            dd-MM
            if (dueDateYear > currentYear){
                DateTimeFormat.DATE_TIME_FORMAT_16
            }else {
                DateTimeFormat.DATE_TIME_FORMAT_3
            }
        } else {
//            MM-dd
            if (dueDateYear > currentYear){
                DateTimeFormat.DATE_TIME_FORMAT_17
            }else {
                DateTimeFormat.DATE_TIME_FORMAT_4
            }

        }
    }
    fun convertClockModelToTime(clockModel: ClockModel): String {
        val timeFormat = when {
            AppPrefs.getTimeFormat() == TimeFormatValue.FORMAT_24 ||
            (AppPrefs.getTimeFormat() == TimeFormatValue.FORMAT_BY_SYSTEM && UtilsJ.isSystemFormat24()) -> {
                "%02d:%02d"
            }

            AppPrefs.getTimeFormat() == TimeFormatValue.FORMAT_12 ||
            (AppPrefs.getTimeFormat() == TimeFormatValue.FORMAT_BY_SYSTEM && !UtilsJ.isSystemFormat24()) -> {
                val period = if (clockModel.hour < 12) "AM" else "PM"
                val hour12 =
                    if (clockModel.hour == 0 || clockModel.hour == 12) 12 else clockModel.hour % 12
                "%02d:%02d %s".format(hour12, clockModel.minute, period)
            }

            else -> "%02d:%02d"
        }
        return timeFormat.format(clockModel.hour, clockModel.minute)
    }

}