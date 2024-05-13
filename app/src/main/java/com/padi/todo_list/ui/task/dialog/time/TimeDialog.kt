package com.padi.todo_list.ui.task.dialog.time

import android.content.res.Resources
import android.view.KeyEvent
import android.view.View
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.invisible
import com.padi.todo_list.common.extension.serializable
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.TimeFormatValue
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.is24HourView
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.DialogTimeBinding
import com.padi.todo_list.ui.task.enum_class.TimeSelectionEnum
import com.padi.todo_list.ui.task.model.ClockModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TimeDialog(
    private val onClickDone: (typeTime: TimeSelectionEnum, clockModel: ClockModel) -> Unit
) : BaseDialogFragment<DialogTimeBinding>() {
    private lateinit var _clockModel: ClockModel
    private lateinit var _typeTime: TimeSelectionEnum
    override fun getLayout(): Int {
        return R.layout.dialog_time
    }

    override fun initView() {
        getArgument()
        binding.is24HourView = is24HourView(AppPrefs.getTimeFormat())
        setupTimePicker()
        setupAction()
        handleBackDevice()
    }

    private fun getArgument() {
        _clockModel = arguments?.serializable(BundleKey.KEY_CLOCK_MODEL)!!
        _typeTime = arguments?.serializable(BundleKey.KEY_TYPE_TIME) ?:TimeSelectionEnum.NO_TIME
        binding.timeSelection = _typeTime
    }

    private fun setupTimePicker() {
        binding.apply {
            timePicker.setIs24HourView(is24HourView(AppPrefs.getTimeFormat()))
            timeSelection = _typeTime
        }
        if (_clockModel.hour != ClockModel.NO_HOUR) {
            binding.timePicker.hour = _clockModel.hour
        }
        if (_clockModel.minute != ClockModel.NO_HOUR) {
            binding.timePicker.minute = _clockModel.minute
        }
        binding.timePicker.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom->
            val id = Resources.getSystem().getIdentifier("input_header", "id", "android")
            binding.timePicker.findViewById<View>(id)?.invisible()
        }
    }

    private fun setupAction() {
        binding.chipNoTime.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum.NO_TIME
        }
        binding.chip7Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._7_HOUR
            binding.timePicker.hour = 7
            binding.timePicker.minute = 0
        }
        binding.chip9Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._9_HOUR
            binding.timePicker.hour = 9
            binding.timePicker.minute = 0
        }
        binding.chip10Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._10_HOUR
            binding.timePicker.hour = 10
            binding.timePicker.minute = 0
        }
        binding.chip12Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._12_HOUR
            binding.timePicker.hour = 12
            binding.timePicker.minute = 0
        }
        binding.chip14Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._14_HOUR
            binding.timePicker.hour = 14
            binding.timePicker.minute = 0
        }
        binding.chip16Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._16_HOUR
            binding.timePicker.hour = 16
            binding.timePicker.minute = 0
        }
        binding.chip18Hour.setOnClickListener {
            binding.timeSelection = TimeSelectionEnum._18_HOUR
            binding.timePicker.hour = 18
            binding.timePicker.minute = 0
        }
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            _clockModel.hour = hourOfDay
            _clockModel.minute = minute
            updateTimeSelection(hourOfDay, minute)
        }
        binding.btnCancel.setOnDebounceClickListener {
            dismiss()
        }
        binding.btnDone.setOnDebounceClickListener {
            val clockModel =
                if (binding.timeSelection != TimeSelectionEnum.NO_TIME) _clockModel else _clockModel.copy(
                    hour = ClockModel.NO_HOUR,
                    minute = ClockModel.NO_HOUR
                )
            onClickDone.invoke(
                binding.timeSelection as TimeSelectionEnum,
                clockModel
            )
            dismiss()
        }
    }

    private fun updateTimeSelection(hourOfDay: Int, minute: Int) {
        when {
            hourOfDay == 7 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._7_HOUR
            }
            hourOfDay == 9 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._9_HOUR
            }
            hourOfDay == 10 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._10_HOUR
            }
            hourOfDay == 12 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._12_HOUR
            }
            hourOfDay == 14 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._14_HOUR
            }
            hourOfDay == 16 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._16_HOUR
            }
            hourOfDay == 18 && minute == 0 -> {
                binding.timeSelection = TimeSelectionEnum._18_HOUR
            }
            else -> {
                binding.timeSelection = TimeSelectionEnum.ANOTHER
            }
        }
    }
    private fun handleBackDevice() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                return@setOnKeyListener true
            } else
                return@setOnKeyListener false
        }
    }
}