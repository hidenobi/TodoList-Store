package com.padi.todo_list.receiver.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.parcelable
import com.padi.todo_list.common.extension.setOnClickPendingIntent
import com.padi.todo_list.common.extension.setRemoteAdapter
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.ui.main.MainActivity
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.widget_setting.WidgetSettingActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WidgetStandard : AppWidgetProvider() {
    @Inject
    lateinit var widgetSettingRepo: WidgetSettingRepositoryImpl
    private val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {

        Timber.d("WidgetStandard: onUpdate")
        // There may be multiple widgets active, so update all of them
        callRepository(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        markDoneTask(context!!, intent!!)
    }

    companion object {

        const val NAME = "WidgetStandard"
        const val WIDGET_STANDARD_DATA = "WIDGET_STANDARD_DATA"
        const val MARK_DONE_TASK_ACTION = "android.appwidget.action.MARK_DONE_TASK"
        const val KEY_UPDATE_TASK = "KEY_UPDATE_TASK"

        fun forceUpdateAll(
            context: Context,
            inputData: ArrayList<EventTaskUI>,
        ) {
            val widgetProvider = ComponentName(context, WidgetStandard::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, inputData)
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            inputData: ArrayList<EventTaskUI>,
        ) {
            Timber.d("WidgetStandard - updateAppWidget - inputData:  ${inputData.size}")
            // Construct the RemoteViews object

            val views = RemoteViews(context.packageName, R.layout.standard_widget).apply {
                Timber.d("WidgetStandard: setRemoteAdapter")
                setTextViewText(R.id.tvCategoryName, context.setUpLanguage().getText(R.string.all_tasks))
                setRemoteAdapter(R.id.listview, context, inputData, WIDGET_STANDARD_DATA)
                setOnClickPendingIntent(R.id.btnSetting, context, WidgetSettingActivity::class.java)
                setOnClickPendingIntent(R.id.btnAdd, context, MainActivity::class.java)
                setPendingIntentTemplate(R.id.listview, getDoneTaskPendingIntent(context))
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getDoneTaskPendingIntent(context: Context): PendingIntent {
            return Intent(context, WidgetStandard::class.java).run {
                action = MARK_DONE_TASK_ACTION
                PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            }
        }
    }

    private fun markDoneTask(context: Context, intent: Intent) {
        if (intent.action == MARK_DONE_TASK_ACTION) {
            LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent().apply { action = MARK_DONE_TASK_ACTION })

            Timber.d("MARK_DONE_TASK_ACTION: start")
            val output = intent.extras?.parcelable<EventTaskUI>(KEY_UPDATE_TASK)
            output?.let {
                widgetSettingRepo.markDoneTask(output.id!!)
                    .observeOn(SchedulerProvider.ui())
                    .subscribeBy(
                        onSuccess = {
                            forceUpdateAll(context, it as ArrayList<EventTaskUI>)
                            Timber.d("MARK_DONE_TASK: success")
                        },
                        onError = {
                            Timber.d("MARK_DONE_TASK: error")
                        }
                    )
                    .add(subscriptions)
            }
        }
    }

    private fun callRepository(context: Context) {
        widgetSettingRepo.getPrevData(
            AppPrefs.scopeTime,
            AppPrefs.scopeCategory,
            AppPrefs.showCompletedTask
        )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    Timber.d("WidgetStandard: success onUpdate ${it.size}")
                    forceUpdateAll(context.setUpLanguage(), it as ArrayList<EventTaskUI>)
                },
                onError = {
                    Timber.d("WidgetStandard: error onUpdate: ${it.message}")
                },
            ).add(subscriptions)
    }

}

