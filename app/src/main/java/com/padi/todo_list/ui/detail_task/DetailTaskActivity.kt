package com.padi.todo_list.ui.detail_task

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.BuildConfig
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.parcelable
import com.padi.todo_list.common.extension.sendUpdateTaskFragmentIntent
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showAlert
import com.padi.todo_list.common.extension.showDetailMenu
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.DateTimeFormat
import com.padi.todo_list.common.utils.TimeStatusTask
import com.padi.todo_list.common.utils.ItemHelperCallback
import com.padi.todo_list.common.utils.ItemTouchListener
import com.padi.todo_list.common.utils.PermissionUtils
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.getTimeSelectionEnumInput
import com.padi.todo_list.common.utils.UtilsJ.shareTask
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityDetailTaskBinding
import com.padi.todo_list.ui.task.dialog.DeleteDialog
import com.padi.todo_list.ui.task.dialog.date.DateDialog
import com.padi.todo_list.ui.task.dialog.new_category.NewCategoryDialog
import com.padi.todo_list.ui.task.dialog.reminder.ReminderDialog
import com.padi.todo_list.ui.task.dialog.repeat.RepeatDialog
import com.padi.todo_list.ui.task.dialog.time.TimeDialog
import com.padi.todo_list.ui.task.enum_class.TimeSelectionEnum
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.RepeatOptions
import com.padi.todo_list.ui.task.popup.CategoryArrayAdapter
import com.padi.todo_list.ui.task.popup.CustomListPopupWindowBuilder
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailTaskActivity :
    BaseBindingActivity<DetailTaskViewModel, ActivityDetailTaskBinding>(R.layout.activity_detail_task) {
    companion object {
        fun start(
            context: Context,
            entity: EventTaskEntity,
        ) {
            val intent = Intent(context, DetailTaskActivity::class.java).apply {
                putExtra(BundleKey.KEY_EVENT_TASK, entity)
            }
            context.startActivity(intent)
        }
    }

    override val viewModel: DetailTaskViewModel by viewModels()
    private lateinit var mEventInput: EventTaskEntity

    private val updateNoteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NoteActivity.ACTION_UPDATE_NOTE -> {
                    getData(mEventInput.id)
                }
                NoteActivity.ACTION_UPDATE_USED_CREATED_NOTE -> {
                   viewBinding.eventTask!!.usedCreateNote = true
                }
            }
        }
    }

    private val itemTouchHelper = ItemTouchHelper(ItemHelperCallback(object : ItemTouchListener {
        override fun onRowMoved(fromPosition: Int, toPosition: Int) {
            viewModel.move(fromPosition, toPosition, subTaskAdapter.getData())
            subTaskAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onRowSelected(viewHolder: RecyclerView.ViewHolder?) {

        }

        override fun onRowClear(viewHolder: RecyclerView.ViewHolder?) {

        }
    }))

    private val subTaskAdapter: SubTasksAdapter by lazy {
        SubTasksAdapter(
            data = mutableListOf(),
            onDrag = {
                itemTouchHelper.startDrag(it)
            },
            detailTaskViewModel = viewModel
        )
    }

    private val openSettingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                openFile()
            }

            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false) -> {
                openFile()
            }

            else -> {
                showAlertDialog()
            }
        }
    }

    private lateinit var categoryAdapter: CategoryArrayAdapter
    private lateinit var listPopupWindow: ListPopupWindow
    private var mIsRemind = false

    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        getIntentData()
        setupRecyclerViewSubtask()
        viewModel.fetchCategoryOfPopup(this.getString(R.string.no_category),this.getString(R.string.create_new))
        action()
        initCategoryPopup()
        observeData()
    }

    private fun getIntentData() {
        mEventInput = intent.parcelable(BundleKey.KEY_EVENT_TASK) ?: EventTaskEntity()
        if (mEventInput.id != 0L) {
            viewModel.getEventTaskInput(mEventInput.id)
            viewModel.getCategory(mEventInput.categoryId!!,getString(R.string.no_category))
            viewModel.updateRepeatOption(RepeatOptions.getInstance(mEventInput.repeat))

            getData(mEventInput.id)
            viewModel.getListSubTask(mEventInput.id, mEventInput.isCompleted)
            viewModel.updateTimeSelection(
                getTimeSelectionEnumInput(mEventInput),
                UtilsJ.getClockModelInput(mEventInput)
            )
            mIsRemind = mEventInput.hasRemind
            viewModel.updateListOffsetTimeDefault(mEventInput)

            viewModel.updateTask(mEventInput.copy(timeStatus = UtilsJ.getDateTimeTaskValue(UtilsJ.getClockModelInput(mEventInput))), isUpdateWithListOffset = false)
            regisUpdateNoteReceiver()
        }
    }

    private fun regisUpdateNoteReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(NoteActivity.ACTION_UPDATE_NOTE)
            addAction(NoteActivity.ACTION_UPDATE_USED_CREATED_NOTE)
        }
        LocalBroadcastManager.getInstance(this@DetailTaskActivity).registerReceiver(
            updateNoteReceiver,
            intentFilter
        )
    }

    private fun action() {
        viewBinding.apply {
            tbDetail.handleAction(leftAction = {
                finish()
            })

            edtTaskName.addTextChangedListener {
                if (it.isNullOrBlank()) {
                    edtTaskName.hint = eventTask!!.name
//                    viewModel.updateNameTask(eventTask!!, eventTask!!.name.toString())
                    viewModel.updateTask(eventTask!!.copy(name = eventTask!!.name.toString()), isUpdateWithListOffset = false)
                } else {
//                    viewModel.updateNameTask(eventTask!!, it.toString())
                    viewModel.updateTask(eventTask!!.copy(name = it.toString()), isUpdateWithListOffset = false)
                }
            }

            tvCategory.setOnDebounceClickListener(750) {
                listPopupWindow.show()
            }
            addSubTask.setOnDebounceClickListener(750) {
                if (!eventTask!!.isCompleted) viewModel.addOneSubTask()
            }
            llDueDate.setOnDebounceClickListener(1000) {
                if(!eventTask!!.isCompleted) showCalender()
            }
            llNote.setOnDebounceClickListener {
                NoteActivity.start(this@DetailTaskActivity, eventTask!!.name, eventTask!!.id, eventTask!!.usedCreateNote)
            }
            llRepeat.setOnDebounceClickListener(750) {
                if (!eventTask!!.isCompleted) showRepeatDialog()
            }
            timeReminder.setOnClickListener() {
                if (!eventTask!!.isCompleted) showTimeDialog()
            }
            layoutReminder.setOnDebounceClickListener(750) {
                if (!eventTask!!.isCompleted) showRemindTimeDialog()
            }
            attachFile.setOnDebounceClickListener {
                if (!eventTask!!.isCompleted) choosePhoto()
            }
            delete.setOnDebounceClickListener {
                viewModel.deleteImage(eventTask!!.id)
                imgFile.gone()
                delete.gone()
            }
            menuItem.setOnDebounceClickListener {
                showDetailMenu(
                    isComplete = eventTask!!.isCompleted,
                    context = this@DetailTaskActivity,
                    anchorView = it,
                    onClickMenu = { str ->
                        when (str) {
                            KEY_MARK_DONE -> viewModel.markDoneTask(eventTask!!.id, !eventTask!!.isCompleted)
                            KEY_DUPLICATE -> viewModel.duplicateTask(resources.getString(R.string.copy), eventTask!!.id)
                            KEY_SHARE -> shareTask(
                                this@DetailTaskActivity,
                                eventTask!!,
                                subTaskAdapter.getData()
                            )

                            else -> {
                                DeleteDialog(onOK = {
                                    viewModel.deleteTask(eventTask!!.id)
                                }).apply {
                                    show(supportFragmentManager, DeleteDialog::javaClass.name)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun showRemindTimeDialog() {
        val clockModel = viewModel.clockModelLiveData.value!!
        val bundle = Bundle()
        bundle.putSerializable(BundleKey.IS_REMIND, mIsRemind)
        bundle.putSerializable(BundleKey.KEY_CLOCK_MODEL, clockModel)
        bundle.putSerializable(
            BundleKey.KEY_LIST_REMIND_TIME_UI,
            viewModel.listOffsetTimeDefaultLiveData.value
        )
        val reminderDialog = ReminderDialog(
            onClickCancel = { outputCancel ->
                viewModel.updateListOffsetTimeDefault(outputCancel)
            },
            onClickDone = { allOffsetTimeOutput ->
            viewModel.updateTask(
                viewBinding.eventTask!!,
                allOffsetTimeOutput as ArrayList<OffsetTimeUI>,
                isUpdateWithListOffset = true
            )
            viewModel.updateListOffsetTimeDefault(allOffsetTimeOutput)
            if( allOffsetTimeOutput.any { it.isChecked }) showOverlayPerDialog()
        })
        reminderDialog.arguments = bundle
        reminderDialog.show(supportFragmentManager, ReminderDialog::javaClass.name)
    }

    private fun showRepeatDialog() {
        val bundle = Bundle()
        bundle.apply {
            putBoolean(
                BundleKey.IS_REPEAT,
                viewModel.repeatOptionLiveData.value!!.isSelected
            )
            putSerializable(
                BundleKey.KEY_REPEAT_OPTION,
                viewModel.repeatOptionLiveData.value!!
            )
            putSerializable(
                BundleKey.KEY_CLOCK_MODEL,
                viewModel.clockModelLiveData.value!!
            )
        }
        val repeatDialog = RepeatDialog(onClickDone = { repeatOption ->
            viewModel.updateTask(viewBinding.eventTask!!.copy(repeat = repeatOption.repeat), isUpdateWithListOffset = false)
            viewModel.updateRepeatOption(repeatOption)
        })
        repeatDialog.arguments = bundle
        repeatDialog.show(supportFragmentManager, RepeatDialog::javaClass.name)
    }

    private fun showTimeDialog() {
        val bundle = Bundle()
        bundle.putSerializable(
            BundleKey.KEY_CLOCK_MODEL,
            viewModel.clockModelLiveData.value!!
        )
        bundle.putSerializable(
            BundleKey.KEY_TYPE_TIME,
            viewModel.timeSelectionLiveData.value!!
        )
        val clockDialog = TimeDialog { typeTime, clockModel ->

            val mListOffset = if (typeTime == TimeSelectionEnum.NO_TIME) {
                OffsetTimeUI.getData(isNotSelect = true)
            } else {
                viewModel.listOffsetTimeDefaultLiveData.value
            }
            viewModel.updateTask(
                viewBinding.eventTask!!.copy(
                    dueDate = UtilsJ.convertTimeToMilliseconds(clockModel),
                    timeStatus = UtilsJ.getDateTimeTaskValue(clockModel)
                ),
                listOffset = mListOffset,
                isUpdateWithListOffset = true
            )
            viewModel.updateTimeSelection(typeTime, clockModel)
        }
        clockDialog.arguments = bundle
        if (!clockDialog.isAdded && supportFragmentManager.findFragmentByTag(TimeDialog::javaClass.name) == null) {
            clockDialog.show(supportFragmentManager, TimeDialog::javaClass.name)
            supportFragmentManager.executePendingTransactions()
        }
    }

    private fun showAlertDialog() {
        showAlert(
            getString(R.string.permission),
            getString(R.string.request_permission_storage)
        ) {
            val intentLocationSettings = Intent()
            intentLocationSettings.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            intentLocationSettings.data = uri
            openSettingResult.launch(intentLocationSettings)
        }
    }

    private fun getData(idTask: Long) {
        viewModel.getData(idTask)
        viewModel.eventNoteLiveData.observe(this@DetailTaskActivity) {
            if (it != null && it.id != 0L) {
                viewBinding.eventNote = it
                viewBinding.groupNote.visible()
            } else {
                viewBinding.groupNote.gone()
            }
        }
        viewModel.eventFileImageUI.observe(this@DetailTaskActivity) {
            viewBinding.eventFileImage = it
            if(it != null) viewBinding.imgFile.visible()
        }
    }

    private fun observeData() {
        viewModel.run {
            eventTaskLiveData.observe(this@DetailTaskActivity) {
                viewBinding.eventTask = it
                setDueDateText(it)
                visibleLayoutRemind(it)
                disableLayoutByNoDate(it)
                setAlphaLayoutWithNoDate(it)

            }
            category.observe(this@DetailTaskActivity) { category ->
                viewBinding.category = category
                categoryAdapter.updateSelectItem(category.id.toInt())
            }
            listCategory.observe(this@DetailTaskActivity) {
                updatePopupData(it)
            }

            repeatOptionLiveData.observe(this@DetailTaskActivity) {
                if (it.id != RepeatOptions.NOT_INIT_ID) {
                    viewBinding.txtRepeat.text = viewModel.repeatOptionLiveData.value!!.name
                } else {
                    viewBinding.txtRepeat.text = getString(R.string.not_available)
                }
            }

            isInsertCategorySuccess.observe(this@DetailTaskActivity) {
                if (it == true) {
                    viewModel.listCategory.value?.let { it1 -> updatePopupData(it1) }
                    viewModel.updateTask(viewBinding.eventTask!!.copy(categoryId = viewBinding.category!!.id), isUpdateWithListOffset = false)
                }
            }

            viewModel.clockModelLiveData.observe(this@DetailTaskActivity) {
//                mIsRemind = !(it.hour == ClockModel.NO_HOUR || it.minute == ClockModel.NO_HOUR)
                setRemindTextView()
            }
            viewModel.listOffsetTimeDefaultLiveData.observe(this@DetailTaskActivity) {
                mIsRemind = it.any { model -> model.isChecked }
                setRemindTextView()
            }

            listSubTaskLiveData.observe(this@DetailTaskActivity) {
                subTaskAdapter.initData(it)
            }
            newTask.observe(this@DetailTaskActivity) {
                subTaskAdapter.addNewItem(it)
                viewBinding.rvSubTask.smoothScrollToPosition(subTaskAdapter.getData().size - 1)
            }
            markDone.observe(this@DetailTaskActivity) {
                subTaskAdapter.initData(subTaskAdapter.getData().map { sub ->
                    sub.copy(isEventComplete = viewBinding.eventTask!!.isCompleted)
                })
                showToast(this@DetailTaskActivity, getString(if (it) R.string.task_marked_as_completed else R.string.undone_successfully))
            }
            deleteTask.observe(this@DetailTaskActivity) {
                if (it) finish()
            }
            duplicate.observe(this@DetailTaskActivity) { item ->
                start(this@DetailTaskActivity, item)
                finish()
            }
        }
    }

    private fun setDueDateText(task: EventTaskEntity) {
        if (
            (task.dateStatus == DateStatusTask.NO_DATE)
        ) {
            viewBinding.dueDate.text = getString(R.string.no_date)
        } else {
            val dateTime =
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(task.dueDate!!),
                    ZoneId.systemDefault()
                )
            val formatDate = when (AppPrefs.getOptionDay()) {
                BundleKey.DAY_MONTH_YEAR -> dateTime.format(
                    DateTimeFormatter.ofPattern(
                        DateTimeFormat.DATE_TIME_FORMAT_7.value
                    )
                )

                BundleKey.MONTH_DAY_YEAR -> dateTime.format(
                    DateTimeFormatter.ofPattern(
                        DateTimeFormat.DATE_TIME_FORMAT_8.value
                    )
                )

                else -> dateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_TIME_FORMAT_6.value))
            }
            viewBinding.dueDate.text = formatDate.toString()
        }
    }

    private fun initCategoryPopup() {
        categoryAdapter =
            CategoryArrayAdapter(this@DetailTaskActivity, mEventInput.id.toInt()) {
                if (it.id != CategoryConstants.CREATE_NEW_ID) {
                    viewModel.updateTask(viewBinding.eventTask!!.copy(categoryId = it.id), isUpdateWithListOffset = false)
                    viewModel.updateCategory(it)
                    listPopupWindow.dismiss()
                } else {
                    val newCategoryDialog = NewCategoryDialog { newItem ->
                        viewModel.insertNewCategory(newItem,getString(R.string.all))
                        listPopupWindow.dismiss()
                    }
                    newCategoryDialog.show(
                        supportFragmentManager,
                        NewCategoryDialog::javaClass.name
                    )
                }
            }
    }

    private fun updatePopupData(data: ArrayList<CategoryEntity>) {
        categoryAdapter.setData(data)
        listPopupWindow = CustomListPopupWindowBuilder<CategoryEntity>(this@DetailTaskActivity)
            .setAnchor(viewBinding.tvCategory)
            .setList(data as List<CategoryEntity>)
            .setBackgroundDrawableRes(R.drawable.round_white_12sp)
            .setAdapter(categoryAdapter)
            .build()
    }

    private fun showCalender() {
        val bundle = Bundle()
        bundle.putParcelable(BundleKey.KEY_EVENT_TASK, viewBinding.eventTask!!)
        val dialog = DateDialog(onClickDone = { entity, listOffsetTime ->
            viewModel.updateTask(entity, listOffsetTime, isUpdateWithListOffset = true)
            viewModel.updateListOffsetTimeDefault(listOffsetTime)
            viewModel.updateRepeatOption(RepeatOptions.getInstance(entity.repeat))
            viewModel.updateTimeSelection(getTimeSelectionEnumInput(entity), UtilsJ.getClockModelInput(entity))
            if(entity.hasRemind) showOverlayPerDialog()
        })
        dialog.arguments = bundle
        if (!dialog.isAdded && supportFragmentManager.findFragmentByTag(TimeDialog::javaClass.name) == null) {
            dialog.show(supportFragmentManager, DateDialog::javaClass.name)
            supportFragmentManager.executePendingTransactions()
        }
        showNotifyDialog()
    }

    private fun setupRecyclerViewSubtask() {
        viewBinding.rvSubTask.apply {
            itemTouchHelper.attachToRecyclerView(this)
            adapter = subTaskAdapter
        }
    }


    private fun openFile() {
        TedImagePicker.with(this)
            .start { uri ->
                Timber.d("uri: $uri")
                viewModel.saveImage(uri.toString(), viewBinding.eventTask!!.id)
            }
    }

    private fun choosePhoto() {
        if (!PermissionUtils.checkStoragePermission(this@DetailTaskActivity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            } else {
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        } else {
            openFile()
        }
    }

    private fun visibleLayoutRemind(task: EventTaskEntity) {
        if (task.timeStatus == TimeStatusTask.NO_TIME) {
            viewBinding.layoutReminder.gone()
        } else {
            viewBinding.layoutReminder.visible()
        }
    }
    private fun disableLayoutByNoDate(task: EventTaskEntity) {
        if (task.dateStatus == DateStatusTask.NO_DATE) {
            viewBinding.timeReminder.isClickable = false
            viewBinding.llRepeat.isClickable = false
            viewBinding.layoutReminder.isClickable = false
        } else {
            viewBinding.timeReminder.isClickable = true
            viewBinding.llRepeat.isClickable = true
            viewBinding.layoutReminder.isClickable = true
        }
    }
    private fun setAlphaLayoutWithNoDate(task: EventTaskEntity) {
        if (task.isCompleted  || task.dateStatus == DateStatusTask.NO_DATE) {
            viewBinding.textRemind.alpha = 0.35f
            viewBinding.tvRemindTime.alpha = 0.35f
            viewBinding.txtTime.alpha = 0.35f
            viewBinding.tvTimeAndReind.alpha = 0.35f
            viewBinding.tvRepeatTask.alpha = 0.35f
            viewBinding.txtRepeat.alpha = 0.35f

        } else {
            viewBinding.textRemind.alpha = 1f
            viewBinding.tvRemindTime.alpha = 1f
            viewBinding.txtTime.alpha = 1f
            viewBinding.tvTimeAndReind.alpha = 1f
            viewBinding.tvRepeatTask.alpha = 1f
            viewBinding.txtRepeat.alpha = 1f
        }
    }

    private fun setRemindTextView() {
        val list =
            viewModel.listOffsetTimeDefaultLiveData.value!!.filter { model -> model.isChecked }
        val dueDate = UtilsJ.convertTimeToMilliseconds(viewModel.clockModelLiveData.value!!)
        val resultText = StringBuilder("")
        val listString = UtilsJ.getReminderString(dueDate, list)
        listString.forEachIndexed { index, str ->
            if (index == 0) {
                resultText.append(str)
            } else {
                resultText.append(" ,$str")
            }
        }
        if (resultText.toString().trim().isBlank()) {
            viewBinding.tvRemindTime.text = getString(R.string.not_available)
        } else {
            viewBinding.tvRemindTime.text = resultText
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sendUpdateTaskFragmentIntent()
        LocalBroadcastManager.getInstance(this@DetailTaskActivity)
            .unregisterReceiver(updateNoteReceiver)
    }


}