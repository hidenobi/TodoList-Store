package com.padi.todo_list.ui.task.dialog.custom_remind_time

import androidx.fragment.app.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.parcelable
import com.padi.todo_list.common.extension.serializable
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.setSizePercent
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.NumberPickerKey.DEFAULT_NUMBER_VALUE
import com.padi.todo_list.common.utils.NumberPickerKey.DEFAULT_UNIT_VALUE
import com.padi.todo_list.common.utils.NumberPickerKey.MAX_NUMBER_VALUE
import com.padi.todo_list.common.utils.NumberPickerKey.MAX_UNIT_VALUE
import com.padi.todo_list.common.utils.NumberPickerKey.MIN_NUMBER_VALUE
import com.padi.todo_list.common.utils.NumberPickerKey.MIN_UNIT_VALUE
import com.padi.todo_list.databinding.DialogCustomOffsetTimeBinding
import com.padi.todo_list.ui.task.enum_class.RemindUnitEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomOffsetTimeDialog(private val onClickSave: (offsetTimeUI: OffsetTimeUI) -> Unit) :
    BaseDialogFragment<DialogCustomOffsetTimeBinding>() {
    private val viewModel: CustomOffsetTimeDlgViewModel by viewModels()
    private lateinit var _clock: ClockModel
    private lateinit var _customOffsetTimeInput: OffsetTimeUI
    override fun getLayout(): Int {
        return R.layout.dialog_custom_offset_time
    }

    override fun initView() {
        setSizePercent(80)
        getArgument()
        setupUnitPicker()
        setupNumPicker()
        setAction()
        observeData()
    }

    private fun getArgument() {
        _clock = arguments?.serializable(BundleKey.KEY_CLOCK_MODEL)!!
        binding.clock = _clock
        viewModel.initClockModel(_clock)
        _customOffsetTimeInput = arguments?.parcelable(BundleKey.KEY_REMIND_TIME_UI)!!
        binding.offsetTimeUIModel = _customOffsetTimeInput
        viewModel.initCustomOffsetTime(_customOffsetTimeInput)
    }

    private fun observeData() {
        viewModel.clockModelLiveData.observe(viewLifecycleOwner) { clock ->
            binding.clock = clock
        }
        viewModel.unitLiveData.observe(viewLifecycleOwner) { unit ->
            binding.unit = unit
            viewModel.updateRemindModel(newUnit = unit)
        }
        viewModel.numberLiveData.observe(viewLifecycleOwner) { number ->
            binding.num = number
            viewModel.updateRemindModel(newNumber = number)
        }
        viewModel.offSetTimeModelLivedata.observe(viewLifecycleOwner) {
            binding.offsetTimeUIModel = it
        }
    }

    private fun setAction() {
        binding.unitPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.updateUnit(newVal)
        }
        binding.numPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.updateNumber(newVal)
        }
        binding.btnCancel.setOnDebounceClickListener {
            onClickSave.invoke(
                _customOffsetTimeInput
            )
            dismiss()
        }
        binding.btnSave.setOnDebounceClickListener {
            onClickSave.invoke(
                viewModel.offSetTimeModelLivedata.value!!.copy(isChecked = true)
            )
            dismiss()
        }
    }

    private fun setupNumPicker() {
        binding.numPicker.apply {
            minValue = MIN_NUMBER_VALUE
            maxValue = MAX_NUMBER_VALUE
            value = DEFAULT_NUMBER_VALUE
            wrapSelectorWheel = false
        }
        binding.num = binding.numPicker.value
    }

    private fun setupUnitPicker() {
        binding.unitPicker.apply {
            val stringArray = resources.getStringArray(R.array.remind_unit_picker)
            displayedValues = stringArray
            minValue = MIN_UNIT_VALUE
            maxValue = MAX_UNIT_VALUE
            wrapSelectorWheel = false
            value = DEFAULT_UNIT_VALUE
        }
        binding.unit = RemindUnitEnum.HOUR
    }
}