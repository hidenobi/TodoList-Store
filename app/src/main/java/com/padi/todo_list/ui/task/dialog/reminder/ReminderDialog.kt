package com.padi.todo_list.ui.task.dialog.reminder

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.activityViewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.deepCopy
import com.padi.todo_list.common.extension.parcelableArrayList
import com.padi.todo_list.common.extension.serializable
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.setSizePercent
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.databinding.DialogReminderBinding
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.task.dialog.remind_time.RemindTimeDialog
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReminderDialog(private val onClickCancel: (allOffsetTimeOutput: List<OffsetTimeUI>) -> Unit,private val onClickDone: (allOffsetTimeOutput: List<OffsetTimeUI>) -> Unit) :
    BaseDialogFragment<DialogReminderBinding>() {
    private lateinit var mClockInput: ClockModel
    private val viewModel: MainViewModel by activityViewModels()
    private var mOffsetTimeInput = OffsetTimeUI.getData(isNotSelect = false)
    private var mIsRemind = true
    private var mPrepareInputCompleted = false
    override fun getLayout(): Int {
        return R.layout.dialog_reminder
    }

    override fun initView() {
        setSizePercent(90)
        getArgument()
        binding.isRemind = mIsRemind
        setupAction()
        handleBackDevice()
        observeData()
    }

    private fun getArgument() {
        mClockInput = arguments?.serializable(BundleKey.KEY_CLOCK_MODEL)!!
        val temp = arguments?.parcelableArrayList<OffsetTimeUI>(BundleKey.KEY_LIST_REMIND_TIME_UI)!!
        mOffsetTimeInput = temp.deepCopy()
        viewModel.updateListOffsetTimeDefault(ArrayList(temp))
        mIsRemind = arguments?.serializable(BundleKey.IS_REMIND)!!
    }

    private fun observeData() {
        viewModel.listOffsetTimeDefaultLiveData.observe(viewLifecycleOwner) {
            mPrepareInputCompleted = true
            setTextReminderTime(it)
            binding.isRemind = it.any { model -> model.isChecked }
        }
    }

    private fun setupAction() {
        binding.swReminder.setOnCheckedChangeListener { _, isChecked ->
            if (mPrepareInputCompleted) {
                if (!isChecked) {
                    viewModel.updateListOffsetTimeDefault(OffsetTimeUI.getData(isNotSelect = true))
                } else {
                    viewModel.updateListOffsetTimeDefault(OffsetTimeUI.getData(isNotSelect = false))
                }
            }
        }
        binding.lnRemind.setOnDebounceClickListener {
            val location = IntArray(2)
            binding.imgDown.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]

            val bundle = Bundle()
            bundle.putSerializable(BundleKey.KEY_CLOCK_MODEL, mClockInput)
            bundle.putParcelableArrayList(
                BundleKey.KEY_LIST_REMIND_TIME_UI,
                viewModel.listOffsetTimeDefaultLiveData.value!!
            )
            bundle.putInt(BundleKey.POS_X, x)
            bundle.putInt(BundleKey.POS_Y, y)
            val remindTimeDialog = RemindTimeDialog {
                viewModel.updateListOffsetTimeDefault(it)
            }
            remindTimeDialog.arguments = bundle
            remindTimeDialog.show(childFragmentManager, RemindTimeDialog::javaClass.name)
        }
        binding.btnCancel.setOnDebounceClickListener {
            onClickCancel.invoke(mOffsetTimeInput)
            dismiss()
        }
        binding.btnDone.setOnDebounceClickListener {
            onClickDone.invoke(viewModel.listOffsetTimeDefaultLiveData.value!!)
            dismiss()
        }
    }


    private fun setTextReminderTime(inputList: ArrayList<OffsetTimeUI>) {
        val resultText = StringBuilder("")
        val list = inputList.filter { model -> model.isChecked }
        if (list.isNotEmpty()) {
            list.forEachIndexed { index, model ->
                if (index == 0) {
                    resultText.append(model.text)
                } else {
                    resultText.append(" ,${model.text}")
                }
            }
        }
        binding.tvRemindValue.text = resultText.toString().trim()
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