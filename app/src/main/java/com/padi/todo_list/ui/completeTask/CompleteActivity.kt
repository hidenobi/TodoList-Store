package com.padi.todo_list.ui.completeTask

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.showPopupMenuFlags
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityCompleteTaskBinding
import com.padi.todo_list.ui.main.ACTION_COMPLETE_SRC_RETURN
import com.padi.todo_list.ui.detail_task.DetailTaskActivity
import com.padi.todo_list.ui.task.dialog.DeleteDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteActivity :
    BaseBindingActivity<CompleteViewModel, ActivityCompleteTaskBinding>(R.layout.activity_complete_task) {
    override val viewModel: CompleteViewModel by viewModels()
    private var _showStatus = Status.IDLE
    private val completeAdapter: CompleteTaskAdapter by lazy {
        CompleteTaskAdapter(
            onClickCheck = { _, index ->
                viewModel.updateIsChecked(index)
            },
            onClickFlag = { view, _, index ->
                showPopupMenuFlags(this@CompleteActivity, view,
                    onFlagClick = { flagType ->
                        viewModel.updateFlagType(flagType, index)
                    })
            },
            onClickItem = { item, _ ->
                _showStatus = Status.SHOW_DETAIL_TASK
                viewModel.getEventTaskInput(item.id!!)
            }
        )
    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.getAllTasks()
    }

    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        setAction()
        initRecycleView()
        viewModel.run {
            getAllTasks()
            listTask.observe(this@CompleteActivity) { list ->
                completeAdapter.submitList(list)
            }
            deleteAll.observe(this@CompleteActivity) {
                finish()
                viewModel.updateStandardWidget()
            }
            eventTaskInputLiveData.observe(this@CompleteActivity) { item ->
                if (_showStatus == Status.SHOW_DETAIL_TASK) {
                    val intent = Intent(this@CompleteActivity, DetailTaskActivity::class.java)
                    intent.putExtra(BundleKey.KEY_EVENT_TASK, item)
                    resultLauncher.launch(intent)
                }
                _showStatus = Status.IDLE
            }
        }
    }

    private fun initRecycleView() {
        viewBinding.rcvCompleteTask.run {
            layoutManager =
                LinearLayoutManager(this@CompleteActivity, LinearLayoutManager.VERTICAL, false)
            adapter = completeAdapter
        }
    }

    private fun setAction() {
        viewBinding.apply {
            toolbar.handleAction { finish() }
            toolbar.handleRightAction {showDeleteDialog()}
        }
    }

    private fun showDeleteDialog() {
        val dialogDelete =
            DeleteDialog(
                title = getString(R.string.txt_delete_all_task),
                onOK = {
                    viewModel.deleteAllTask()
                },
            )
        dialogDelete.show(supportFragmentManager, DeleteDialog::javaClass.name)
    }

    override fun onDestroy() {
        val addIntent = Intent(ACTION_COMPLETE_SRC_RETURN)
        LocalBroadcastManager.getInstance(this@CompleteActivity).sendBroadcast(addIntent)
        super.onDestroy()
    }

    enum class Status {
        IDLE, SHOW_DETAIL_TASK
    }

}