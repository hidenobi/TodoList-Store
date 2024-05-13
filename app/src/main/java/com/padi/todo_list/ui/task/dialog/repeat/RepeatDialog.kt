package com.padi.todo_list.ui.task.dialog.repeat

import android.view.KeyEvent
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.serializable
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.databinding.DialogRepeatBinding
import com.padi.todo_list.ui.task.dialog.adapter.RepeatOptionAdapter
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.RepeatOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class RepeatDialog(private val onClickDone: (repeatOption: RepeatOptions) -> Unit) :
    BaseDialogFragment<DialogRepeatBinding>() {
    private val viewModel: RepeatDlgViewModel by viewModels()
    private var mIsRepeatInput by Delegates.notNull<Boolean>()
    private lateinit var mRepeatOptionInput: RepeatOptions
    private lateinit var mClockInput: ClockModel
    private val repeatAdapter: RepeatOptionAdapter by lazy { RepeatOptionAdapter {} }
    override fun getLayout(): Int {
        return R.layout.dialog_repeat
    }

    override fun initView() {
        getArgument()
        setupRecyclerView()
        setupAction()
        handleBackDevice()
        observeData()
    }

    private fun getArgument() {
        mIsRepeatInput = arguments?.getBoolean(BundleKey.IS_REPEAT)!!
        viewModel.updateRepeat(mIsRepeatInput)
        mRepeatOptionInput = arguments?.serializable(BundleKey.KEY_REPEAT_OPTION)!!
        viewModel.updateRepeatOption(mRepeatOptionInput)
        mClockInput = arguments?.serializable(BundleKey.KEY_CLOCK_MODEL)!!
    }

    private fun observeData() {
        viewModel.listRepeatOption.observe(viewLifecycleOwner) { output ->
            repeatAdapter.setData(output)
        }
        viewModel.isRepeat.observe(viewLifecycleOwner) {
            binding.swRepeat.isChecked = it
        }
    }

    private fun setupRecyclerView() {
        binding.rvRepeat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRepeat.adapter = repeatAdapter
        repeatAdapter.onClickItem = { repeatOption ->
            handleAdapterClick(repeatOption)
        }
    }

    private fun setupAction() {
        binding.swRepeat.setOnCheckedChangeListener { _, isChecked ->
            handleSwitchCheck(isChecked)
        }
        binding.btnCancel.setOnDebounceClickListener {
            dismiss()
        }
        binding.btnDone.setOnDebounceClickListener {
            onClickDone.invoke(
                if (!viewModel.isRepeat.value!!) {
                    RepeatOptions()
                } else {
                    viewModel.repeatOptionLiveData.value!!
                }
            )
            dismiss()
        }
    }
    private fun handleSwitchCheck(isChecked: Boolean) {
        viewModel.updateRepeat(isChecked)
        if (isChecked) {
            if (viewModel.repeatOptionLiveData.value!!.id == RepeatOptions.NOT_INIT_ID) {
                val model = viewModel.listRepeatOption.value!![1] // option daily
                viewModel.updateRepeatOption(model.copy(isSelected = true))
                viewModel.updateSelectRepeatOption(model)
            } else {
                viewModel.updateSelectRepeatOption(viewModel.repeatOptionLiveData.value!!)
            }
        } else {
            viewModel.clearAllRepeatOption()
        }
    }

    private fun handleAdapterClick(repeatOption: RepeatOptions) {
        if ((mClockInput.hour == ClockModel.NO_HOUR || mClockInput.minute == ClockModel.NO_HOUR) && repeatOption.id == 1 // hourly
        ) {
           showToast(requireContext(), getString(R.string.please_select_time))
        } else {
            viewModel.updateSelectRepeatOption(repeatOption)
            viewModel.updateRepeat(true)
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