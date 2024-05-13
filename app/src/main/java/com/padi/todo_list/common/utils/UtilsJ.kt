package com.padi.todo_list.common.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.padi.todo_list.BuildConfig
import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.extension.changeContextByLanguageCode
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.extension.transformToTaskUI
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.notfication.NotificationController
import com.padi.todo_list.notfication.NotificationController.KEY_ID_NOTIFICATION
import com.padi.todo_list.receiver.notify.NotificationBroadcastReceiver
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.main.MainActivity
import com.padi.todo_list.ui.language.LanguageModel
import com.padi.todo_list.ui.main.CLICK_NOTIFICATION
import com.padi.todo_list.ui.task.enum_class.DateSelectionEnum
import com.padi.todo_list.ui.task.enum_class.RemindUnitEnum
import com.padi.todo_list.ui.task.enum_class.TimeSelectionEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.SubTaskUI
import timber.log.Timber
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue

object UtilsJ {

    fun checkIsToDayType(dueDate: Long): Int {
        val currentDate = LocalDate.now()
        return if (Instant.ofEpochMilli(dueDate).atZone(ZoneId.systemDefault())
                .toLocalDate() < currentDate
        ) {
            PREVIOUS
        } else if (Instant.ofEpochMilli(dueDate).atZone(ZoneId.systemDefault())
                .toLocalDate() == currentDate
        ) {
            TODAY
        } else {
            FUTURE
        }
    }

    fun calculateRemindMillis(
        clockModel: ClockModel, number: Int, unit: RemindUnitEnum
    ): Long {
        val milliDueDate = convertTimeToMilliseconds(
            clockModel
        )

        val inputMillis = calculateOffsetTime(number, unit)

        return milliDueDate - inputMillis
    }

    fun convertTimeToMilliseconds(
        clock: ClockModel
    ): Long {
        val localDateTime =
            LocalDateTime.of(
                clock.year,
                clock.month,
                clock.dayOfMonth,
                if (clock.hour == ClockModel.NO_HOUR) {
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                } else {
                    clock.hour
                },
                if (clock.minute == ClockModel.NO_HOUR) {
                    Calendar.getInstance().get(Calendar.MINUTE)
                } else {
                    clock.minute
                },
            )
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun calculateOffsetTime(number: Int, unit: RemindUnitEnum): Long {
        var result = 1000L
        result *= when (unit) {
            RemindUnitEnum.MINUTE -> {
                60 * number
            }

            RemindUnitEnum.HOUR -> {
                60 * 60 * number
            }

            RemindUnitEnum.DAY -> {
                24 * 60 * 60 * number
            }

            RemindUnitEnum.WEEK -> {
                7 * 24 * 60 * 60 * number
            }
        }
        return result
    }

    fun customRemindText(milliRemind: Long): String {
        val isToday = checkIsToDayType(milliRemind)
        return when (isToday) {
            TODAY -> {
                "${Application.getInstance().applicationContext.getText(R.string.today)} ${
                    milliRemind.formatLongToTime(
                        DateTimeFormat.DATE_TIME_FORMAT_1
                    )
                }"
            }

            else -> {
                milliRemind.formatLongToTime(
                    DateTimeFormat.DATE_TIME_FORMAT_2
                )
            }
        }
    }

    fun getReminderString(dueDate: Long, list: List<OffsetTimeUI>): ArrayList<String> {
        val output = ArrayList<String>()
        list.forEach { offsetTimeUI ->
            var str = ""
            val dateTimeFormat = if (is24HourView(AppPrefs.getTimeFormat())) {
                DateTimeFormat.DATE_TIME_FORMAT_1
            } else {
                DateTimeFormat.DATE_TIME_FORMAT_9
            }
            when (offsetTimeUI.position) {
                0 -> {
                    str =
                        (dueDate - OffSetTimeValue.ZERO).formatLongToTime(dateTimeFormat)
                }

                1 -> {
                    str =
                        (dueDate - OffSetTimeValue.FIVE_MINUTES).formatLongToTime(dateTimeFormat)
                }

                2 -> {
                    str =
                        (dueDate - OffSetTimeValue.TEN_MINUTES).formatLongToTime(dateTimeFormat)
                }

                3 -> {
                    str =
                        (dueDate - OffSetTimeValue.FIFTEEN_MINUTES).formatLongToTime(dateTimeFormat)
                }

                4 -> {
                    str =
                        (dueDate - OffSetTimeValue.THIRTY_MINUTES).formatLongToTime(dateTimeFormat)
                }

                5 -> {
                    str =
                        (dueDate - OffSetTimeValue.ONE_DAY).formatLongToTime(dateTimeFormat)
                }
            }
            output.add(str)
        }
        return output
    }

