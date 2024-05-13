package com.padi.todo_list.ui.setting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.setButtonTintListCompat
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.TimeFormatValue
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivitySettingBinding
import com.padi.todo_list.ui.notification_and_reminder.NotificationAndReminderActivity
import com.padi.todo_list.ui.task.dialog.time.DateFormatDialog
import com.padi.todo_list.ui.task.dialog.time.TimeFormatDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class SettingActivity :
    BaseBindingActivity<SettingViewModel, ActivitySettingBinding>(R.layout.activity_setting) {
    override val viewModel: SettingViewModel by viewModels()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_CHANGE_DATE_TIME_FORMAT -> {
                    viewModel.updateWidget()
                }
            }
        }
    }

    companion object {
        const val ACTION_CHANGE_DATE_TIME_FORMAT = "ACTION_CHANGE_DATE_TIME_FORMAT"
    }
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        regisReceiver()
        setAction()
        setTextTime()
        setTextDay()
    }

    private fun setAction() {
        viewBinding.apply {
            llDateFormat.setOnDebounceClickListener() {
                val dateFormatDialog = DateFormatDialog(
                    setText = {
                        setTextDay()
                    }
                )
                dateFormatDialog.show(supportFragmentManager, DateFormatDialog::javaClass.name)
            }
            llTimeFormat.setOnDebounceClickListener() {
                val timeFormatDialog = TimeFormatDialog(
                    setText = {
                        setTextTime()
                    }
                )
                timeFormatDialog.show(supportFragmentManager, TimeFormatDialog::javaClass.name)
            }
            tbSetting.handleAction(leftAction = {
                finish()
            })
            lnNotifyAndRemind.setOnDebounceClickListener {
                val intent = Intent(this@SettingActivity, NotificationAndReminderActivity::class.java)
                startActivity(intent)
            }
            swTone.isChecked = AppPrefs.getCompleteTone()
            swTone.setOnCheckedChangeListener { _, b ->
                AppPrefs.saveCompleteTone(b)
            }

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@SettingActivity).unregisterReceiver(receiver)
    }
    private fun setTextTime(){
        when(AppPrefs.getTimeFormat()){
            TimeFormatValue.FORMAT_24 -> viewBinding.txtOption.setText(R.string.time_24h)
            TimeFormatValue.FORMAT_12 -> viewBinding.txtOption.setText(R.string.time_12h)
            TimeFormatValue.FORMAT_BY_SYSTEM -> viewBinding.txtOption.setText(R.string.system_default)
        }
    }
    private fun setTextDay(){
        val currentDate = LocalDate.now()
        val formatDMY = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatYMD = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatMDY = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        when(AppPrefs.getOptionDay()){
            BundleKey.YEAR_MONTH_DAY ->  viewBinding.txtDay.text = currentDate.format(formatYMD).toString()
            BundleKey.DAY_MONTH_YEAR -> viewBinding.txtDay.text =  currentDate.format(formatDMY).toString()
            BundleKey.MONTH_DAY_YEAR -> viewBinding.txtDay.text = currentDate.format(formatMDY).toString()
        }
    }
    private fun regisReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_CHANGE_DATE_TIME_FORMAT)
        }
        LocalBroadcastManager.getInstance(this@SettingActivity).registerReceiver(
            receiver,
            intentFilter
        )
    }
}