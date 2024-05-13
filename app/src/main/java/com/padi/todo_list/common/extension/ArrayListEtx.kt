package com.padi.todo_list.common.extension

import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.COMPLETED
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.FUTURE
import com.padi.todo_list.common.utils.PREVIOUS
import com.padi.todo_list.common.utils.TODAY
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.checkIsToDayType
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import com.padi.todo_list.data.local.model.SubtaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.SubTaskUI
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ArrayList<EventTaskEntity>.transformToTaskUI(showInWidget: Boolean? = false): ArrayList<EventTaskUI> {
    val input = ArrayList<EventTaskEntity>()
    input.addAll(this)
    input.sortBy { it.dueDate }

    val pastItems = ArrayList<EventTaskUI>()
    val todayItems = ArrayList<EventTaskUI>()
    val futureItems = ArrayList<EventTaskUI>()
    val completeItems = ArrayList<EventTaskUI>()

    val filteredInput = input.filter { !it.isCompleted && it.dateStatus == DateStatusTask.HAS_DATE}
    for (item in filteredInput) {
        when (checkIsToDayType(item.dueDate!!)) {
            PREVIOUS -> {
                pastItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = PREVIOUS))
            }

            TODAY -> {
                todayItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = TODAY))
            }

            else -> {
                futureItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = FUTURE))
            }
        }

    }
    for (item in input.filter {it.dateStatus == DateStatusTask.HAS_DATE}) {
        if (item.isCompleted) {
            completeItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = COMPLETED))
        }
    }

    val filteredNoDateInput = input.filter { it.dateStatus == DateStatusTask.NO_DATE }

    if (filteredNoDateInput.isNotEmpty()) {
        for (item in filteredNoDateInput) {
            if (!item.isCompleted) {
                futureItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = FUTURE))
            } else {
                completeItems.add(EventTaskUI.newInstance(item, isTitle = false, typeTask = COMPLETED))
            }
        }
    }

    val output = ArrayList<EventTaskUI>()

    if (pastItems.isNotEmpty()) {
        val result = pastItems.arrangeList()
        val titlePast = EventTaskUI(
            name = Application.getInstance().applicationContext.setUpLanguage().getString(R.string.previous),
            dueDate = result[0].dueDate,
            isTitle = true,
            typeTask = PREVIOUS
        )
        (result as ArrayList).add(0, titlePast)
        output.addAll(result)
    }
    if (todayItems.isNotEmpty()) {
        val result = todayItems.arrangeList()
        val titleToday = EventTaskUI(
            name = Application.getInstance().applicationContext.setUpLanguage().getString(R.string.today),
            dueDate = result[0].dueDate,
            isTitle = true,
            typeTask = TODAY
        )
        (result as ArrayList).add(0, titleToday)
        output.addAll(result)
    }
    if (futureItems.isNotEmpty()) {
        val result = futureItems.arrangeList()
        val titleFuture =
            EventTaskUI(
                name = Application.getInstance().applicationContext.setUpLanguage().getString(R.string.future),
                dueDate = result[0].dueDate,
                isTitle = true,
                typeTask = FUTURE
            )
        (result as ArrayList).add(0, titleFuture)
        output.addAll(result)
    }

    if (completeItems.isNotEmpty()) {
        val result = completeItems.arrangeList()
        val titleComplete = EventTaskUI(
            name = if (showInWidget!!) {
                Application.getInstance().applicationContext.setUpLanguage().getString(R.string.completed)
            } else {
                Application.getInstance().applicationContext.setUpLanguage().getString(R.string.complete_today)
            }, isTitle = true, typeTask = COMPLETED
        )
        (result as ArrayList).add(0, titleComplete)
        output.addAll(result)
    }
    if (!showInWidget!!) {
        output.filter {
            it.dateComplete != -1L && UtilsJ.isToday(it.dateComplete)
        }
    }
    return output
}

private fun ArrayList<EventTaskUI>.arrangeList(): List<EventTaskUI> {
    val result = ArrayList<EventTaskUI>()
    if (AppPrefs.getArrangeTaskOption().equals(BundleKey.OPTION_AZ)) {
        result.addAll(this.sortedBy { it.name })
    } else if (AppPrefs.getArrangeTaskOption().equals(BundleKey.OPTION_ZA)) {
        result.addAll(this.sortedByDescending { it.name })
    } else {
        result.addAll(this.sortedBy { it.dueDate })
    }
    return result
}

fun isDifferentDay(previousDate: Long, currentDate: Long): Boolean {
    val previousDay =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(previousDate), ZoneId.systemDefault())
            .toLocalDate()
    val currentDay =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(currentDate), ZoneId.systemDefault())
            .toLocalDate()
    return previousDay != currentDay
}

fun ArrayList<OffsetTimeUI>.transformToListReminderTime(
    eventTaskId: Long, dueDate: Long
): ArrayList<ReminderTimeEntity> {
    val input = this.filter { model -> model.isChecked }
    val output = ArrayList<ReminderTimeEntity>()
    return if (input.isNotEmpty()) {
        input.forEach { item ->
            output.add(
                ReminderTimeEntity(
                    eventTaskId = eventTaskId,
                    remindTime = (dueDate - item.offsetTime),
                    offsetTime = item.offsetTime,
                )
            )
        }
        output
    } else {
        ArrayList()
    }
}

fun ArrayList<SubTaskUI>.transformToListSubtask(
    eventTaskId: Long,
): ArrayList<SubtaskEntity> {
    val input = this.filter { item -> item.content.isNotEmpty() }
    val output = ArrayList<SubtaskEntity>()
    val list = input.mapIndexed { index, item ->
        SubtaskEntity(
            id = item.id,
            taskId = eventTaskId,
            completed = item.isDone,
            name = item.content,
            order = index
        )
    }
    output.addAll(list)
    return output
}


fun List<SubTaskUI>.transformToListSubtask(
    eventTaskId: Long,
): List<SubtaskEntity> {
    return this.filter { item -> item.content.isNotEmpty() }.mapIndexed { index, item ->
        SubtaskEntity(
            id = item.id,
            taskId = eventTaskId,
            completed = item.isDone,
            name = item.content,
            order = index
        )
    }
}
