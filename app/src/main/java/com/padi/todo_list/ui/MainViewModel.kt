package com.padi.todo_list.ui

import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.transformToListReminderTime
import com.padi.todo_list.common.extension.transformToListSubtask
import com.padi.todo_list.common.utils.DateStatusTask
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.repository.AlarmRepositoryImpl
import com.padi.todo_list.data.local.repository.ManageCategoryRepository
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.ui.task.enum_class.DateSelectionEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepo: AlarmRepositoryImpl,
    private val newTaskRepo: NewTaskRepositoryImpl,
    private val widgetSettingRepo: WidgetSettingRepositoryImpl,
    private val manageCatRepo: ManageCategoryRepository
) : SharedActivityViewModel(alarmRepo, newTaskRepo, widgetSettingRepo, manageCatRepo) {

    fun addNewTask(name: String) {
        val dateStatus = if (_dateSelectionLiveData.value!! == DateSelectionEnum.NO_DATE) {
            DateStatusTask.NO_DATE
        } else {
            DateStatusTask.HAS_DATE
        }
        val offsetList = _listOffsetTimeDefaultLiveData.value!!
        val output = _eventTask.value!!.copy(
            name = name,
            timeStatus = UtilsJ.getDateTimeTaskValue(_clockModelLiveData.value!!),
            dateStatus = dateStatus,
            hasRemind = (offsetList.any { it.isChecked })
        )
        newTaskRepo.insertTask(output).subscribeOn(SchedulerProvider.io())
            .flatMapCompletable { insertedId ->
                val remindList = offsetList.transformToListReminderTime(
                    insertedId, output.dueDate!!
                )
                val subtaskList =
                    _listSubTask.value?.transformToListSubtask(insertedId) ?: ArrayList()

                newTaskRepo.insertSubtask(ArrayList(subtaskList))
                    .andThen(newTaskRepo.setStateSubTask(insertedId, subtaskList.size>0))
                    .andThen(newTaskRepo.insertReminderTime(ArrayList(remindList)))
            }.observeOn(SchedulerProvider.ui()).subscribeBy(onComplete = {
                _isInsertTaskSuccess.value = true
                Timber.d("CHECK-Notify: Add new task success: ")
                Timber.d("Add new task success: ")
            }, onError = { error ->
                Timber.e("Add new task error: $error")
            }).addTo(subscriptions)
    }

}