package com.padi.todo_list.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.ui.task.model.EventTaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StarViewModel @Inject constructor(
    private val taskDao: EventTaskDao
) : BaseViewModel() {
    private val _listTask = MutableLiveData<ArrayList<EventTaskUI>>()
    val listTask: LiveData<ArrayList<EventTaskUI>> = _listTask

    fun getAllStarTasks() {
        taskDao.getStarTask()
            .subscribeOn(SchedulerProvider.io())
            .map { it ->
                it.map {
                    EventTaskUI.newInstance(it)
                }
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _listTask.postValue(it as ArrayList)
                },
                onError = {
                    Timber.d("getAllStarTasks fail: ${it.message}")
                })
            .add(subscriptions)
    }

    fun updateIsChecked(position: Int) {
        _listTask.value?.getOrNull(position)?.let { item ->
            item.id?.let { itemId ->
                taskDao.getEvenByID(itemId)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { entity ->
                        item.isCompleted = !item.isCompleted
                        taskDao.update(entity.copy(isCompleted = item.isCompleted))
                    }
                    .observeOn(SchedulerProvider.ui())
                    .subscribeBy(
                        onComplete = {
                            val list = _listTask.value
                            list!!.removeAt(position)
                            list.add(position, item)
                            _listTask.postValue(list!!)
                        }
                    )
                    .add(subscriptions)
            }
        }
    }

    fun updateEventTask(position: Int) {
        _listTask.value?.getOrNull(position)?.let { item ->
            item.id?.let { itemId ->
                taskDao.getEvenByID(itemId)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { entity ->
                        item.isStar = !item.isStar
                        taskDao.update(entity.copy(isStar = item.isStar))
                    }
                    .observeOn(SchedulerProvider.ui())
                    .subscribeBy(
                        onComplete = {
                            val list = _listTask.value
                            list!!.removeAt(position)
                            list.add(position, item)
                            _listTask.postValue(list!!)
                        }
                    )
                    .add(subscriptions)
            }
        }
    }
}