    fun hideNavigationBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    fun setStatusBar(window: Window, drawableRes: Int, isSetByColor: Boolean = false) {
        if (isSetByColor) {
            window.statusBarColor = window.context.resources.getColor(drawableRes, null)
            window.statusBarColor = window.context.resources.getColor(drawableRes, null)
        } else {
            val background =
                ResourcesCompat.getDrawable(window.context.resources, drawableRes, null)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =
                window.context.resources.getColor(android.R.color.transparent, null)
            window.setBackgroundDrawable(background)
        }
    }

    fun lightStatusBar(window: Window, isLight: Boolean = true) {
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = isLight
    }

    fun getPendingIntentFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

    fun getAddTaskPendingIntent(context: Context): PendingIntent {
        val addTaskIntent = Intent(context, MainActivity::class.java)
        addTaskIntent.action = (ADD_ACTION)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(addTaskIntent)
        return stackBuilder.getPendingIntent(
            System.currentTimeMillis().toInt(),
            getPendingIntentFlag()
        )
    }

    fun getPendingIntentNotificationGoToMain(context: Context, id: Long): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = CLICK_NOTIFICATION
            putExtra(KEY_ID_NOTIFICATION, id)
        }
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent)
        return stackBuilder.getPendingIntent(
            System.currentTimeMillis().toInt(),
            getPendingIntentFlag()
        )
    }

    fun getRemovePendingIntent(
        context: Context,
        id: Int? = NotificationController.ID_PIN_REMINDER,
    ): PendingIntent {
        val removeIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        removeIntent.action = REMOVE_ACTION
        removeIntent.putExtra(NOTIFY_ID, id)
        return PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            removeIntent,
            getPendingIntentFlag()
        )
    }


    fun canShowAddWidgetDirectly(): Boolean {
        val deviceManufacturer = Build.MANUFACTURER.lowercase()
        val unSupportList = listOf("vivo", "xiaomi", "huawei", "realme", "oppo")
        return !unSupportList.contains(deviceManufacturer)
    }
    fun getListLanguage(context: Context): MutableList<LanguageModel> {
        val list = createListLanguage(context)
        moveCurrentLanguageToFirst(list)
        return list
    }

    @SuppressLint("Recycle")
    private fun createListLanguage(context: Context): MutableList<LanguageModel> {
        val list = mutableListOf<LanguageModel>()
        val arrFlag = context.resources.obtainTypedArray(R.array.array_flag_language)
        val arrCode = context.resources.getStringArray(R.array.array_language_code)
        context.resources.getStringArray(R.array.array_country).mapIndexed { index, s ->
            list.add(
                LanguageModel(
                    id = index,
                    src = arrFlag.getResourceId(index, 0),
                    country = s,
                    arrCode[index],
                )
            )
        }
        return list
    }

    private fun moveCurrentLanguageToFirst(list: MutableList<LanguageModel>) {
        //find and set element selected
        val indexContain = list.indexOfFirst { it.codeLang ==  AppPrefs.languageCode }
        list[indexContain].isSelected = true

        //move language selected to first
        val element = list.removeAt(indexContain)
        list.add(0, element)
    }

    fun getRemoteWidgetPreview(context: Context, typeWidget: String): RemoteViews? {
        return when (typeWidget) {
            WidgetStandard.NAME -> {
                RemoteViews(context.packageName, R.layout.layout_prev_widget_standard)
            }

            else -> {
                null
            }
        }
    }

    fun getProvider(context: Context): ComponentName {
        return ComponentName(context, WidgetStandard::class.java)
    }

    fun getStartAndEndOfWeek(
        weekNumberInFront: Int,
    ): List<LocalDateTime> {
        val today = LocalDateTime.now()
        val startOfWeek = LocalDateTime.of(today.toLocalDate().with(DayOfWeek.MONDAY).minusWeeks(weekNumberInFront.toLong()), LocalTime.MIN)
        val endOfWeek = LocalDateTime.of(startOfWeek.toLocalDate().plusDays(6),LocalTime.MAX)

        val daysOfWeek = arrayListOf<LocalDateTime>()
        var currentDay = startOfWeek
        while (currentDay <= endOfWeek) {
            daysOfWeek.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }
        return daysOfWeek
    }

    fun localDateTimeToMillis(dateTime: LocalDateTime?): Long {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()?:0
    }

    fun getDaysOfWeek(): List<String> {
        val daysOfWeek = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.Builder().setLanguageTag(AppPrefs.languageCode).build())
        val today = LocalDate.now()
        for (i in 1..7) {
            val day = today.plusDays((i - today.dayOfWeek.value).toLong())
            daysOfWeek.add(formatter.format(day))
        }
        return daysOfWeek
    }
    fun getListOfDays(numberOfDays: Int): List<LocalDateTime> {
        val step = if (numberOfDays > 0) 1 else -1
        val startDay = LocalDate.now().plusDays(step.toLong())
        return generateSequence(startDay) {
            it.plusDays(step.toLong())
        }
            .take(numberOfDays.absoluteValue + 1)
            .map { date ->
                when (date) {
                    startDay -> date.atTime(LocalTime.MIN)
                    LocalDate.now().plusDays(numberOfDays.toLong()) -> date.atTime(LocalTime.MAX)
                    else -> date.atStartOfDay()
                }
            }
            .toList()
    }

    fun getViewPointOnScr(windowManager: WindowManager): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = windowManager.currentWindowMetrics.windowInsets
            var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
            windowInsets.displayCutout?.run {
                insets = Insets.max(
                    insets,
                    Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
                )
            }
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            Point(
                windowManager.currentWindowMetrics.bounds.width() - insetsWidth,
                windowManager.currentWindowMetrics.bounds.height() - insetsHeight
            )
        } else {
            Point().apply {
                windowManager.defaultDisplay.getSize(this)
            }
        }
    }

    fun Long.toClockModel(): ClockModel {
        val instant = Instant.ofEpochMilli(this)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val year = localDateTime.year
        val month = localDateTime.monthValue
        val day = localDateTime.dayOfMonth
        val hour = localDateTime.hour
        val minute = localDateTime.minute
        return ClockModel(hour, minute, day, month, year)
    }

    fun Long.toDateSelectionEnum(): DateSelectionEnum {
        val millis = this
        val currentDate = LocalDate.now()
        val targetDate = LocalDate.ofEpochDay(millis / 86400000)

        val tomorrowCalendar = currentDate.plusDays(1)
        val threeDaysLaterCalendar = currentDate.plusDays(3)
        val lastDayOfWeek = currentDate.with(DayOfWeek.SUNDAY)

        return when {
            targetDate.isEqual(currentDate) -> DateSelectionEnum.TODAY
            targetDate.isEqual(tomorrowCalendar) -> DateSelectionEnum.TOMORROW
            targetDate.isEqual(threeDaysLaterCalendar) -> DateSelectionEnum.NEXT_3_DAYS
            targetDate.isEqual(lastDayOfWeek) -> DateSelectionEnum.THIS_SUNDAY
            else -> DateSelectionEnum.IDLE
        }
    }

    fun Long.toTimeSelectionEnum(): TimeSelectionEnum {
        val instant = Instant.ofEpochMilli(this)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val hour = localDateTime.hour
        val minute = localDateTime.minute
        if (minute != 0) {
            return TimeSelectionEnum.ANOTHER
        } else {
            return when (hour) {
                7 -> {
                    TimeSelectionEnum._7_HOUR
                }

                9 -> {
                    TimeSelectionEnum._9_HOUR
                }

                10 -> {
                    TimeSelectionEnum._10_HOUR
                }

                12 -> {
                    TimeSelectionEnum._12_HOUR
                }

                14 -> {
                    TimeSelectionEnum._14_HOUR
                }

                16 -> {
                    TimeSelectionEnum._16_HOUR
                }

                18 -> {
                    TimeSelectionEnum._18_HOUR
                }

                else -> {
                    TimeSelectionEnum.ANOTHER
                }
            }
        }
    }

    fun isToday(millis: Long): Boolean {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate() == LocalDate.now()
    }

    fun isToday(clockModel: ClockModel): Boolean {
        return LocalDate.of(
            clockModel.year,
            clockModel.month,
            clockModel.dayOfMonth
        ) == LocalDate.now()
    }

    fun isNoHour(clockModel: ClockModel): Boolean {
        return clockModel.hour == ClockModel.NO_HOUR || clockModel.minute == ClockModel.NO_HOUR
    }

    fun getDateTimeTaskValue(value: ClockModel): Int {
        val dayValue = if (isToday(value)) 1 else 0
        val hourValue = if (isNoHour(value)) 1 else 0
        return when (dayValue) {
            1 -> {
                if (hourValue == 1) {
                    TimeStatusTask.NO_TIME
                } else {
                    TimeStatusTask.HAS_TIME
                }
            }

            else -> {
                if (hourValue == 1) {
                    TimeStatusTask.NO_TIME
                } else {
                    TimeStatusTask.HAS_TIME
                }
            }
        }
    }

    fun getTimeSelectionEnumInput(input: EventTaskEntity): TimeSelectionEnum {
        return if (input.timeStatus == TimeStatusTask.NO_TIME) {
            TimeSelectionEnum.NO_TIME
        } else {
            input.dueDate!!.toTimeSelectionEnum()
        }
    }

    fun getDateSelectionEnumInput(input: EventTaskEntity): DateSelectionEnum {
        return if (
            (input.dateStatus == DateStatusTask.NO_DATE)
        ) {
            DateSelectionEnum.NO_DATE
        } else if (
            isToday(input.dueDate!!) &&
            input.dateStatus == DateStatusTask.HAS_DATE
        ) {
            DateSelectionEnum.TODAY
        } else {
            input.dueDate!!.toDateSelectionEnum()
        }
    }

    fun getClockModelInput(input: EventTaskEntity): ClockModel {
        return if (input.dateStatus == DateStatusTask.NO_DATE) {
            ClockModel.getInstance()
        } else {
            input.dueDate!!.toClockModel()
        }
    }

    fun getPrevData(
        taskScope: Int,
        categoryScope: Long,
        showCompleted: Boolean,
        output: List<EventTaskEntity>
    ): List<EventTaskUI> {
        val temp = ArrayList<EventTaskEntity>()
        temp.addAll(output)

        if (taskScope == ScopeTask.TODAY) {
            temp.removeIf {
                Instant.ofEpochMilli(it.dueDate!!).atZone(ZoneId.systemDefault())
                    .toLocalDate() != LocalDate.now()
            }
        }

        if (categoryScope != CategoryConstants.NO_CATEGORY_ID) {
            temp.removeIf {
                it.categoryId != categoryScope
            }
        }

        if (!showCompleted) {
            temp.removeIf {
                it.isCompleted
            }
        }

        return if (temp.isEmpty()) {
            ArrayList()
        } else {
            temp.transformToTaskUI(true).toList()
        }
    }

    fun createFormattedString(subtasks: List<SubTaskUI>): String {
        return buildString {
            for (subtask in subtasks) {
                if (!subtask.isDone) {
                    append("\u2022 ${subtask.content}\n")
                }
            }
        }
    }
    fun shareTask(context: Context, eventTask: EventTaskEntity, subtasks: List<SubTaskUI>) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            val shareMessage = buildString {
                appendLine(
                    "${eventTask.name} ${eventTask.dueDate!!.formatLongToTime(DateTimeFormat.DATE_TIME_FORMAT_2)} ${
                            getRepeatName(eventTask.repeat)
                    }"
                )
                if (subtasks.isNotEmpty()) {
                    appendLine(createFormattedString(subtasks))
                }
                appendLine("\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}\n\n")
            }
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, ""))
        } catch (e: Exception) {
            Timber.e("Share Error: $e")
        }
    }
    private fun getRepeatName(position: Int): String {
        return when (position) {
            1 -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.hour) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }

            2 -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.daily) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }

            3 -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.weekly) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }

            4 -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.monthly) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }

            5 -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.yearly) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }

            else -> {
                Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.no) + " " + Application.getInstance().applicationContext.changeContextByLanguageCode()
                    .getString(R.string.repeat)
            }
        }
    }
    fun isSystemFormat24(): Boolean {
        val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        val formattedTime = dateFormat.format(System.currentTimeMillis())
        return !formattedTime.contains(Regex("AM|PM", RegexOption.IGNORE_CASE))
    }
    fun is24HourView(timeFormat: Int): Boolean {
        return when (timeFormat) {
            TimeFormatValue.FORMAT_24 -> {
                true
            }

            TimeFormatValue.FORMAT_12 -> {
                false
            }

            else -> {
                isSystemFormat24()
            }
        }
    }

}