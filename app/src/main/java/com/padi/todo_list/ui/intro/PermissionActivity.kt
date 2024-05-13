package com.padi.todo_list.ui.intro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.utils.PermissionUtils
import com.padi.todo_list.common.utils.PermissionUtils.checkNotifyPermission
import com.padi.todo_list.common.utils.PermissionUtils.checkOverlayPermission
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.setStatusBar
import com.padi.todo_list.databinding.ActivityPermissionBinding
import com.padi.todo_list.ui.main.MainActivity
import com.padi.todo_list.ui.notification_help.NotificationAndHelpInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity :
    BaseBindingActivity<BaseViewModel, ActivityPermissionBinding>(R.layout.activity_permission),
    NotificationAndHelpInterface {
    override val viewModel: BaseViewModel by viewModels()
    private var overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewBinding.swFloating.isChecked = it.resultCode == Activity.RESULT_OK
            if (checkOverlayPermission(this)) {
                viewBinding.swFloating.isChecked = true
                viewBinding.swFloating.isEnabled = false
            }
        }
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewBinding.swNotification.isChecked = it.resultCode == Activity.RESULT_OK
            if (checkNotifyPermission(this)) {
                viewBinding.swNotification.isChecked = true
                viewBinding.swNotification.isEnabled = false
            }
        }

    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        setStatusBar(window, R.color.main_screen, true)
        initSwitchCompat()
        setAction()
    }
    private fun initSwitchCompat(){
        if (checkNotifyPermission(this)) {
            viewBinding.swNotification.isChecked = true
            viewBinding.swNotification.isEnabled = false
        }
        if (checkOverlayPermission(this)) {
            viewBinding.swFloating.isChecked = true
            viewBinding.swFloating.isEnabled = false
        }
    }
    private fun setAction(){
        viewBinding.swFloating.setOnClickListener() {
            launchFloatingWindowSetting()
        }
        viewBinding.swNotification.setOnClickListener() {
            if (!checkNotifyPermission(this)) {
                launchNotifySetting()
            }
        }
        viewBinding.swFloating.setOnClickListener() {
            if (!checkOverlayPermission(this)) {
                launchFloatingWindowSetting()
            }
        }
        viewBinding.next.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun launchNotifySetting() {
        notificationPermissionLauncher.launch(notificationIntent)
    }

    override fun launchFloatingWindowSetting() {
        overlayPermissionLauncher.launch(overlayIntent)
    }
}