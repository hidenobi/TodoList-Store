package com.padi.todo_list.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.parcelableArrayList
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.extension.toPx
import com.padi.todo_list.data.local.model.AlarmModel
import com.padi.todo_list.notfication.NotificationChannels
import com.padi.todo_list.notfication.NotificationController
import com.padi.todo_list.ui.main.MainActivity
import java.util.ArrayList


class OverlayService : Service() {
    private var isServiceRunning: Boolean = false
    private var mWindowManager: WindowManager? = null
    private var mView: View? = null
    private var mAdapter = OverlayInfoAdapter()

    companion object {
        const val OVERLAY_SERVICE_DATA = "OVERLAY_SERVICE_DATA"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mContext = this@OverlayService.applicationContext.setUpLanguage()
        if (isServiceRunning) {
            removeOverlay()
        } else {
            isServiceRunning = true
        }
        init(intent!!, mContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationChannels.NOTIFY.id, getNotification(mContext),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NotificationChannels.NOTIFY.id, getNotification(mContext))
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }

    @SuppressLint("InflateParams")
    @Suppress("NAME_SHADOWING")
    private fun init(intent: Intent, mContext: Context) {
        val alarmModels = intent.parcelableArrayList<AlarmModel>(OVERLAY_SERVICE_DATA)
        // Set up WindowManager
        val mFlag = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        val mParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, 134.toPx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            mFlag,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER
            x = 0
        }

        // Inflate layout overlay
        val layoutInflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = layoutInflater.inflate(R.layout.overlay_layout, null)

        // Set up RecyclerView
        mAdapter.setData(alarmModels!!)
        val recyclerView = mView!!.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter

        val tvTitle = mView!!.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = mContext.getText(R.string.task_reminders)
        val tvGotIt = mView!!.findViewById<TextView>(R.id.tvGotIt)
        tvGotIt.text = mContext.getText(R.string.got_it)
        tvGotIt.setOnClickListener {
            cancelNotification(alarmModels)
            stopSelf()
        }

        val tvCheck = mView!!.findViewById<TextView>(R.id.tvCheck)
        tvCheck.text = mContext.getText(R.string.check)
        tvCheck.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            cancelNotification(alarmModels)
            stopSelf()
        }

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if (mView?.parent == null) {
            mWindowManager?.addView(mView, mParams)
        }
    }

    private fun getNotification(mContext: Context): Notification {
        return NotificationCompat.Builder(
            mContext,
            NotificationChannels.NOTIFY.channelID
        ).setSmallIcon(R.mipmap.ic_launcher_round).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setVibrate(longArrayOf(0, 100))
            .setSilent(true).build()
    }

    private fun removeOverlay() {
        if (mView != null && mWindowManager != null) {
            mWindowManager?.removeView(mView)
        }
    }

    private fun cancelNotification(alarmModels: ArrayList<AlarmModel>) {
        alarmModels.map { model ->
            NotificationController.cancel(model.id.toInt())
        }
    }

}