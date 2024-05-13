package com.padi.todo_list.ui.task.dialog.new_task

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.sendUpdateTaskActivityIntent
import com.padi.todo_list.common.extension.setHeight
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.common.utils.ACTION_NOTIFY_PER
import com.padi.todo_list.common.utils.ACTION_OVERLAY_PER
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs.isFirstApp
import com.padi.todo_list.databinding.BottomSheetTaskDialogBinding
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.task.TaskFragment.Companion.INSERTED_NEW_CATEGORY
import com.padi.todo_list.ui.task.dialog.adapter.SubTaskAdapter
import com.padi.todo_list.ui.task.dialog.date.DateDialog
import com.padi.todo_list.ui.task.dialog.new_category.NewCategoryDialog
import com.padi.todo_list.ui.task.dialog.time.TimeDialog
import com.padi.todo_list.ui.task.popup.CategoryArrayAdapter
import com.padi.todo_list.ui.task.popup.CustomListPopupWindowBuilder
import com.padi.todo_list.ui.tutorial.TutorialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class NewTaskBottomSheet(private val onClickInsert: (event: EventTaskEntity) -> Unit) :
    BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: BottomSheetTaskDialogBinding
    private var idCategoryInput by Delegates.notNull<Long>()

    private val subTaskAdapter: SubTaskAdapter by lazy {
        SubTaskAdapter(
            onTickSubtask = {
                viewModel.tickSubtaskBottomSheet(it)
            },
            onChangeContentSubtask = { s, i ->
                viewModel.updateSubtask(s, i)
            },
            onClickDelete = {
                viewModel.deleteSubtaskBottomSheet(it)
            }
        )
    }

    private lateinit var categoryAdapter: CategoryArrayAdapter
    private lateinit var listPopupWindow: androidx.appcompat.widget.ListPopupWindow

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).apply {
                    isHideable = false
                }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetTaskDialogBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        binding.edtEventName.requestFocus()
        getArgument()
        viewModel.fetchCategoryOfPopup(requireContext().getString(R.string.no_category),requireContext().getString(R.string.create_new))
        setAction()
        setupRecyclerViewSubtask()
        initCategoryPopup()
        observeData()
    }

    private fun getArgument() {
        idCategoryInput = arguments?.getLong(BundleKey.KEY_CATEGORY_ID)!!
        viewModel.getCategory(idCategoryInput,getString(R.string.no_category))
        if (idCategoryInput != CategoryConstants.NO_CATEGORY_ID) {
            binding.tvCategory.setTextColor(resources.getColor(R.color.white, null))
            binding.tvCategory.setBackgroundResource(R.drawable.round_category_selected)
        }
    }

    private fun observeData() {
        viewModel.category.observe(viewLifecycleOwner) { category ->
            binding.category = category

            val event = viewModel.eventTaskLiveData.value
            viewModel.updateEventLiveData(event!!.copy(categoryId = category.id))
            categoryAdapter.updateSelectItem(category.id.toInt())
        }
        viewModel.eventTaskLiveData.observe(viewLifecycleOwner) {}
        viewModel.listSubTaskLiveData.observe(viewLifecycleOwner) {
            binding.rvSubTask.setHeight(it.size)
            subTaskAdapter.setData(it!!)
            if (it.size > 0) {
                binding.rvSubTask.smoothScrollToPosition(it.size - 1)
            }
        }

        viewModel.listCategory.observe(viewLifecycleOwner) {
            updatePopupData(it)
        }

        viewModel.isInsertCategorySuccess.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.listCategory.value?.let { it1 ->
                    updatePopupData(it1)
                    LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent().apply { action = INSERTED_NEW_CATEGORY })
                }
            }
        }

    }

    private fun initCategoryPopup() {
        categoryAdapter = CategoryArrayAdapter(requireContext(), idCategoryInput.toInt()) {
            if (it.id != CategoryConstants.CREATE_NEW_ID) {
                viewModel.updateCategory(it)
                listPopupWindow.dismiss()
            } else {
                val newCategoryDialog = NewCategoryDialog { newItem ->
                    viewModel.insertNewCategory(newItem,getString(R.string.all))
                    listPopupWindow.dismiss()
                }
                newCategoryDialog.show(childFragmentManager, NewCategoryDialog::javaClass.name)
            }
            binding.tvCategory.setTextColor(resources.getColor(R.color.white, null))
            binding.tvCategory.setBackgroundResource(R.drawable.round_category_selected)
        }
    }

    private fun updatePopupData(data: ArrayList<CategoryEntity>) {
        categoryAdapter.setData(data)
        listPopupWindow = CustomListPopupWindowBuilder<CategoryEntity>(requireContext())
            .setAnchor(binding.tvCategory)
            .setList(data as List<CategoryEntity>)
            .setBackgroundDrawableRes(R.drawable.round_white_12sp)
            .setAdapter(categoryAdapter)
            .build()
    }

    private fun setAction() {
        binding.edtEventName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                val textEntered = binding.edtEventName.text.toString()

                if (textEntered.isNotEmpty()) {
                    binding.edtEventName.text.toString()
                    binding.edtEventName.setSelection(binding.edtEventName.text!!.length)
                    binding.btnAdd.setImageResource(R.drawable.ic_button_add_new_task_enable)
                } else {
                    binding.btnAdd.setImageResource(R.drawable.ic_button_add_new_task_disable)
                }
            }
        })
        binding.btnAdd.setOnDebounceClickListener {
            addNewTask()
        }
        binding.tvCategory.setOnDebounceClickListener(750) {
            listPopupWindow.show()
        }
        binding.ivSubTasks.setOnDebounceClickListener {
            viewModel.addOneSubTask()
        }
        binding.ivCalender.setOnDebounceClickListener {
            showCalendarSettingDlg()
        }
    }


    private fun showCalendarSettingDlg() {
        val bundle = Bundle()
        val eventTask = viewModel.eventTaskLiveData.value
        bundle.putParcelable(BundleKey.KEY_EVENT_TASK, eventTask)
        bundle.putBoolean(BundleKey.KEY_MODE_CREATE_NEW, true)

        val dialog = DateDialog(onClickDone = { entity, listOffsetTime ->
            viewModel.updateEventLiveData(entity)
            viewModel.updateListOffsetTimeDefault(listOffsetTime)
            if(entity.hasRemind) LocalBroadcastManager.getInstance(requireActivity())
                .sendBroadcast(Intent(ACTION_OVERLAY_PER))
        })
        dialog.arguments = bundle
        if (!dialog.isAdded && childFragmentManager.findFragmentByTag(TimeDialog::javaClass.name) == null) {
            dialog.show(childFragmentManager, TimeDialog::javaClass.name)
            childFragmentManager.executePendingTransactions()
            LocalBroadcastManager.getInstance(requireActivity())
                .sendBroadcast(Intent(ACTION_NOTIFY_PER))
        }
    }

    private fun setupRecyclerViewSubtask() {
        binding.rvSubTask.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = subTaskAdapter
        }
    }

    private fun addNewTask() {
        binding.edtEventName.text.toString().trim().let {
            if (it.isBlank()) {
                showToast(requireContext(), getString(R.string.cannot_be_empty))
            } else {
                viewModel.addNewTask(it)
                onClickInsert.invoke(viewModel.eventTaskLiveData.value!!)
                requireContext().sendUpdateTaskActivityIntent()
                dismiss()
                if (isFirstApp){
                    val tutorialDialog = TutorialDialog(requireContext(),it)
                    tutorialDialog.show()
                    isFirstApp = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}