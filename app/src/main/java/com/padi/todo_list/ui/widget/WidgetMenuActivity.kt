package com.padi.todo_list.ui.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.transformToTaskUI
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityWidgetMenuBinding
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.receiver.widget.WidgetsReceiver
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.widget.dialog.AddWidgetBottomSheet
import com.padi.todo_list.ui.widget.dialog.WidgetGuideDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WidgetMenuActivity :
    BaseBindingActivity<WidgetMenuViewModel, ActivityWidgetMenuBinding>(R.layout.activity_widget_menu) {
    override val viewModel: WidgetMenuViewModel by viewModels()

    private lateinit var addWidgetBottomSheet: AddWidgetBottomSheet
    private val guideDialog = WidgetGuideDialog()

    private val broadcastAddWidgetSuccess: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    if (it.action == WidgetsReceiver.ACTION_ADD_WIDGET_SUCCESS) {
                        showGuideDialog()
                    }
                }
            }
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        showBatteryPerDialog()
        registerBroadcastAddWidgetSuccess()
        setAction()
        observeData()
    }

    private fun observeData() {
        viewModel.listTaskLiveData.observe(this) {
            Timber.d("WidgetStandard live data ${it.size}")
            if (it != null) {
                Timber.d("WidgetStandard live data pinWidget")
                pinWidgetToHomeScreen(it.transformToTaskUI(true))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastAddWidgetSuccess()
    }

    private fun setAction() {
        viewBinding.tbWidget.handleAction(leftAction = {
            finish()
        })

        viewBinding.incStandardWidget.root.setOnDebounceClickListener {
            showBottomSheet()
        }

    }

    private fun showBottomSheet() {
        addWidgetBottomSheet = AddWidgetBottomSheet(
            onClickAdd = {
                viewModel.prepareData()
                addWidgetBottomSheet.dismiss()
            },
            onClickCancel = {
                showGuideDialog()
                addWidgetBottomSheet.dismiss()
            })
        addWidgetBottomSheet.show(supportFragmentManager, AddWidgetBottomSheet::class.java.name)
    }

    private fun pinWidgetToHomeScreen(data: ArrayList<EventTaskUI>) {
        val appWidgetManager = AppWidgetManager.getInstance(this@WidgetMenuActivity)
        val provider = UtilsJ.getProvider(this@WidgetMenuActivity)

        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            Toast.makeText(this, getString(R.string.not_support), Toast.LENGTH_SHORT).show()
            return
        }
        if (!UtilsJ.canShowAddWidgetDirectly()) {
            return
        }

        val successCallback = WidgetsReceiver.getPendingIntent(this@WidgetMenuActivity, data)
        val remoteViews =
            UtilsJ.getRemoteWidgetPreview(this@WidgetMenuActivity, WidgetStandard.NAME)
        val bundle = Bundle().apply {
            putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, remoteViews)
        }
        appWidgetManager.requestPinAppWidget(provider, bundle, successCallback)
    }

    private fun showGuideDialog() {
        guideDialog.dialog?.isShowing?.let {
            guideDialog.dismiss()
        }
        guideDialog.show(supportFragmentManager, WidgetGuideDialog::class.java.name)
    }


    private fun registerBroadcastAddWidgetSuccess() {
        val filter = IntentFilter(WidgetsReceiver.ACTION_ADD_WIDGET_SUCCESS)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastAddWidgetSuccess, filter)
    }

    private fun unregisterBroadcastAddWidgetSuccess() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastAddWidgetSuccess)
    }
}