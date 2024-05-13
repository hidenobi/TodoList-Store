package com.padi.todo_list.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.utils.AlarmUtils
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.repository.AlarmRepositoryImpl
import com.padi.todo_list.data.local.repository.ManageCategoryRepository
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.task.enum_class.DateSelectionEnum
import com.padi.todo_list.ui.task.enum_class.TimeSelectionEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import com.padi.todo_list.ui.task.model.RepeatOptions
import com.padi.todo_list.ui.task.model.SubTaskUI
import com.padi.todo_list.ui.task.model.TabCategory
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import java.time.LocalDate

open class SharedActivityViewModel(
    private val alarmRepo: AlarmRepositoryImpl,
    private val newTaskRepo: NewTaskRepositoryImpl,
    private val widgetSettingRepo: WidgetSettingRepositoryImpl,
    private val manageCatRepo: ManageCategoryRepository,
) : BaseViewModel() {
    //  use in NewTaskBottomSheet :start
    private val _category = MutableLiveData<CategoryEntity>()
    val category: LiveData<CategoryEntity> get() = _category
    fun updateCategory(data: CategoryEntity) {
        _category.value = data
    }

    fun getCategory(idCategoryInput: Long,textNoCategory: String) {
        if (idCategoryInput == CategoryConstants.NO_CATEGORY_ID) {
            _category.value = CategoryEntity(
                CategoryConstants.NO_CATEGORY_ID,
               textNoCategory
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
    val listCategory: LiveData<ArrayList<CategoryEntity>> get() = _listCategory

    private val _listCategoryMenu = MutableLiveData<ArrayList<TabCategory>>()
    val listCategoryMenu: LiveData<ArrayList<TabCategory>> get() = _listCategoryMenu
    fun fetchCategoryOfPopup(noCategory: String, createNew: String) {
        newTaskRepo.getAllCategories().subscribeOn(SchedulerProvider.io())
            .map { list ->
                val output = list as ArrayList
                output.add(
                    0, CategoryEntity(
                        CategoryConstants.NO_CATEGORY_ID,
                        noCategory
                    )
                )
                output.add(
                    output.size, CategoryEntity(
                        CategoryConstants.CREATE_NEW_ID,
                        createNew
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

    fun getALlCategory(textAll :String) {
        manageCatRepo.getTabTaskCounts()
            .subscribeOn(SchedulerProvider.io())
            .map {
                val tempList = ArrayList<TabCategory>()
                tempList.addAll(it)
                tempList.add(
                    0,
                    TabCategory(
                        CategoryConstants.NO_CATEGORY_ID,
                        textAll
                    )
                )
                tempList
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = {
                _listCategoryMenu.postValue(it)
            }, onError = {
                Timber.d("fetchCategory error")
            }).add(subscriptions)
    }
    fun insertCategoryManager(category: CategoryEntity,textAll: String) {
        newTaskRepo.insertCategory(category, _listCategoryMenu.value!!.size)
            .subscribeOn(SchedulerProvider.io())
            .flatMap {
                manageCatRepo.getTabTaskCounts()
                    .map {
                        val tempList = ArrayList<TabCategory>()
                        tempList.addAll(it)
                        tempList.add(
                            0,
                            TabCategory(
                                CategoryConstants.NO_CATEGORY_ID,
                                textAll
                            )
                        )
                        tempList
                    }
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listCategoryMenu.postValue(it as ArrayList)
                    Timber.d("insert Category success")
                },
                onError = {
                    Timber.d("insert Category error")
                }
            ).add(subscriptions)
    }

    private val _isInsertCategorySuccess = MutableLiveData(false)
    val isInsertCategorySuccess: LiveData<Boolean> get() = _isInsertCategorySuccess
    fun insertNewCategory(category: CategoryEntity,textAll: String) {
        val existItem =
            _listCategory.value!!.firstOrNull { item -> item.name == category.name }
        if (existItem == null) {
            newTaskRepo.insertCategory(category, _listCategoryMenu.value!!.size)
                .subscribeOn(SchedulerProvider.io())
                .flatMap { id ->
                    newTaskRepo.getAllCategories()
                        .map { list ->
                            val output = list as ArrayList
                            output.add(
                                0, CategoryEntity(
                                    CategoryConstants.NO_CATEGORY_ID,
                                   textAll
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

    protected val _eventTask = MutableLiveData(EventTaskEntity())
    val eventTaskLiveData: LiveData<EventTaskEntity> = _eventTask
    fun updateEventLiveData(data: EventTaskEntity) {
        _eventTask.value = data
    }

    protected val _listOffsetTimeDefaultLiveData = MutableLiveData(OffsetTimeUI.getData(true))
    val listOffsetTimeDefaultLiveData: LiveData<ArrayList<OffsetTimeUI>> get() = _listOffsetTimeDefaultLiveData
    fun updateListOffsetTimeDefault(allOffsetTimeOutput: List<OffsetTimeUI>) {
        _listOffsetTimeDefaultLiveData.value = allOffsetTimeOutput as ArrayList<OffsetTimeUI>
    }

//    use in RemindTimeDialog   ----->
    fun updateCustomOffsetTime(newData: OffsetTimeUI) {
        val oldList = _listOffsetTimeDefaultLiveData.value
        oldList?.map { offsetTimeUI ->
            if (offsetTimeUI.position == OffsetTimeUI.POS_OFFSET_TIME_CUSTOM) {
                offsetTimeUI.isChecked = newData.isChecked
                offsetTimeUI.offsetTime = newData.offsetTime
                offsetTimeUI.text = newData.text
            }
        }
        _listOffsetTimeDefaultLiveData.value = oldList!!
    }

    fun updateOffsetTime(newData: OffsetTimeUI) {
        val oldList = _listOffsetTimeDefaultLiveData.value
        oldList?.map { offsetTimeUI ->
            if (offsetTimeUI.position == newData.position) {
                offsetTimeUI.isChecked = !newData.isChecked
            }
        }
        _listOffsetTimeDefaultLiveData.value = oldList!!
    }

//    use in RemindTimeDialog   <-----
    fun updateListOffsetTimeDefault(model: EventTaskEntity) {
        newTaskRepo.getReminderByEventId(model.id)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listOffsetTimeDefaultLiveData.postValue(it as ArrayList)
                    Timber.d("getReminderByEventId success: ${it.size}")
                },
                onError = {
                    Timber.d("getReminderByEventId error: ${it.message} \n ${it.cause}")
                },
            ).add(subscriptions)
    }

    protected val _isInsertTaskSuccess = MutableLiveData(false)
    val isInsertTaskSuccess: LiveData<Boolean> = _isInsertTaskSuccess

    fun setAlarm() {
        alarmRepo.getNextAlarm().subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(onSuccess = {
                AlarmUtils.startAlarm(Application.getInstance().applicationContext, it)
                Timber.d("CHECK-Notify: MainViewModel setAlarm")
                Timber.d("setAlarm-MainViewModel: success")
            }, onError = {
                Timber.d("CHECK-Notify-MainViewModel-setAlarm-error: ${it.message}")
            }

            ).addTo(subscriptions)
    }

    private val subtasks = ArrayList<SubTaskUI>()
    protected val _listSubTask = MutableLiveData<ArrayList<SubTaskUI>>().apply {
        value = subtasks
    }
    val listSubTaskLiveData: LiveData<ArrayList<SubTaskUI>> = _listSubTask

    open fun addOneSubTask() {
        subtasks.add(SubTaskUI(System.currentTimeMillis(), "", false))
        _listSubTask.value = subtasks
    }

    fun deleteSubtaskBottomSheet(subtask: SubTaskUI) {
        val idDeleted = subtask.id
        subtasks.removeIf { model -> model.id == idDeleted }
        _listSubTask.value = subtasks
    }

    fun tickSubtaskBottomSheet(subtask: SubTaskUI) {
        subtasks.map { subTaskUI ->
            if (subTaskUI.id == subtask.id) {
                subTaskUI.isDone = !subTaskUI.isDone
            }
        }
        _listSubTask.value = subtasks
    }

    fun updateSubtask(newContent: String, position: Int) {
        subtasks[position].content = newContent
    }

//  use in NewTaskBottomSheet :end


//  use in DateDialog :start

    protected val _dateSelectionLiveData = MutableLiveData(DateSelectionEnum.TODAY)
    val dateSelectionLiveData: LiveData<DateSelectionEnum> get() = _dateSelectionLiveData
    fun updateDateSelectionEnum(date: DateSelectionEnum) {
        _dateSelectionLiveData.value = date
    }

    private val _timeSelectionLiveData = MutableLiveData(TimeSelectionEnum.NO_TIME)
    val timeSelectionLiveData: LiveData<TimeSelectionEnum> get() = _timeSelectionLiveData
    protected val _clockModelLiveData = MutableLiveData(ClockModel.getInstance())
    val clockModelLiveData: LiveData<ClockModel> get() = _clockModelLiveData
    fun updateClockModel(clockModel: ClockModel) {
        _clockModelLiveData.value = clockModel
    }
    fun updateTimeSelection(time: TimeSelectionEnum, clockModel: ClockModel) {
        _timeSelectionLiveData.value = time
        _clockModelLiveData.value = clockModel
    }

    fun updateDateSelectionByYearMonthDay(year: Int, month: Int, dayOfMonth: Int) {
        _clockModelLiveData.value!!.dayOfMonth = dayOfMonth
        _clockModelLiveData.value!!.year = year
        _clockModelLiveData.value!!.month = month
    }

    private val _repeatOption = MutableLiveData(RepeatOptions())
    val repeatOptionLiveData: LiveData<RepeatOptions> get() = _repeatOption
    fun updateRepeatOption(newData: RepeatOptions) {
        _repeatOption.value = newData
    }

    private val _localeDateSelected = MutableLiveData(LocalDate.now())
    val localeDateSelected: LiveData<LocalDate> = _localeDateSelected
    fun updateLocaleDateSelected(date: LocalDate?) {
        _localeDateSelected.value = date
    }

//  use in DateDialog :end

    fun resetLiveDataOfBottomSheet(textNoCategory: String) {
        _listCategory.value = ArrayList()
        _category.value = CategoryEntity(
            CategoryConstants.NO_CATEGORY_ID,
            textNoCategory
        )
        _isInsertCategorySuccess.value = false
        _eventTask.value = EventTaskEntity()
        _isInsertTaskSuccess.value = false
        subtasks.clear()
        _listSubTask.value = ArrayList()
        _repeatOption.value = RepeatOptions()
        _clockModelLiveData.value = ClockModel.getInstance()
        _timeSelectionLiveData.value = TimeSelectionEnum.NO_TIME
        _dateSelectionLiveData.value = DateSelectionEnum.TODAY
        _localeDateSelected.value = LocalDate.now()
        _listOffsetTimeDefaultLiveData.value = OffsetTimeUI.getData(true)
    }

    fun updateStandardWidget() {
        widgetSettingRepo.getPrevData(
            AppPrefs.scopeTime,
            AppPrefs.scopeCategory,
            AppPrefs.showCompletedTask
        )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = { inputData ->
                    WidgetStandard.forceUpdateAll(
                        Application.getInstance().applicationContext,
                        inputData as ArrayList
                    )
                    Timber.d("updateStandardWidget success")
                },
                onError = {
                    Timber.d("updateStandardWidget error: ${it.message}")
                },
            )
            .add(subscriptions)
    }

    fun getEventTaskInput(id: Long) {
        newTaskRepo.getTaskById(id)
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui()).subscribeBy(
                onSuccess = {
                    _eventTask.value = it
                    Timber.d("getEventTaskInput success")
                },
                onError = {
                    Timber.d("getEventTaskInput - error: ${it.message}")
                }
            ).add(subscriptions)
    }

    private var _taskCountLiveData = MutableLiveData<Int>()
    val taskCountLiveData: LiveData<Int> get() = _taskCountLiveData
    fun getTaskCountAll() {
        newTaskRepo.getTabTaskCounts()
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui()).subscribeBy(
                onSuccess = {
                    _taskCountLiveData.value = it
                }
            )
            .add(subscriptions)
    }

}