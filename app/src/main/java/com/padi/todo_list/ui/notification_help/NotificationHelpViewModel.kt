package com.padi.todo_list.ui.notification_help

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationHelpViewModel @Inject constructor() : BaseViewModel() {

    private val _areNotificationsEnabled = MutableLiveData(false)
    val areNotificationsEnabled: LiveData<Boolean> = _areNotificationsEnabled

    fun updateNotificationEnable(bool: Boolean) {
        _areNotificationsEnabled.value = bool
    }

    private val _canDrawOverlays = MutableLiveData(false)
    val canDrawOverlays: LiveData<Boolean> = _canDrawOverlays

    fun updateFloatingWindowEnable(bool: Boolean) {
        _canDrawOverlays.value = bool
    }

    private val _isIgnoringBatteryOptimizations = MutableLiveData(false)
    val isIgnoringBatteryOptimizations: LiveData<Boolean> = _isIgnoringBatteryOptimizations

    fun updateIgnoreBatteryEnable(bool: Boolean) {
        _isIgnoringBatteryOptimizations.value = bool
    }

}