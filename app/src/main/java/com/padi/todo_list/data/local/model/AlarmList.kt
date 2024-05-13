package com.padi.todo_list.data.local.model


data class AlarmList(var time: Long) {
    val alarmModels = ArrayList<AlarmModel>()
}
