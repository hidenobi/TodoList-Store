package com.padi.todo_list.ui.notification_help

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.utils.PermissionUtils.checkBatteryPermission
import com.padi.todo_list.common.utils.PermissionUtils.checkNotifyPermission
import com.padi.todo_list.common.utils.PermissionUtils.checkOverlayPermission
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityNotificationHelpBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NotificationHelpActivity :
    BaseBindingActivity<NotificationHelpViewModel, ActivityNotificationHelpBinding>(R.layout.activity_notification_help),
    NotificationAndHelpInterface {
    override val viewModel: NotificationHelpViewModel by viewModels()

    private var overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            viewModel.updateFloatingWindowEnable(checkOverlayPermission(this))
        }

    private val ignoreBatteryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            Timber.i("ICARUS ${checkBatteryPermission(this)}")
            viewModel.updateIgnoreBatteryEnable(checkBatteryPermission(this))
        }
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            viewModel.updateNotificationEnable(checkNotifyPermission(this))
        }

    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        initPermissionValue()
        setAction()

        observeData()
    }

    private fun initPermissionValue() {
        viewModel.updateNotificationEnable(checkNotifyPermission(this))
        viewModel.updateFloatingWindowEnable(checkOverlayPermission(this))
        viewModel.updateIgnoreBatteryEnable(checkBatteryPermission(this))
    }

    private fun observeData() {
        viewModel.areNotificationsEnabled.observe(this) {
            viewBinding.notifyEnable = it
        }

        viewModel.canDrawOverlays.observe(this) {
            viewBinding.floatingEnable = it
        }

        viewModel.isIgnoringBatteryOptimizations.observe(this) {
            viewBinding.batteryEnable = it
        }
    }

    private fun setAction() {
        viewBinding.apply {
            tbNotifyHelp.handleAction { finish() }
            myInterface = this@NotificationHelpActivity
        }

    }

    override fun launchNotifySetting() {
        notificationPermissionLauncher.launch(notificationIntent)
    }

    override fun launchIgnoreBatterySetting() {
        ignoreBatteryPermissionLauncher.launch(ignoreBatteryIntent)
    }

    override fun launchFloatingWindowSetting() {
        overlayPermissionLauncher.launch(overlayIntent)
    }
}