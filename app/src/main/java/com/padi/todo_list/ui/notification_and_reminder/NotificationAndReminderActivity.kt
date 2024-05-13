package com.padi.todo_list.ui.notification_and_reminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityNotificationAndReminderBinding
import com.padi.todo_list.notfication.NotificationChannels
import com.padi.todo_list.notfication.NotificationController
import com.padi.todo_list.ui.notification_help.NotificationHelpActivity
import com.padi.todo_list.ui.work_manager.EventWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationAndReminderActivity :
    BaseBindingActivity<NotificationAndRemindViewModel, ActivityNotificationAndReminderBinding>(R.layout.activity_notification_and_reminder) {
    override val viewModel: NotificationAndRemindViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        setAction()

    }

    private fun setAction() {
        viewBinding.apply {
            tbNotifyAndReminder.handleAction { finish() }
            incTaskReminderNotWork.lnTaskReminderNotWork.setOnDebounceClickListener {
                val intent = Intent(
                    this@NotificationAndReminderActivity,
                    NotificationHelpActivity::class.java
                )
                startActivity(intent)
            }

            incAddTask.swAddTask.isChecked = AppPrefs.getEnableAddTaskFromNotification()
            incAddTask.swAddTask.setOnCheckedChangeListener { _, b ->
                if (b) {
                    EventWorker.schedulePinReminder(this@NotificationAndReminderActivity)
                } else {
                    NotificationController.cancel(NotificationController.ID_PIN_REMINDER)
                }
                AppPrefs.saveEnableAddTaskFromNotification(b)
            }

            incTodoReminder.swTodoReminder.isChecked = AppPrefs.getEnableTodoReminder()
            incTodoReminder.swTodoReminder.setOnCheckedChangeListener { _, b ->
                if (b) {
                    NotificationController.cancel(NotificationController.ID_TODO_REMIND)
                    EventWorker.scheduleTodoRemind(this@NotificationAndReminderActivity, true)
                } else {
                    NotificationController.cancel(NotificationController.ID_TODO_REMIND)
                }
                AppPrefs.saveEnableTodoReminder(b)
            }
        }
    }

}