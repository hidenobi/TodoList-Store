package com.padi.todo_list.data.local.repository

import com.padi.todo_list.ui.task.model.EventTaskUI
import io.reactivex.rxjava3.core.Maybe

interface WidgetSettingRepository {
    fun getPrevData(
        scopeTime: Int, scopeCategory: Long, showCompleted: Boolean
    ): Maybe<List<EventTaskUI>>
}