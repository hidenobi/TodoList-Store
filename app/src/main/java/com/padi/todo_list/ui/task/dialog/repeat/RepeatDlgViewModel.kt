package com.padi.todo_list.ui.task.dialog.repeat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.ui.task.model.RepeatOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RepeatDlgViewModel @Inject constructor() : BaseViewModel() {
    private val _isRepeat = MutableLiveData(false)
    val isRepeat: LiveData<Boolean> get() = _isRepeat
    fun updateRepeat(bool: Boolean) {
        _isRepeat.value = bool
    }
    private val _repeatOption = MutableLiveData<RepeatOptions>()
    val repeatOptionLiveData: LiveData<RepeatOptions> get() = _repeatOption
    fun updateRepeatOption(newData: RepeatOptions) {
        _repeatOption.value = newData
    }
    private val _listRepeatOption = MutableLiveData(RepeatOptions.getData())
    val listRepeatOption: LiveData<ArrayList<RepeatOptions>> get() = _listRepeatOption
    fun updateSelectRepeatOption(data: RepeatOptions) {
        val output = _listRepeatOption.value
        output?.map {
            it.isSelected = it.id == data.id
        }
        _repeatOption.value = data
        _listRepeatOption.value = output
    }
    fun clearAllRepeatOption() {
        val output = _listRepeatOption.value
        output?.map {
            it.isSelected = false
        }
        _listRepeatOption.value = output
    }
}