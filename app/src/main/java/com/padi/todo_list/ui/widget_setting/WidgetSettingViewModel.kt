package com.padi.todo_list.ui.widget_setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.ui.task.model.EventTaskUI
import com.padi.todo_list.ui.widget_setting.model.TimeScopeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WidgetSettingViewModel @Inject constructor(
    private val widgetSettingRepo: WidgetSettingRepositoryImpl,
    private val newTaskRepo: NewTaskRepositoryImpl,
) :
    BaseViewModel() {

    private val _boolCompletedTask = MutableLiveData(AppPrefs.showCompletedTask)
    val boolCompletedTask: LiveData<Boolean> = _boolCompletedTask
    fun updateShowCompletedTask(bool: Boolean) {
        _boolCompletedTask.value = bool
    }

    private val _timeScope = MutableLiveData(
        TimeScopeModel(
            AppPrefs.scopeTime,
            Application.getInstance().applicationContext.setUpLanguage().getString(R.string.all_tasks)
        )
    )
    val timeScopeLiveData: LiveData<TimeScopeModel> = _timeScope
    fun updateTimeScope(scope: TimeScopeModel) {
        _timeScope.value = scope
    }

    private val _listTimeScope = MutableLiveData(TimeScopeModel.getData())
    val listTimeScopeLiveData: LiveData<ArrayList<TimeScopeModel>> = _listTimeScope

    private val _prevData = MutableLiveData<ArrayList<EventTaskUI>>(arrayListOf())
    val prevData: LiveData<ArrayList<EventTaskUI>> = _prevData

    fun fetchPrevData() {
        widgetSettingRepo.getPrevData(
            _timeScope.value!!.scope,
            _category.value!!.id,
            _boolCompletedTask.value!!
        )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _prevData.value = (it as ArrayList)
                    Timber.d("fetchPrevData success")
                },
                onError = {
                    Timber.d("fetchPrevData error: ${it.message}")
                },
            )
            .add(subscriptions)
    }

    private val _category = MutableLiveData(
        CategoryEntity(
            id = CategoryConstants.NO_CATEGORY_ID,
            name = Application.getInstance().applicationContext.setUpLanguage().getString(R.string.all_tasks)
        )
    )
    val categoryLiveData: LiveData<CategoryEntity> get() = _category
    fun updateCategory(data: CategoryEntity) {
        _category.value = data
    }

    fun getCategory(idCategoryInput: Long) {
        if (idCategoryInput == CategoryConstants.NO_CATEGORY_ID) {
            _category.value = CategoryEntity(
                CategoryConstants.NO_CATEGORY_ID,
                Application.getInstance().applicationContext.setUpLanguage().getString(
                    R.string.all_tasks
                )
            )
        } else {
            newTaskRepo.getCategory(idCategoryInput).subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.ui()).subscribeBy(onSuccess = { category ->
                    Timber.d("get category success: ")
                    _category.value = category
                }, onError = { error ->
                    Timber.e("get category error: $error")
                }).addTo(subscriptions)
        }
    }

    private val _listCategory = MutableLiveData<ArrayList<CategoryEntity>>()
    val listCategoryLiveData: LiveData<ArrayList<CategoryEntity>> get() = _listCategory

    fun fetchCategoryOfPopup() {
        newTaskRepo.getAllCategories().subscribeOn(SchedulerProvider.io())
            .map { list ->
                val output = list as ArrayList
                output.add(
                    0, CategoryEntity(
                        CategoryConstants.NO_CATEGORY_ID,
                        Application.getInstance().applicationContext.setUpLanguage().getString(
                            R.string.all_tasks
                        )
                    )
                )
                output.add(
                    output.size, CategoryEntity(
                        CategoryConstants.CREATE_NEW_ID,
                        Application.getInstance().applicationContext.setUpLanguage().getString(
                            R.string.create_new
                        )
                    )
                )
                output
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = { onSuccess ->
                if (onSuccess.isNotEmpty()) {
                    Timber.d("fetchCategory success")
                    _listCategory.value = onSuccess
                }
            }, onError = {
                Timber.d("fetchCategory error")
            }).add(subscriptions)
    }

    private val _isInsertCategorySuccess = MutableLiveData(false)
    val isInsertCategorySuccess: LiveData<Boolean> get() = _isInsertCategorySuccess

    fun insertNewCategory(category: CategoryEntity) {
        val existItem =
            _listCategory.value!!.firstOrNull { item -> item.name == category.name }
        if (existItem == null) {
            newTaskRepo.insertCategory(category, _listCategory.value!!.size)
                .subscribeOn(SchedulerProvider.io())
                .flatMap { id ->
                    newTaskRepo.getAllCategories()
                        .map { list ->
                            val output = list as ArrayList
                            output.add(
                                0, CategoryEntity(
                                    CategoryConstants.NO_CATEGORY_ID,
                                    Application.getInstance().applicationContext.setUpLanguage().getString(
                                        R.string.all_tasks
                                    )
                                )
                            )
                            output.add(
                                output.size, CategoryEntity(
                                    CategoryConstants.CREATE_NEW_ID,
                                    Application.getInstance().applicationContext.setUpLanguage().getString(
                                        R.string.create_new
                                    )
                                )
                            )
                            Pair(id, output)
                        }
                        .toSingle()
                }
                .observeOn(SchedulerProvider.ui())
                .subscribeBy(
                    onSuccess = {
                        _listCategory.value = it.second
                        _category.value =
                            _listCategory.value!!.first { model -> model.id == it.first }
                        _isInsertCategorySuccess.value = true
                        Timber.d("insert Category success")
                    },
                    onError = {
                        Timber.d("insert Category error")
                    }
                ).add(subscriptions)
        }
    }

}