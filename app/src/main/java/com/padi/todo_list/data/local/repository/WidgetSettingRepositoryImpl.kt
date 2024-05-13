package com.padi.todo_list.data.local.repository

import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.ui.task.model.EventTaskUI
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class WidgetSettingRepositoryImpl @Inject constructor(
    private val eventTaskDao: EventTaskDao,
) : WidgetSettingRepository {
    override fun getPrevData(
        scopeTime: Int, scopeCategory: Long, showCompleted: Boolean
    ): Maybe<List<EventTaskUI>> {
        return eventTaskDao.getAllEventTasks().subscribeOn(SchedulerProvider.io()).map { output ->
            UtilsJ.getPrevData(scopeTime, scopeCategory, showCompleted, output)
        }
    }

    fun markDoneTask(id: Long): Maybe<List<EventTaskUI>> {
        return eventTaskDao.getEvenByID(id)
            .subscribeOn(SchedulerProvider.io())
            .flatMapCompletable {
                eventTaskDao.update(it.copy(isCompleted = !it.isCompleted))
            }.andThen(
                getPrevData(
                    AppPrefs.scopeTime,
                    AppPrefs.scopeCategory,
                    AppPrefs.showCompletedTask
                )
            )
    }

}