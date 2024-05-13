package com.padi.todo_list.ui.task.model

import com.padi.todo_list.common.utils.TimeFormatValue
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.prefs.AppPrefs
import timber.log.Timber
import java.io.Serializable
import java.util.Calendar

data class ClockModel(
    var hour: Int = NO_HOUR,
    var minute: Int = NO_HOUR,
    var dayOfMonth: Int,
    var month: Int,
    var year: Int,
) : Serializable {
    companion object {
        const val NO_HOUR = -1
        fun getInstance(): ClockModel {
            val calendar = Calendar.getInstance()
            return ClockModel(
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH) + 1,
                year = calendar.get(Calendar.YEAR)
            )
        }
    }
}