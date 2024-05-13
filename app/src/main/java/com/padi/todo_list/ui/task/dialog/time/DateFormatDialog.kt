package com.padi.todo_list.ui.task.dialog.time

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setButtonTintListCompat
import com.padi.todo_list.common.utils.BundleKey.DAY_MONTH_YEAR
import com.padi.todo_list.common.utils.BundleKey.MONTH_DAY_YEAR
import com.padi.todo_list.common.utils.BundleKey.YEAR_MONTH_DAY
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.DialogDateFormatBinding
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.setting.SettingActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFormatDialog(
    private val setText: () ->Unit
) :
    BaseDialogFragment<DialogDateFormatBinding>() {

    override fun getLayout(): Int {
        return R.layout.dialog_date_format
    }

    override fun initView() {
        setupAction()
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
            setTextTime()
            btnDone.setOnClickListener() {
                when {
                    rbYmd.isChecked -> {
                        AppPrefs.saveOptionDay(YEAR_MONTH_DAY)
                    }

                    rbDmy.isChecked -> {
                        AppPrefs.saveOptionDay(DAY_MONTH_YEAR)

                    }

                    rbMdy.isChecked -> {
                        AppPrefs.saveOptionDay(MONTH_DAY_YEAR)
                    }

                    else -> dismiss()
                }
                sendIntentUpdateWidget()
                setText.invoke()
                dismiss()
            }
            when (AppPrefs.getOptionDay()) {
                YEAR_MONTH_DAY -> groupRdDate.check(R.id.rb_ymd)
                DAY_MONTH_YEAR -> groupRdDate.check(R.id.rb_dmy)
                MONTH_DAY_YEAR -> groupRdDate.check(R.id.rb_mdy)
                else -> {
                    groupRdDate.clearCheck()
                }
            }
            btnCancel.setOnClickListener(){
                dismiss()
            }
        }
    }
    private fun setTextTime(){
        val currentDate = LocalDate.now()
        val formatDMY = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatYMD = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatMDY = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        binding.apply {
            rbYmd.text = getString(R.string.text_ymd,currentDate.format(formatYMD))
            rbDmy.text = getString(R.string.text_dmy,currentDate.format(formatDMY))
            rbMdy.text = getString(R.string.text_mdy,currentDate.format(formatMDY))
            rbYmd.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rbDmy.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rbMdy.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
        }
    }

    private fun sendIntentUpdateWidget() {
        val intent = Intent().apply {
            action = SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

}