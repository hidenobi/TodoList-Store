package com.padi.todo_list.ui.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityStarBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StarActivity : BaseBindingActivity<StarViewModel, ActivityStarBinding>(R.layout.activity_star) {
    override val viewModel: StarViewModel by viewModels()
    private val starTaskAdapter: StarTaskAdapter by lazy {
        StarTaskAdapter(
            onClickCheck = { _, index ->
                viewModel.updateIsChecked(index)
            },
            onClickStar = { _, index ->
                viewModel.updateEventTask(index)
            },
        )
    }
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        initRecycleView()
        setAction()
    }
    private fun initRecycleView() {
        viewBinding.apply {
            rvStarTask.layoutManager = LinearLayoutManager(this@StarActivity)
            rvStarTask.adapter = starTaskAdapter
        }

    }
    private fun setAction() {
        viewBinding.apply {
            tbStarTask.handleAction { finish() }
        }
        viewModel.run {
            listTask.observe(this@StarActivity) { list ->
                viewBinding.isEmpty = list.isNullOrEmpty()
                starTaskAdapter.setData(list)
                Timber.d("setAction   $list")
            }
        }
        viewModel.getAllStarTasks()
    }
}