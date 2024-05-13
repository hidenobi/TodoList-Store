package com.padi.todo_list.receiver.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.padi.todo_list.common.utils.NOTIFY_ID
import com.padi.todo_list.common.utils.REMOVE_ACTION
import com.padi.todo_list.notfication.NotificationController

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            REMOVE_ACTION -> {
                val notifyID =
                    intent.extras?.getInt(NOTIFY_ID, NotificationController.ID_PIN_REMINDER)
                notifyID?.let {
                    NotificationController.cancel(it)
                }
            }
        }
    }

}