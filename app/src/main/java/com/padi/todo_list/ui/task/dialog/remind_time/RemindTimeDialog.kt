package com.padi.todo_list.ui.task.dialog.remind_time

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.parcelableArrayList
import com.padi.todo_list.common.extension.serializable
import com.padi.todo_list.common.extension.setPositionOfView
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.databinding.DialogTimeRemindBinding
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.task.dialog.adapter.ReminderTimeOffsetAdapter
import com.padi.todo_list.ui.task.dialog.custom_remind_time.CustomOffsetTimeDialog
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class RemindTimeDialog(
    private val onDismissDialog: (ArrayList<OffsetTimeUI>) -> Unit
) : BaseDialogFragment<DialogTimeRemindBinding>() {
    private val viewModel: MainViewModel by activityViewModels()
    private val timeOffsetAdapter: ReminderTimeOffsetAdapter by lazy {
        ReminderTimeOffsetAdapter {
            handleAdapterClick(it)
        }
    }
    private var posX by Delegates.notNull<Int>()
    private var posY by Delegates.notNull<Int>()
    private lateinit var _clock: ClockModel
    private lateinit var _allOffsetTime: ArrayList<OffsetTimeUI>

    override fun getLayout(): Int {
        return R.layout.dialog_time_remind
    }

    override fun initView() {
        isCancelable = true
        getArgument()
        setPositionOfView(x = null, y = posY + 20)
        setupAdapter()
        observeData()
    }

    private fun observeData() {
        viewModel.listOffsetTimeDefaultLiveData.observe(viewLifecycleOwner) {
            timeOffsetAdapter.setData(it)
        }
    }

    private fun getArgument() {
        posX = arguments?.getInt(BundleKey.POS_X)!!
        posY = arguments?.getInt(BundleKey.POS_Y)!!
        _clock = arguments?.serializable(BundleKey.KEY_CLOCK_MODEL)!!
        _allOffsetTime = arguments?.parcelableArrayList(BundleKey.KEY_LIST_REMIND_TIME_UI)!!
        viewModel.updateListOffsetTimeDefault(_allOffsetTime)
    }

    private fun setupAdapter() {
        binding.rvRemindTime.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = timeOffsetAdapter
        }
    }

    private fun handleAdapterClick(model: OffsetTimeUI) {
        if (model.position == OffsetTimeUI.POS_OFFSET_TIME_CUSTOM && !model.isChecked) {
            val customDlg = CustomOffsetTimeDialog { newData ->
                viewModel.updateCustomOffsetTime(newData)
            }
            val bundle = Bundle()
            bundle.putSerializable(BundleKey.KEY_CLOCK_MODEL, _clock)
            bundle.putParcelable(
                BundleKey.KEY_REMIND_TIME_UI,
                _allOffsetTime[OffsetTimeUI.POS_OFFSET_TIME_CUSTOM]
            )
            customDlg.arguments = bundle
            customDlg.show(childFragmentManager, CustomOffsetTimeDialog::javaClass.name)
        } else if (model.isChecked && model.position == OffsetTimeUI.POS_OFFSET_TIME_CUSTOM) {
            viewModel.updateCustomOffsetTime(model.copy(isChecked = false))
        } else {
            viewModel.updateOffsetTime(model)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.listOffsetTimeDefaultLiveData.value?.let { onDismissDialog.invoke(it) }
        val test = viewModel.listOffsetTimeDefaultLiveData.value
        Timber.d("${test?.size}")
    }
}