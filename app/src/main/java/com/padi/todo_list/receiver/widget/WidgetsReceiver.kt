package com.padi.todo_list.receiver.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.common.extension.parcelableArrayList
import com.padi.todo_list.receiver.widget.WidgetStandard.Companion.WIDGET_STANDARD_DATA
import com.padi.todo_list.ui.task.model.EventTaskUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WidgetsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ADD_WIDGET_SUCCESS = "ACTION_ADD_WIDGET_SUCCESS"
        private const val BROADCAST_ID = 1121212
        fun getPendingIntent(context: Context, data: ArrayList<EventTaskUI>): PendingIntent {
            val bundle = Bundle().apply {
                putParcelableArrayList(WIDGET_STANDARD_DATA, data)
            }
            val callbackIntent = Intent(context, WidgetsReceiver::class.java).apply {
                putExtra(WIDGET_STANDARD_DATA, bundle)
            }

            Timber.d("WidgetStandard - getPendingIntent data  ${data.size}")
            return PendingIntent.getBroadcast(
                context,
                BROADCAST_ID,
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        Timber.d("WidgetStandard - onReceive start $widgetId")
        if (widgetId == -1) {
            return
        } else {
            handlePinWidgetSuccess(context)
            val bundle = intent.getBundleExtra(WIDGET_STANDARD_DATA)
            val inputData: ArrayList<EventTaskUI>? =
                bundle?.parcelableArrayList(WIDGET_STANDARD_DATA)
            Timber.d("WidgetStandard - onReceive data ${inputData?.size}")
            WidgetStandard.forceUpdateAll(context, inputData ?: ArrayList())
        }
    }


    private fun handlePinWidgetSuccess(context: Context) {
        Intent().also {
            it.action = ACTION_ADD_WIDGET_SUCCESS
            LocalBroadcastManager.getInstance(context).sendBroadcast(it)
        }
    }

}