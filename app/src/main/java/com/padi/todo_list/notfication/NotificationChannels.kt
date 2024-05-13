package com.padi.todo_list.notfication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager

enum class NotificationChannels(
    val id: Int,
    val channelID: String,
    val description: String,
    val soundPattern: SoundPattern,
) {
    NOTIFY(1000, "Notify_Channel", "NotifyTask", SoundPattern.Enable),
    TODO_REMIND(1001, "Todo_Remind_Channel", "TodoRemind", SoundPattern.Enable),
    PIN_REMINDER(1010, "Pin_Reminder_Chanel", "PinReminder", SoundPattern.Enable);

    enum class SoundPattern {
        Enable,
        Disable
    }

    fun createChannel(): NotificationChannel = NotificationChannel(
        this.channelID,
        description,
        NotificationManager.IMPORTANCE_HIGH
    ).also {
        when (soundPattern) {
            SoundPattern.Enable -> {
                val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                it.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes
                )
            }

            SoundPattern.Disable -> {
                it.setSound(null, null)
            }
        }
    }
}