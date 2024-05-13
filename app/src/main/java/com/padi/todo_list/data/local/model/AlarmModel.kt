package com.padi.todo_list.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmModel(
    var id: Long,
    var exactTime: Long,
    var remindTime: Long,
    var name: String
) : Parcelable