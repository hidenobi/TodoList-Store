package com.padi.todo_list.ui.task.dialog.remind_time

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemindTimeDlgViewModel @Inject constructor() : BaseViewModel() {
    private val _listOffsetTime = MutableLiveData<ArrayList<OffsetTimeUI>>()
    val listOffsetTime: LiveData<ArrayList<OffsetTimeUI>> get() = _listOffsetTime

    fun getAllOffsetTime(data: ArrayList<OffsetTimeUI>) {
        _listOffsetTime.value = data
    }

    fun updateCustomOffsetTime(newData: OffsetTimeUI) {
        val oldList = _listOffsetTime.value
        oldList?.map { offsetTimeUI ->
            if (offsetTimeUI.position == OffsetTimeUI.POS_OFFSET_TIME_CUSTOM) {
                offsetTimeUI.isChecked = newData.isChecked
                offsetTimeUI.offsetTime = newData.offsetTime
                offsetTimeUI.text = newData.text
            }
        }
        _listOffsetTime.value = oldList!!
    }

    fun updateOffsetTime(newData: OffsetTimeUI) {
        val oldList = _listOffsetTime.value
        oldList?.map { offsetTimeUI ->
            if (offsetTimeUI.position == newData.position) {
                offsetTimeUI.isChecked = !newData.isChecked
            }
        }
        _listOffsetTime.value = oldList!!
    }

}
