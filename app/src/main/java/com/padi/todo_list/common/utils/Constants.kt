package com.padi.todo_list.common.utils

import androidx.core.content.ContextCompat
import com.padi.todo_list.R
import com.padi.todo_list.common.Application


const val PREVIOUS = 1
const val TODAY = 2
const val FUTURE = 3
const val COMPLETED = 4
const val ALARM_PENDING_INTENT_CODE = 121234
const val ACTION_ALARM = "ACTION_ALARM"
const val ALARM_TIME = "ALARM_TIME"
const val ALARM_JSON = "ALARM_JSON"
const val ADD_ACTION = "ADD_ACTION"
const val REMOVE_ACTION = "REMOVE_ACTION"
const val NOTIFY_ID = "NOTIFY_ID"
const val ACTION_NOTIFY_PER = "ACTION_NOTIFY_PER"
const val ACTION_OVERLAY_PER = "ACTION_OVERLAY_PER"
const val LINK_POLICY = "https://ourdreamy.com/policy/"



object ChipDate {
    const val CHIP_TODAY = 0
    const val CHIP_TOMORROW = 1
    const val CHIP_3_DAY_LATER = 3

}

enum class DateTimeFormat(val value: String) {
    DATE_TIME_FORMAT_1("HH:mm"), //24h
    DATE_TIME_FORMAT_2("yyyy/MM/dd HH:mm"),
    DATE_TIME_FORMAT_3("dd-MM"),
    DATE_TIME_FORMAT_4("MM-dd"),
    DATE_TIME_FORMAT_6("yyyy/MM/dd"),
    DATE_TIME_FORMAT_7("dd/MM/yyyy"),
    DATE_TIME_FORMAT_16("dd-MM-yyyy"),
    DATE_TIME_FORMAT_8("MM/dd/yyyy"),
    DATE_TIME_FORMAT_17("MM-dd-yyyy"),
    DATE_TIME_FORMAT_9("hh:mm a"), //12h
    DATE_TIME_FORMAT_11("MM/dd"),
    DATE_TIME_FORMAT_12("MM/dd HH:mm"),
    DATE_TIME_FORMAT_14("MM-dd hh:mm a"),
    DATE_TIME_FORMAT_15("dd/MM"),

}

object BundleKey {
    const val KEY_MODE_CREATE_NEW = "KEY_MODE_CREATE_NEW"
    const val KEY_CATEGORY_ID = "KEY_CATEGORY_ID"
    const val KEY_TYPE_TIME = "KEY_TYPE_TIME"
    const val POS_X = "POS_X"
    const val POS_Y = "POS_Y"

    const val KEY_CLOCK_MODEL = "KEY_CLOCK_MODEL"
    const val KEY_EVENT_TASK = "KEY_EVENT_TASK"

    const val KEY_REMIND_TIME_UI = "KEY_REMIND_TIME_UI"
    const val KEY_REPEAT_OPTION = "KEY_REPEAT_OPTION"
    const val KEY_LIST_REMIND_TIME_UI = "KEY_REMIND_TIME_UI"

    const val IS_REMIND = "IS_REMIND"
    const val IS_REPEAT = "IS_REMIND"
    const val OPTION_AZ = "OPTION_AZ"
    const val OPTION_ZA = "OPTION_ZA"
    const val OPTION_DUE_DATE = "OPTION_DUE_DATE"
    const val YEAR_MONTH_DAY = "YEAR_MONTH_DAY"
    const val DAY_MONTH_YEAR = "DAY_MONTH_YEAR"
    const val MONTH_DAY_YEAR = "MONTH_DAY_YEAR"


}

object NumberPickerKey {
    const val MIN_NUMBER_VALUE = 1
    const val DEFAULT_NUMBER_VALUE = 1
    const val MAX_NUMBER_VALUE = 99

    const val MIN_UNIT_VALUE = 0
    const val DEFAULT_UNIT_VALUE = 1
    const val MAX_UNIT_VALUE = 3
}

object OffSetTimeValue {
    const val ZERO = 0L
    const val FIVE_MINUTES = 5 * 60 * 1000L
    const val TEN_MINUTES = 10 * 60 * 1000L
    const val FIFTEEN_MINUTES = 15 * 60 * 1000L
    const val THIRTY_MINUTES = 30 * 60 * 1000L
    const val ONE_DAY = 24 * 60 * 60 * 1000L
    const val CUSTOM = -1L
}

object RepeatConstants {
    const val NO_REPEAT = 0
    const val HOURLY = 1
    const val DAILY = 2
    const val WEEKLY = 3
    const val MONTHLY = 4
    const val YEARLY = 5
}

object CategoryConstants {
    const val NO_CATEGORY_ID = -1L
    const val CREATE_NEW_ID = -2L

}
object IntentConstants {

    const val position = "position"
    const val idTask = "idTask"
    const val titleNote = "titleNote"
    const val usedCreatedNote = "usedCreatedNote"
    const val item = "item"
    const val dueDate = "dueDate"
    const val splashData = "splashData"
    const val isComplete = "isComplete"

}
object SenMail{
    const val SUPPORT_EMAIL = "abc@paditech.com"
}

object ScopeTask {
    const val ALL = 1
    const val TODAY = 2
}

val COLOR_PIE_CHART = intArrayOf(
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_1),
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_2),
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_3),
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_4),
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_5),
    ContextCompat.getColor(Application.getInstance(), R.color.color_pie_7),
)

object TimeStatusTask {
    const val NO_TIME = 1
    const val HAS_TIME = 2
}

object DateStatusTask {
    const val NO_DATE = 1
    const val HAS_DATE = 2
}

object TimeFormatValue {
    const val FORMAT_24 = 1
    const val FORMAT_12 = 2
    const val FORMAT_BY_SYSTEM = 3

}