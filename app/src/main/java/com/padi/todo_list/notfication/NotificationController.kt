package com.padi.todo_list.notfication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.extension.formatLongToTime
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.common.utils.UtilsJ.getPendingIntentNotificationGoToMain
import com.padi.todo_list.data.local.model.AlarmModel
import timber.log.Timber

object NotificationController {
    const val ID_PIN_REMINDER = -1011
    const val ID_TODO_REMIND = -1010
    const val KEY_ID_NOTIFICATION = "KEY_ID_NOTIFICATION"

    private val mNotificationManager: NotificationManager =
        Application.getInstance().getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    fun push(id: Int, notification: Notification) {
        mNotificationManager.notify(id, notification)
    }

    fun cancel(id: Int) {
        mNotificationManager.cancel(id)
    }

    fun clearAll() {
        mNotificationManager.cancelAll()
    }

    fun createNotificationChannel() {
        val channels = mutableListOf<NotificationChannel>()
        NotificationChannels.entries.forEach {
            channels.add(it.createChannel())
        }
        mNotificationManager.createNotificationChannels(channels)
    }

    fun notifyEventTask(context: Context, alarmModels: List<AlarmModel>) {

        alarmModels.forEach { model ->
            mNotificationManager.cancel(model.id.toInt())
            mNotificationManager.notify(model.id.toInt(), getNotification(context, model).build())
        }

    }

    private fun getNotification(
        context: Context,
        alarmModel: AlarmModel,
    ): NotificationCompat.Builder {
        Timber.d("CHECK-Notify: getNotification")

        return NotificationCompat.Builder(
            context, NotificationChannels.NOTIFY.channelID
        ).setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setVibrate(longArrayOf(0, 100))
            .setDefaults(Notification.DEFAULT_SOUND)
            .setSilent(false)
            .setContentTitle(alarmModel.name)
            .setContentText((alarmModel.exactTime).formatLongToTime(DateTimeFormat.DATE_TIME_FORMAT_1))
            .setContentIntent(getPendingIntentNotificationGoToMain(context, alarmModel.id))
    }

}