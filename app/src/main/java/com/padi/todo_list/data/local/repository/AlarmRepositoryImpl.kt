package com.padi.todo_list.data.local.repository

import com.padi.todo_list.common.utils.AlarmUtils
import com.padi.todo_list.common.utils.RepeatConstants
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.database.dao.ReminderTimeDao
import com.padi.todo_list.data.local.model.AlarmList
import com.padi.todo_list.data.local.model.AlarmModel
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min

class AlarmRepositoryImpl @Inject constructor(
    private val eventTaskDao: EventTaskDao,
    private val reminderTimeDao: ReminderTimeDao,
) : AlarmRepository {
    override fun getNextAlarm(): Maybe<AlarmList> {
        val nowCalendar = Calendar.getInstance()
        val eventTaskMaybe = eventTaskDao.getAllEventTasks()
        val reminderTimeMaybe = reminderTimeDao.getAllReminderTime()

        return Maybe.zip(eventTaskMaybe, reminderTimeMaybe) { eventTasks, reminderTimes ->
            Pair(eventTasks, reminderTimes)
        }.map { (eventTasks, reminderTimes) ->
           handleAlarmList(eventTasks,reminderTimes, nowCalendar)
        }
    }

    private fun getRemindTime(taskModel: EventTaskEntity, reminderModel: ReminderTimeEntity): Long {
        return if (
            taskModel.repeat == RepeatConstants.NO_REPEAT &&
            reminderModel.remindTime > Calendar.getInstance().timeInMillis
        ) {
            reminderModel.remindTime
        } else if (taskModel.repeat != RepeatConstants.NO_REPEAT) {
            (taskModel.nextAlarm - reminderModel.offsetTime)
        } else {
            IGNORE_NUMBER
        }
    }

    companion object{
        private const val IGNORE_NUMBER = -111L
    }

    private fun handleAlarmList(
        eventTasks: List<EventTaskEntity>,
        reminderTimes: List<ReminderTimeEntity>,
        nowCalendar: Calendar
    ) :AlarmList{
        if (eventTasks.isNotEmpty()) {

            val nextEventList = AlarmUtils.getNextEventList(eventTasks.filter { it.hasRemind && !it.isCompleted})
            val nextRemindTimeList = reminderTimes
//                    reminderTimes.filter { it.remindTime > nowCalendar.timeInMillis }

            Timber.d("CHECK-Notify - AlarmRepo - nextEventList ${nextEventList.size}")
            Timber.d("CHECK-Notify - AlarmRepo - nextRemindTimeList ${nextRemindTimeList.size}")

            if (nextRemindTimeList.isNotEmpty()) {
                nextRemindTimeList.forEach { reminderTime ->
                    val matchingEventTask =
                        nextEventList.find { it.id == reminderTime.eventTaskId}

                    matchingEventTask?.let { eventTask ->
                        val newEventTask = eventTask.copy(
                            remindTime = getRemindTime(eventTask, reminderTime),
                            nextAlarm = eventTask.nextAlarm,
                            isRemindModel = true
                        )
                        if (newEventTask.remindTime != IGNORE_NUMBER) {
                            nextEventList.add(newEventTask)
                        }
                    }
                }
            }
/**        group all tasks with the same 'trigger time' -> start    */
//                model with 'isRemindModel == false'
            val listByNextAlarm =
                nextEventList.filter { !it.isRemindModel }.sortedBy { it.nextAlarm }

//                model with 'isRemindModel == true'
            val listByRemindTime =
                nextEventList.filter { eventTaskEntity -> eventTaskEntity.remindTime > nowCalendar.timeInMillis }
                    .sortedBy { it.remindTime }

//                Ignore notifications with exact time if there is a reminder notification type
            nextEventList.removeIf {
                it.hasRemind && !it.isRemindModel
            }

            val topNextAlarmTime: Long =
                if (listByRemindTime.isNotEmpty() && listByRemindTime[0].remindTime > nowCalendar.timeInMillis) {
                    min(
                        listByNextAlarm[0].nextAlarm, listByRemindTime[0].remindTime
                    )
                } else {
                    listByNextAlarm[0].nextAlarm
                }

            val sameTimeAlarmList: List<EventTaskEntity> =
                nextEventList.filter { eventTask: EventTaskEntity ->
                    eventTask.nextAlarm == topNextAlarmTime || eventTask.remindTime == topNextAlarmTime
                }

            val alarmList = AlarmList(topNextAlarmTime)

            sameTimeAlarmList.forEach { eventTask ->
                alarmList.alarmModels.add(
                    AlarmModel(
                        id = eventTask.id,
                        exactTime = eventTask.dueDate!!,
                        remindTime = eventTask.remindTime,
                        name = eventTask.name!!
                    )
                )
            }
/**         group all tasks with the same 'trigger time' -> end     */
            Timber.d("CHECK-Notify - AlarmRepo - alarmList.time: ${alarmList.time}  alarmList.list: ${alarmList.alarmModels.size}")

            return alarmList
        } else {
            return AlarmList(IGNORE_NUMBER)
        }
    }

}
