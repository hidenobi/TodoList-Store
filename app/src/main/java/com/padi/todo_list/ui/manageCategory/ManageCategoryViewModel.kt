package com.padi.todo_list.ui.manageCategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.common.extension.transformToListSubtask
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.repository.ManageCategoryRepository
import com.padi.todo_list.data.local.repository.NewTaskRepository
import com.padi.todo_list.ui.task.model.SubTaskUI
import com.padi.todo_list.ui.task.model.TabCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    private val newTaskRepo: NewTaskRepository,
    private val manageCatRepo: ManageCategoryRepository
) : BaseViewModel() {

    private val _categories = MutableLiveData<MutableList<TabCategory>>()
    val categories: LiveData<MutableList<TabCategory>> = _categories

    fun insertCategoryManager(category: CategoryEntity) {
        newTaskRepo.insertCategory(category, _categories.value!!.size)
            .subscribeOn(SchedulerProvider.io())
            .flatMap {
                manageCatRepo.getTabTaskCounts()
                    .flatMap { list ->
                        val updatedList = list.mapIndexed { index, item ->
                            item.position = index
                            item
                        }
                        manageCatRepo.updateItemPosition(convertList(updatedList))
                            .andThen(Single.just(updatedList))

                    }
            }
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _categories.value = it.toMutableList()
                    Timber.d("insert Category success")
                },
                onError = {
                    Timber.d("insert Category error")
                }
            ).add(subscriptions)
    }

    fun fetchData() {
        manageCatRepo.getTabTaskCounts()
            .flatMap { list ->
                val updatedList = list.mapIndexed { index, item ->
                    item.position = index
                    item
                }
                manageCatRepo.updateItemPosition(convertList(updatedList))
                    .andThen(Single.just(updatedList))

            }
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _categories.value = it.toMutableList()
                },
                onError = {
                    Timber.e("getTab error: ${it.message}")
                }
            ).add(subscriptions)
    }
    fun deleteEventTaskById(id: Long) {
        manageCatRepo.deleteById(id)
            .andThen(
                newTaskRepo.getEventTasksByCategoryId(id)
                    .flatMapCompletable { tasks ->
                        Completable.concat(
                            tasks.map { task ->
                                Completable.mergeArray(
                                    newTaskRepo.deleteSubTaskById(task.id),
                                    newTaskRepo.deleteAllSubTaskById(task.id),
                                    newTaskRepo.deleteImage(task.id),
                                    newTaskRepo.deleteNote(task.id),
                                    newTaskRepo.deleteRemindByIdTask(task.id)
                                )
                            }
                        )
                    }
            )
            .andThen(newTaskRepo.deleteTaskByCateId(id))
            .andThen(manageCatRepo.getTabTaskCounts())
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _categories.value = it.toMutableList()
                },
                onError = {}
            )
            .addTo(subscriptions)
    }
    fun updateName(category: CategoryEntity,id: Long) {
        manageCatRepo.updateName(category.name, id)
            .andThen(manageCatRepo.getTabTaskCounts())
            .subscribeOn(SchedulerProvider.io())
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    _categories.value = it.toMutableList()
                }
            )
            .addTo(subscriptions)
    }
    fun move(from: Int, target: Int, data: List<TabCategory>) {
        if (from >= data.size || target >= data.size) return
        if (from < target) {
            for (i in from until target) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in from downTo target + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        updateDatabase(data)
    }

    private fun updateDatabase(list: List<TabCategory>) {
        manageCatRepo.updateItemPosition(convertList(list))
            .subscribeOn(SchedulerProvider.io())
            .subscribeBy()
            .add(subscriptions)
    }
    private fun convertList(list: List<TabCategory>): List<CategoryEntity> {
        return list.mapIndexed { index, tabCategory ->
            CategoryEntity(
                id = tabCategory.id,
                name = tabCategory.name,
                position = index

            )
        }
    }
}