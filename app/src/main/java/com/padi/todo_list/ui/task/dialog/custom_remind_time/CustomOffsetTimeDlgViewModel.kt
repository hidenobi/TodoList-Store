package com.padi.todo_list.ui.task.dialog.custom_remind_time

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.ui.task.enum_class.RemindUnitEnum
import com.padi.todo_list.ui.task.model.ClockModel
import com.padi.todo_list.ui.task.model.OffsetTimeUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomOffsetTimeDlgViewModel @Inject constructor() : BaseViewModel() {
    private val _clockModel = MutableLiveData<ClockModel>()
    val clockModelLiveData: LiveData<ClockModel> get() = _clockModel

    fun initClockModel(model: ClockModel) {
        _clockModel.value = model
    }

    private val _offsetTimeModel = MutableLiveData<OffsetTimeUI>()
    val offSetTimeModelLivedata: LiveData<OffsetTimeUI> get() = _offsetTimeModel

    fun initCustomOffsetTime(model: OffsetTimeUI) {
        _offsetTimeModel.value = model
    }

    fun updateRemindModel(newNumber: Int? = null, newUnit: RemindUnitEnum? = null) {

        val num = newNumber ?: _number.value
        val unit = newUnit ?: _unit.value
        val milliRemind = UtilsJ.calculateRemindMillis(_clockModel.value!!, num!!, unit!!)

        val offsetTime = UtilsJ.calculateOffsetTime(num, unit)
        val text = UtilsJ.customRemindText(milliRemind)
        val offsetTimeUI = _offsetTimeModel.value?.copy(offsetTime = offsetTime, text = text)
        _offsetTimeModel.value = offsetTimeUI!!
    }

    private val _unit = MutableLiveData(RemindUnitEnum.HOUR)
    val unitLiveData: LiveData<RemindUnitEnum> get() = _unit

    fun updateUnit(newUnit: Int) {
        _unit.value = setUnit(newUnit)
    }

    private val _number = MutableLiveData(1)
    val numberLiveData: LiveData<Int> get() = _number

    fun updateNumber(newNumber: Int) {
        _number.value = newNumber
    }

    private fun setUnit(newVal: Int): RemindUnitEnum {
        return when (newVal) {
            0 -> {
                RemindUnitEnum.MINUTE
            }

            1 -> {
                RemindUnitEnum.HOUR
            }

            2 -> {
                RemindUnitEnum.DAY
            }

            else -> {
                RemindUnitEnum.WEEK
            }
        }
    }


}



