package com.padi.todo_list.ui.work_manager

import android.app.Notification
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.notfication.NotificationChannels
import com.padi.todo_list.notfication.NotificationController
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class EventWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted parameters: WorkerParameters,
) : RxWorker(context, parameters) {

    override fun createWork(): Single<Result> {
        Timber.d("EventWorker-start")
        val inputData = inputData
        val channelId = inputData.getString(WORKER_CHANEL_ID)
        val mContext = applicationContext
        val notification = createNotification(mContext.setUpLanguage(), channelId!!)
        NotificationController.push(getNotifyID(channelId), notification)
        return Single.just(Result.success())
    }

    private fun getNotifyID(channelId: String): Int {
        return if (channelId == NotificationChannels.PIN_REMINDER.channelID) {
            NotificationController.ID_PIN_REMINDER
        } else {
            NotificationController.ID_TODO_REMIND
        }
    }

    private fun createNotification(context: Context, channelId: String): Notification {
        return if (channelId == NotificationChannels.PIN_REMINDER.channelID) {
            getPinReminderNotification(context, channelId)
        } else {
            getTodoNotification(context, channelId)
        }
    }

    private fun getPinReminderNotification(context: Context, channelId: String): Notification {
        val nb = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSilent(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val notificationLayoutSmallHighAPI = RemoteViews(
                context.packageName,
                R.layout.layout_pin_reminder_notification_small_high_api
            )
            notificationLayoutSmallHighAPI.setTextViewText(
                R.id.tvAddTask,
                context.getText(R.string.add_tasks)
            )
            notificationLayoutSmallHighAPI.setTextViewText(
                R.id.tvEnjoy,
                context.getText(R.string.enjoy_your_day)
            )

            val notificationLayoutLargeHighAPI = RemoteViews(
                context.packageName,
                R.layout.layout_pin_reminder_notification_large_high_api
            )
            notificationLayoutLargeHighAPI.setTextViewText(
                R.id.tvAddTask,
                context.getText(R.string.add_tasks)
            )
            notificationLayoutLargeHighAPI.setTextViewText(
                R.id.tvEnjoy,
                context.getText(R.string.enjoy_your_day)
            )

            notificationLayoutSmallHighAPI.setRemovePendingIntent(R.id.imgRemove, context)
            notificationLayoutLargeHighAPI.setRemovePendingIntent(R.id.imgRemove, context)
            notificationLayoutSmallHighAPI.setAddTaskPendingIntent(R.id.imgAdd, context)
            notificationLayoutLargeHighAPI.setAddTaskPendingIntent(R.id.imgAdd, context)

            nb.setContentTitle(context.getText(R.string.activity_management))
                .setCustomContentView(notificationLayoutSmallHighAPI)
                .setCustomBigContentView(notificationLayoutLargeHighAPI)
        } else {
            val notificationLayoutSmall =
                RemoteViews(context.packageName, R.layout.layout_pin_reminder_notification_small)
            notificationLayoutSmall.setTextViewText(
                R.id.tvAddTask,
                context.getText(R.string.add_tasks)
            )
            notificationLayoutSmall.setTextViewText(
                R.id.tvEnjoy,
                context.getText(R.string.enjoy_your_day)
            )

            val notificationLayoutLarge =
                RemoteViews(context.packageName, R.layout.layout_pin_reminder_notification_large)
            notificationLayoutLarge.setTextViewText(
                R.id.tvAddTask,
                context.getText(R.string.add_tasks)
            )
            notificationLayoutLarge.setTextViewText(
                R.id.tvEnjoy,
                context.getText(R.string.enjoy_your_day)
            )

            notificationLayoutSmall.setRemovePendingIntent(R.id.imgRemove, context)
            notificationLayoutLarge.setRemovePendingIntent(R.id.imgRemove, context)
            notificationLayoutSmall.setAddTaskPendingIntent(R.id.imgAdd, context)
            notificationLayoutLarge.setAddTaskPendingIntent(R.id.imgAdd, context)

            nb.setCustomContentView(notificationLayoutSmall)
                .setCustomBigContentView(notificationLayoutLarge)
        }
        return nb.build()
    }

    private fun getTodoNotification(context: Context, channelId: String): Notification {
        val nb = NotificationCompat.Builder(this.context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val notificationLayoutHighAPI =
                RemoteViews(
                    context.packageName,
                    R.layout.layout_todo_reminder_notification_high_api
                )
            notificationLayoutHighAPI.setTextViewText(R.id.tvCreateJob, context.getText(R.string.click_create_job))
            notificationLayoutHighAPI.setTextViewText(R.id.tvEnjoy, context.getText(R.string.enjoy_your_day_make_plan))

            notificationLayoutHighAPI.setAddTaskPendingIntent(R.id.tvCreateJob, context)

            nb.setContentTitle(context.getText(R.string.activity_management))
                .setCustomContentView(notificationLayoutHighAPI)
                .setCustomBigContentView(notificationLayoutHighAPI)

        } else {
            val notificationLayout =
                RemoteViews(context.packageName, R.layout.layout_todo_reminder_notification)
            notificationLayout.setTextViewText(R.id.tvCreateJob, context.getText(R.string.click_create_job))
            notificationLayout.setTextViewText(R.id.tvEnjoy, context.getText(R.string.enjoy_your_day_make_plan))

            notificationLayout.setAddTaskPendingIntent(R.id.tvCreateJob, context)

            nb.setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
        }
        return nb.build()
    }

    companion object {
        private const val WORKER_NAME = "EVENT-WORKER"
        private const val WORKER_CHANEL_ID = "WORKER_CHANEL_ID"
        fun schedulePinReminder(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            val inputData = Data.Builder()
                .putString(WORKER_CHANEL_ID, NotificationChannels.PIN_REMINDER.channelID)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<EventWorker>()
                .setInitialDelay(0, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }

        fun scheduleTodoRemind(context: Context, isClickSwitch: Boolean = false) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val inputData = Data.Builder()
                .putString(WORKER_CHANEL_ID, NotificationChannels.TODO_REMIND.channelID)
                .build()

            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(currentTime)) {
                    add(Calendar.DATE, 1)
                }
            }

            val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

            val workRequest = PeriodicWorkRequestBuilder<EventWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(
                    if (isClickSwitch) {
                        0
                    } else {
                        initialDelay
                    }, TimeUnit.MILLISECONDS
                )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }

    }

    private fun RemoteViews.setAddTaskPendingIntent(viewId: Int, context: Context) {
        this.setOnClickPendingIntent(
            viewId, UtilsJ.getAddTaskPendingIntent(context)
        )
    }

    private fun RemoteViews.setRemovePendingIntent(viewId: Int, context: Context) {
        this.setOnClickPendingIntent(
            viewId,
            UtilsJ.getRemovePendingIntent(context)
        )
    }
}

