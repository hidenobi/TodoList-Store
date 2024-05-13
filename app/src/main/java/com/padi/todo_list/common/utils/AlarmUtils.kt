package com.padi.todo_list.common.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.gson.Gson
import com.padi.todo_list.common.utils.RepeatConstants.DAILY
import com.padi.todo_list.common.utils.RepeatConstants.HOURLY
import com.padi.todo_list.common.utils.RepeatConstants.MONTHLY
import com.padi.todo_list.common.utils.RepeatConstants.NO_REPEAT
import com.padi.todo_list.common.utils.RepeatConstants.WEEKLY
import com.padi.todo_list.common.utils.RepeatConstants.YEARLY
import com.padi.todo_list.data.local.model.AlarmList
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.receiver.notify.TaskAlarmReceiver
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


object AlarmUtils {
    fun getNextEventList(originEventList: List<EventTaskEntity>): ArrayList<EventTaskEntity> {
        val calendar: Calendar = Calendar.getInstance()
        val nowCalendar: Calendar = Calendar.getInstance()
        if (originEventList.isNotEmpty()) {
            originEventList.forEach { eventModel ->
                calendar.timeInMillis = eventModel.dueDate!!
                if (eventModel.repeat == NO_REPEAT) {
                    eventModel.nextAlarm = eventModel.dueDate!!
                } else {
                    if (calendar.get(Calendar.YEAR) > nowCalendar.get(Calendar.YEAR) ||
                        calendar.get(Calendar.DAY_OF_YEAR) > nowCalendar.get(Calendar.DAY_OF_YEAR)
                    ) {
                        eventModel.nextAlarm = calendar.timeInMillis
                        Timber.d("CHECK-Notify - check eventModel.nextAlarm 1: ${eventModel.nextAlarm}")
                    } else {
                    when (eventModel.repeat) {
                        HOURLY -> {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR))
                            calendar.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR))
                            calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                            if (calendar.timeInMillis < nowCalendar.timeInMillis) {
                                calendar.add(Calendar.HOUR_OF_DAY, 1)
                            }
                        }
                        DAILY -> {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR))
                            calendar.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR))
                            if (calendar.timeInMillis < nowCalendar.timeInMillis) {
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                            }
                        }
                        WEEKLY -> {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR))
                            calendar.set(Calendar.WEEK_OF_YEAR, nowCalendar.get(Calendar.WEEK_OF_YEAR))
                            if (calendar.timeInMillis < nowCalendar.timeInMillis) {
                                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                            }
                        }
                        MONTHLY -> {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR))
                            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH))
                            if (calendar.timeInMillis < nowCalendar.timeInMillis) {
                                calendar.add(Calendar.MONTH, 1)
                            }
                        }
                        YEARLY -> {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR))
                            if (calendar.timeInMillis < nowCalendar.timeInMillis) {
                                calendar.add(Calendar.YEAR, 1)
                            }
                        }
                    }
                    eventModel.nextAlarm = calendar.timeInMillis
                    Timber.d("CHECK-Notify - check eventModel.nextAlarm 2: ${eventModel.nextAlarm}")
                    }
                }
            }

            val filter1 = originEventList
                .filter { eventModel -> eventModel.repeat == NO_REPEAT && eventModel.nextAlarm > nowCalendar.timeInMillis }

            val filter2 = originEventList
                .filter { eventModel -> eventModel.repeat != NO_REPEAT}

//            val nextEventList = originEventList
//                .filter { eventModel -> eventModel.nextAlarm > nowCalendar.timeInMillis }
            val nextEventList = ArrayList<EventTaskEntity>().apply {
                addAll(filter1)
                addAll(filter2)
            }

            return if (nextEventList.isNotEmpty()) {
                Timber.d("CHECK-Notify - getNextEventList - nextEventList.isNotEmpty: ${nextEventList.size}")
                ArrayList(nextEventList.sortedBy { eventTaskEntity ->  eventTaskEntity.nextAlarm })
            } else {
                Timber.d("CHECK-Notify - getNextEventList - nextEventList empty")
                ArrayList()
            }
        } else {
            Timber.d("CHECK-Notify - getNextEventList - originList empty")
            return ArrayList()
        }
    }

    fun startAlarm(context: Context, alarmList: AlarmList) {
        Timber.d("CHECK-Notify: AlarmUtils setAlarm")
        val alarmReceiverIntent = Intent(context, TaskAlarmReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmList.alarmModels.isEmpty()) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_PENDING_INTENT_CODE,
                alarmReceiverIntent,
                UtilsJ.getPendingIntentFlag()
            )
            alarmManager.cancel(pendingIntent)
            return
        }
        val alarmTime = alarmList.time
        val bundle = Bundle()
        alarmReceiverIntent.setAction(
            String.format(
                Locale.US,
                "%s.%s",
                context.packageName,
                ACTION_ALARM
            )
        )
        val alarmListStr = Gson().toJson(alarmList.alarmModels)
        bundle.putString(ALARM_JSON, alarmListStr)
        bundle.putLong(ALARM_TIME, alarmTime)
        alarmReceiverIntent.putExtras(bundle)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_PENDING_INTENT_CODE,
            alarmReceiverIntent,
            UtilsJ.getPendingIntentFlag()
        )
        alarmManager.cancel(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Timber.d("CHECK-Notify: > android S")
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }
                Timber.d("CHECK-Notify: not canSchedule")
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
                Timber.d("CHECK-Notify: canSchedule")
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
            Timber.d("CHECK-Notify: < Android S")
        }


    }

}