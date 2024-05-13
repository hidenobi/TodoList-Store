package com.padi.todo_list.data.local.repository

import com.padi.todo_list.data.local.model.AlarmList
import io.reactivex.rxjava3.core.Maybe

interface AlarmRepository {
    fun getNextAlarm(): Maybe<AlarmList>
}