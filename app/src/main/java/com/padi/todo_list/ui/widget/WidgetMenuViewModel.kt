package com.padi.todo_list.ui.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.EventTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WidgetMenuViewModel @Inject constructor(private val eventTaskDao: EventTaskDao) :
    BaseViewModel() {

    private val _listTaskLiveData = MutableLiveData<ArrayList<EventTaskEntity>>()
    var listTaskLiveData: LiveData<ArrayList<EventTaskEntity>> = _listTaskLiveData

    fun prepareData() {
        eventTaskDao.getAllEventTasks().subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    Timber.d("WidgetStandard - WidgetMenuViewModel success: ")
                    _listTaskLiveData.value = it as ArrayList<EventTaskEntity>
                }, onError = {
                    Timber.d("WidgetStandard - WidgetMenuViewModel: ${it.message}")
                }).add(subscriptions)
    }

}