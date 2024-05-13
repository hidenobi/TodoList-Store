package com.padi.todo_list.ui.task

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingFragment
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.invisible
import com.padi.todo_list.common.extension.parcelable
import com.padi.todo_list.common.extension.sendUpdateTaskFragmentIntent
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.showPopupMenu
import com.padi.todo_list.common.extension.showPopupMenuFlags
import com.padi.todo_list.common.extension.startAnimTranslateY
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.IntentConstants
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.FragmentTaskBinding
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.detail_task.DetailTaskActivity
import com.padi.todo_list.ui.main.ACTION_COMPLETE_SRC_RETURN
import com.padi.todo_list.ui.main.ACTION_COMPLETE_TASK
import com.padi.todo_list.ui.manageCategory.ManageCategoryActivity
import com.padi.todo_list.ui.setting.SettingActivity
import com.padi.todo_list.ui.task.adapter.ItemTouchHelperCallback
import com.padi.todo_list.ui.task.adapter.OnClickListener
import com.padi.todo_list.ui.task.adapter.OnClickTabTitleListener
import com.padi.todo_list.ui.task.adapter.TabCategoryAdapter
import com.padi.todo_list.ui.task.adapter.TaskAdapter
import com.padi.todo_list.ui.task.dialog.ArrangeTaskDialog
import com.padi.todo_list.ui.task.dialog.DeleteDialog
import com.padi.todo_list.ui.task.dialog.date.DateDialog
import com.padi.todo_list.ui.task.dialog.new_task.NewTaskBottomSheet
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.task.model.TabCategory
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TaskFragment :
    BaseBindingFragment<TaskViewModel, FragmentTaskBinding>(R.layout.fragment_task) {
    override val viewModel: TaskViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var tabIdCategory : Long = -1
    private var _showStatus: Status = Status.IDLE
    private var mediaPlayer: MediaPlayer? = null
    private val tabTitleAdapter: TabCategoryAdapter by lazy {
        TabCategoryAdapter(arrayListOf(), object : OnClickTabTitleListener {
            override fun onClick(item: TabCategory) {
                tabCateGoryClick(item)
                tabIdCategory = item.id
            }
        })
    }
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(arrayListOf(), object : OnClickListener {
            override fun onClick(item: EventTaskUI) {
                if(item.isTextComplete) {
                    val addIntent = Intent(ACTION_COMPLETE_TASK)
                    LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(addIntent)
                } else {
                    onClickTitleTask(item)
                }
            }

            override fun onClickItem(item: EventTaskUI,position: Int) {
                _showStatus = Status.SHOW_DETAIL_TASK
                viewModel.getEventTaskInput(item.id!!)
            }

            override fun onClickCheckbox(item: EventTaskUI,position: Int) {
                repeatTask(item.repeat,item.id!!)
                clickCheckbox(item.isCompleted,position)
                mainViewModel.updateStandardWidget()

            }

        },
            onFlagClick = { view, _, position ->
                showPopupMenuFlags(requireContext(), view,
                    onFlagClick = { flagType ->

                        viewModel.updateFlagType(flagType, position)
                    })


            },
            onDeleteClick = { item, position ->
                val dialogDelete = DeleteDialog(onOK = {

                    taskAdapter.removeItem(position)
                    viewModel.deleteEventTaskById(item)
                })
                dialogDelete.show(childFragmentManager, DeleteDialog::javaClass.name)
            },
            onStarClick = { _, position ->
                viewModel.updateEventTask(position)
            },
            onCalenderClick = {item, position ->
                _showStatus = Status.SHOW_CALENDAR
                viewModel.getEventTaskInput(item.id!!)
            })

    }



    private fun showCalender(input: EventTaskEntity) {
        val bundle = Bundle().apply {
            putParcelable(BundleKey.KEY_EVENT_TASK, input)
        }

        val dialog = DateDialog(onClickDone = { entity, listOffsetTime ->
            viewModel.updateDueDateEventTask(entity,listOffsetTime)
        })
        dialog.arguments = bundle
        dialog.show(childFragmentManager, DateDialog::javaClass.name)
    }
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_COMPLETE_SRC_RETURN -> { getSort() }
                ACTION_UPDATE_UI_TASK_FRAGMENT -> {
                    viewModel.getAllCategory(tabIdCategory,requireContext().getString(R.string.all))
                    getSort()
                    mainViewModel.resetLiveDataOfBottomSheet(getString(R.string.no_category))
                    mainViewModel.updateStandardWidget()
                    mainViewModel.setAlarm()
                }

                ACTION_SELECT_CATEGORY -> {
                    getTabCategory(intent)
                }
                SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT, WidgetStandard.MARK_DONE_TASK_ACTION -> {
                    getSort()
                }
                INSERTED_NEW_CATEGORY -> {
                    viewModel.getAllCategory(tabIdCategory,context!!.getString(R.string.all))
                }
            }
        }
    }

    companion object{
        const val ACTION_UPDATE_UI_TASK_FRAGMENT = "ACTION_UPDATE_UI_TASK_FRAGMENT"
        const val ACTION_SELECT_CATEGORY = "ACTION_SELECT_CATEGORY"
        const val INSERTED_NEW_CATEGORY = "INSERTED_NEW_CATEGORY"
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        viewModel.getAllCategory(tabIdCategory, requireContext().getString(R.string.all))
        setupCategoryAdapter()
        setupTaskAdapter()
        initAction()
        observeData()
        getSort()
        initBroadcast()
    }

    private fun initBroadcast() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_COMPLETE_SRC_RETURN)
            addAction(ACTION_UPDATE_UI_TASK_FRAGMENT)
            addAction(SettingActivity.ACTION_CHANGE_DATE_TIME_FORMAT)
            addAction(WidgetStandard.MARK_DONE_TASK_ACTION)
            addAction(INSERTED_NEW_CATEGORY)
            addAction(ACTION_SELECT_CATEGORY)
        }

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            intentFilter
        )
    }
    private fun getTabCategory(intent: Intent) {
        val bundle = intent.extras
        val selectedItem = bundle?.parcelable<TabCategory>(IntentConstants.item)
        val selectedPosition = bundle?.getInt(IntentConstants.position)
        selectedItem?.let {
            tabCateGoryClick(it)
            tabTitleAdapter.updateCategoryAtPosition(selectedPosition!!, it)
            val layoutManager = viewBinding.rvCategory.layoutManager
            if (layoutManager is LinearLayoutManager){
                layoutManager.scrollToPosition(selectedPosition)
            }
            tabIdCategory = it.id
        }
    }

    private fun getSort() {
        if (tabIdCategory == -1L){
            viewModel.getAllTasks()
        }else {
            viewModel.getEventTasksByCategoryId(tabIdCategory)
        }
    }
    private fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun observeData() {
        viewBinding.apply {
            viewModel.run {
                listTab.observe(viewLifecycleOwner) {
                    it.let { tabTitleAdapter.setData(it) }
                }
                listTask.observe(viewLifecycleOwner) {
                    isTutorialEmpty = it.isNullOrEmpty() && tabIdCategory == -1L
                    isBackgroundEmpty = it.isNullOrEmpty()
                    if (it.isNullOrEmpty() && tabIdCategory == -1L) {
                        imgEmptyAdd.startAnimTranslateY(requireContext())
                        textEmpty.startAnimTranslateY(requireContext())
                    } else {
                        imgEmptyAdd.clearAnimation()
                        textEmpty.clearAnimation()

                    }
                    it.let { taskAdapter.setData(it) }
                    it.let { itemTouchHelperCallback.setData(it) }
                }
                listTaskSearch.observe(viewLifecycleOwner) {
                    if (it.isNullOrEmpty()) {
                        rvTask.invisible()
                        imgEmpty.invisible()
                        imgEmptyAdd.gone()
                        textEmpty.gone()
                        imgEmptyAdd.clearAnimation()
                        textEmpty.clearAnimation()
                        fab.clearAnimation()
                    }
                    rvTask.visible()
                    it.let { taskAdapter.setData(it) }
                    it.let { itemTouchHelperCallback.setData(it) }

                }

                eventTaskInputLiveData.observe(viewLifecycleOwner) {
                    if (_showStatus == Status.SHOW_CALENDAR) {
                        showCalender(it)
                    } else if (_showStatus == Status.SHOW_DETAIL_TASK) {
                        DetailTaskActivity.start(requireContext(), it)
                    }
                    _showStatus = Status.IDLE
                }
                duplicate.observe(viewLifecycleOwner) {
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Only if you need to restore open/close state when
        // the orientation is changed
        taskAdapter.saveStates(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        taskAdapter.restoreStates(savedInstanceState)
    }

    private fun setupCategoryAdapter() {
        viewBinding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvCategory.adapter = tabTitleAdapter
    }
    private val itemTouchHelperCallback = ItemTouchHelperCallback(object : ItemTouchHelperCallback.OnSwipeListener {
        override fun onSwipeRight(item: EventTaskUI,position: Int) {
            clickCheckbox(item.isCompleted,position)
        }
    }, arrayListOf())

    private fun setupTaskAdapter() {
        viewBinding.apply {
            rvTask.layoutManager = LinearLayoutManager(requireContext())
            rvTask.adapter = taskAdapter
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(rvTask)


        }
    }

    private fun initAction() {
        viewBinding.apply {
            menuItem.setOnDebounceClickListener(750) {
                showPopupMenu(requireContext(), it,
                    onClickManager = {
                        val intent = Intent(context, ManageCategoryActivity::class.java)
                        startActivity(intent)
                    }, onClickSearch = {
                        rvCategory.invisible()
                        search.visible()
                        menuItem.invisible()
                        searchTask()
                    }, onCLickArrange = {
                        showDialogArray()
                    })
            }
            btnBack.setOnDebounceClickListener(750) {
                rvCategory.visible()
                search.invisible()
                menuItem.visible()
                edtSearch.text = null
                getSort()
                hideKeyboard(search)
            }
            fab.setOnDebounceClickListener(750) {
                mainViewModel.resetLiveDataOfBottomSheet(getString(R.string.no_category))
                val bottomSheet =
                    NewTaskBottomSheet(onClickInsert = { event ->
                        getSort()
                    })
                val bundle = Bundle()
                bundle.putLong(BundleKey.KEY_CATEGORY_ID, tabIdCategory)
                bottomSheet.arguments = bundle
                bottomSheet.show(childFragmentManager, NewTaskBottomSheet::class.java.simpleName)
            }
        }
    }
    private fun showDialogArray(){
        val newCategoryDialog = ArrangeTaskDialog(
            onSave = {
                if (tabIdCategory == -1L) {
                    viewModel.getAllTasks()
                } else {
                    viewModel.getEventTasksByCategoryId( tabIdCategory)
                }

            }
        )
        newCategoryDialog.show(
            childFragmentManager,
            ArrangeTaskDialog::javaClass.name
        )
    }

    private fun onClickTitleTask(item: EventTaskUI) {
        if (item.isTitle) {
            for (i in 0 until taskAdapter.itemCount) {
                val currentItem = taskAdapter.getItem(i)
                if (currentItem.typeTask == item.typeTask && !currentItem.isTitle) {
                    currentItem.isSelected = !currentItem.isSelected
                    taskAdapter.notifyItemChanged(i)
                }
            }
        }

    }

    private fun tabCateGoryClick(item: TabCategory) {
        tabTitleAdapter.listTabTitle.forEach { it.isSelected = false }
        item.isSelected = true
        tabTitleAdapter.notifyItemRangeChanged(0, tabTitleAdapter.itemCount)
        if (item.id == -1L) viewModel.getAllTasks()
        else {
            viewModel.getEventTasksByCategoryId(item.id)
        }
    }

    private fun searchTask() {
        viewBinding.edtSearch.addTextChangedListener{name ->
            viewModel.filterTasksByName(tabIdCategory,name.toString())
        }

    }
    private fun clickCheckbox( isCheck : Boolean,position: Int) {
        viewModel.updateIsChecked(position)
        requireContext().sendUpdateTaskFragmentIntent()
        if (!isCheck && AppPrefs.getCompleteTone()) {
            setRingtoneComplete()
        }
    }
    private fun repeatTask(repeat: Int, id: Long) {
        if (repeat != 0) viewModel.repeatTask(id) else return

    }
    private fun setRingtoneComplete(){
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ringtone)
        mediaPlayer?.start()
    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
        super.onDestroy()
        mediaPlayer?.release()
    }
    enum class Status {
        IDLE, SHOW_CALENDAR, SHOW_DETAIL_TASK
    }
}