package com.padi.todo_list.ui.task.dialog.time

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setButtonTintListCompat
import com.padi.todo_list.common.utils.TimeFormatValue.FORMAT_12
import com.padi.todo_list.common.utils.TimeFormatValue.FORMAT_24
import com.padi.todo_list.common.utils.TimeFormatValue.FORMAT_BY_SYSTEM
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.DialogTimeFormatBinding
import com.padi.todo_list.ui.setting.SettingActivity

class TimeFormatDialog(
    private val setText: () ->Unit
) :
    BaseDialogFragment<DialogTimeFormatBinding>() {

    override fun getLayout(): Int {
        return R.layout.dialog_time_format
    }

    override fun initView() {
        setupAction()
        setTextTime()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupAction() {
        binding.apply {
            btnDone.setOnClickListener() {
                when {
                    rb24h.isChecked -> {
                        AppPrefs.saveTimeFormat(FORMAT_24)
                    }

                    rb12h.isChecked -> {
                        AppPrefs.saveTimeFormat(FORMAT_12)

                    }

                    rbDefault.isChecked -> {
                        AppPrefs.saveTimeFormat(FORMAT_BY_SYSTEM)
                    }

                    else -> dismiss()
                }
                sendIntentUpdateWidget()
                setText.invoke()
                dismiss()
            }
            when (AppPrefs.getTimeFormat()) {
                FORMAT_24 -> groupRdTime.check(R.id.rb_24h)
                FORMAT_12 -> groupRdTime.check(R.id.rb_12h)
                FORMAT_BY_SYSTEM -> groupRdTime.check(R.id.rb_default)
                else -> {
                    groupRdTime.clearCheck()
                }
            }
            btnCancel.setOnClickListener(){
                dismiss()
            }
        }
    }

    private fun sendIntentUpdateWidget() {
        val intent = Intent().apply {
            action = SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun setTextTime(){
        binding.apply {
            rb24h.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rb12h.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rbDefault.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
        }
    }

}