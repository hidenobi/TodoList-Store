package com.padi.todo_list.receiver.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.utils.ACTION_ALARM
import com.padi.todo_list.common.utils.ALARM_JSON
import com.padi.todo_list.common.utils.ALARM_TIME
import com.padi.todo_list.common.utils.AlarmUtils
import com.padi.todo_list.common.utils.PermissionUtils
import com.padi.todo_list.common.utils.PermissionUtils.checkOverlayPermission
import com.padi.todo_list.data.local.model.AlarmModel
import com.padi.todo_list.data.local.repository.AlarmRepositoryImpl
import com.padi.todo_list.notfication.NotificationController
import com.padi.todo_list.service.OverlayService
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TaskAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmRepo: AlarmRepositoryImpl

    private val TIME_SYSTEM_CHANGE_LIST = arrayOf(
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_TIMEZONE_CHANGED,
        Intent.ACTION_LOCALE_CHANGED,
        "android.intent.action.TIME_SET",
        Intent.ACTION_MY_PACKAGE_REPLACED
    )

    private val compositeDisposable = CompositeDisposable()

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("CHECK-Notify: start onReceive")

        val action = intent.action
        val alarmAction = String.format(Locale.US, "%s.%s", context.packageName, ACTION_ALARM)
        if (action == alarmAction) {
            Timber.d("CHECK-Notify: alarm-action 1")
            val alarmJson = intent.extras!!.getString(ALARM_JSON)
            val alarmTime = intent.extras!!.getLong(ALARM_TIME)
            val gson = Gson()
            val typeOfObjectsList = object : TypeToken<ArrayList<AlarmModel?>?>() {}.type
            val alarmModels: List<AlarmModel>? =
                gson.fromJson<List<AlarmModel>>(alarmJson, typeOfObjectsList)
            if (!alarmModels.isNullOrEmpty() && alarmTime != -1L) {
                Timber.d("CHECK-Notify: !alarmModels.isNullOrEmpty()")
                NotificationController.notifyEventTask(context, alarmModels)
//                start overlay service
                if (checkOverlayPermission(context)) {
                    val serviceIntent = Intent(context, OverlayService::class.java).apply {
                        putParcelableArrayListExtra(OverlayService.OVERLAY_SERVICE_DATA, alarmModels as ArrayList)
                    }
                    context.startForegroundService(serviceIntent)
                }
            }

            findNextEvent(context)
        } else if (listOf(*TIME_SYSTEM_CHANGE_LIST).contains(action)) {
            findNextEvent(context)
            Timber.d("CHECK-Notify: alarm-action 2")
        }

    }

    private fun findNextEvent(context: Context) {
        Timber.d("CHECK-Notify: findNextEvent")
        alarmRepo.getNextAlarm().subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui()).subscribeBy(onSuccess = {
                Timber.d("CHECK-Notify-findNextEvent-TaskAlarmReceiver: success")
                AlarmUtils.startAlarm(context, it)
            }, onError = {
                Timber.d("CHECK-Notify-findNextEvent-TaskAlarmReceiver: error ${it.message}")
            }).addTo(compositeDisposable)
    }
}