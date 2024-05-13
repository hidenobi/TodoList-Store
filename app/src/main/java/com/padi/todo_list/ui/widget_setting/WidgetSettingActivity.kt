package com.padi.todo_list.ui.widget_setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.common.utils.ScopeTask
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityWidgetSettingBinding
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.task.dialog.new_category.NewCategoryDialog
import com.padi.todo_list.ui.task.popup.CategoryArrayAdapter
import com.padi.todo_list.ui.task.popup.CustomListPopupWindowBuilder
import com.padi.todo_list.ui.widget_setting.adapter.PreWidgetStandardAdapter
import com.padi.todo_list.ui.widget_setting.adapter.TimeScopeArrayAdapter
import com.padi.todo_list.ui.widget_setting.model.TimeScopeModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WidgetSettingActivity :
    BaseBindingActivity<WidgetSettingViewModel, ActivityWidgetSettingBinding>(R.layout.activity_widget_setting) {
    override val viewModel: WidgetSettingViewModel by viewModels()
    private val prevAdapter: PreWidgetStandardAdapter by lazy { PreWidgetStandardAdapter(arrayListOf()) }
    private lateinit var listPopupWindow: ListPopupWindow
    private lateinit var categoryAdapter: CategoryArrayAdapter
    private lateinit var listTimePopupWindow: ListPopupWindow
    private lateinit var timeScopeAdapter: TimeScopeArrayAdapter
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        setupRecyclerView()
        setAction()
        setDefaultValue()
        initCategoryPopup()
        initTimeScopePopup()

        observeData()
    }

    private fun observeData() {
        viewModel.apply {
            boolCompletedTask.observe(this@WidgetSettingActivity) { bool ->
                viewBinding.incCompletedTask.swShowCompleted.isChecked = bool
                fetchPrevData()
            }
            timeScopeLiveData.observe(this@WidgetSettingActivity) { timeScope ->
                viewBinding.incTimeScope.timeScope = timeScope
                timeScopeAdapter.updateSelectItem(timeScope.scope)
                fetchPrevData()
            }
            prevData.observe(this@WidgetSettingActivity) { data ->
                prevAdapter.setData(data)
            }
            categoryLiveData.observe(this@WidgetSettingActivity) { category ->
                viewBinding.incCategoryScope.category = category
                categoryAdapter.updateSelectItem(category.id.toInt())
                fetchPrevData()
            }
            listCategoryLiveData.observe(this@WidgetSettingActivity) {
                updatePopupData(it)
            }
            viewModel.isInsertCategorySuccess.observe(this@WidgetSettingActivity) {
                if (it == true) {
                    viewModel.listCategoryLiveData.value?.let { it1 -> updatePopupData(it1) }
                }
            }
        }
    }

    private fun setDefaultValue() {
        viewModel.getCategory(AppPrefs.scopeCategory)
        viewModel.fetchCategoryOfPopup()
    }

    private fun initCategoryPopup() {
        categoryAdapter = CategoryArrayAdapter(
            this@WidgetSettingActivity,
            CategoryConstants.NO_CATEGORY_ID.toInt()
        ) {
            if (it.id != CategoryConstants.CREATE_NEW_ID) {
                viewModel.updateCategory(it)
                listPopupWindow.dismiss()
            } else {
                val newCategoryDialog = NewCategoryDialog { newItem ->
                    viewModel.insertNewCategory(newItem)
                    listPopupWindow.dismiss()
                }
                newCategoryDialog.show(supportFragmentManager, NewCategoryDialog::javaClass.name)
            }
        }
    }

    private fun updatePopupData(data: ArrayList<CategoryEntity>) {
        categoryAdapter.setData(data)
        listPopupWindow =
            CustomListPopupWindowBuilder<CategoryEntity>(this@WidgetSettingActivity)
                .setAnchor(viewBinding.incCategoryScope.tvCategoryScopePicker)
                .setList(data as List<CategoryEntity>)
                .setBackgroundDrawableRes(R.drawable.round_white_12sp)
                .setAdapter(categoryAdapter)
                .build()
    }

    private fun initTimeScopePopup() {
        timeScopeAdapter = TimeScopeArrayAdapter(
            this@WidgetSettingActivity,
            ScopeTask.ALL
        ) {
            viewModel.updateTimeScope(it)
            listTimePopupWindow.dismiss()
        }
        timeScopeAdapter.setData(viewModel.listTimeScopeLiveData.value!!)
        listTimePopupWindow =
            CustomListPopupWindowBuilder<TimeScopeModel>(this@WidgetSettingActivity)
                .setAnchor(viewBinding.incTimeScope.tvTimeScopePicker)
                .setList(viewModel.listTimeScopeLiveData.value!! as List<TimeScopeModel>)
                .setBackgroundDrawableRes(R.drawable.round_white_12sp)
                .setWidth(124)
                .setHeight(102)
                .setAdapter(timeScopeAdapter)
                .build()
    }

    private fun setupRecyclerView() {
        viewBinding.incRecyclerView.rcPrevData.apply {
            layoutManager =
                LinearLayoutManager(
                    this@WidgetSettingActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = prevAdapter
        }
    }

    private fun setAction() {
        viewBinding.tbWidget.handleAction(leftAction = {
            finish()
        })

        viewBinding.incCompletedTask.swShowCompleted.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateShowCompletedTask(isChecked)
        }

        viewBinding.btnSave.setOnDebounceClickListener {
            saveSetting()
        }

        viewBinding.incCategoryScope.root.setOnDebounceClickListener {
            listPopupWindow.show()
        }

        viewBinding.incTimeScope.root.setOnDebounceClickListener {
            listTimePopupWindow.show()
        }
    }

    private fun saveSetting() {
        AppPrefs.showCompletedTask = viewModel.boolCompletedTask.value!!
        AppPrefs.scopeCategory = viewModel.categoryLiveData.value!!.id
        AppPrefs.scopeTime = viewModel.timeScopeLiveData.value!!.scope
        WidgetStandard.forceUpdateAll(this@WidgetSettingActivity, viewModel.prevData.value!!)
        finish()
    }

}