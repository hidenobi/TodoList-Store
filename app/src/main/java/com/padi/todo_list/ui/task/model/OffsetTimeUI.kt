package com.padi.todo_list.ui.task.model

import android.os.Parcelable
import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.extension.setUpLanguage
import com.padi.todo_list.common.utils.OffSetTimeValue.CUSTOM
import com.padi.todo_list.common.utils.OffSetTimeValue.FIFTEEN_MINUTES
import com.padi.todo_list.common.utils.OffSetTimeValue.FIVE_MINUTES
import com.padi.todo_list.common.utils.OffSetTimeValue.ONE_DAY
import com.padi.todo_list.common.utils.OffSetTimeValue.TEN_MINUTES
import com.padi.todo_list.common.utils.OffSetTimeValue.THIRTY_MINUTES
import com.padi.todo_list.common.utils.OffSetTimeValue.ZERO
import kotlinx.parcelize.Parcelize

@Parcelize
data class OffsetTimeUI(
    var position: Int,
    var offsetTime: Long,
    var text: String,
    var isChecked: Boolean = false,
) : Parcelable {

    companion object {
        const val POS_OFFSET_TIME_CUSTOM = 6
        fun getData(isNotSelect: Boolean? = false): ArrayList<OffsetTimeUI> {
            val list = ArrayList<OffsetTimeUI>()
            for (i in 0..POS_OFFSET_TIME_CUSTOM) {
                list.add(
                    OffsetTimeUI(
                        position = i,
                        offsetTime = getTimeOffset(i),
                        text = getOffsetTimeText(i),
                        isChecked = if (isNotSelect == true) {
                            false
                        } else {
                            i == 1
                        }
                    )
                )
            }
            return list
        }

        private fun getOffsetTimeText(i: Int): String {

            return when (i) {
                0 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.same_with_due_date)
                }

                1 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.five_minnutes_before)
                }

                2 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.ten_minnutes_before)
                }

                3 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.fifteen_minnutes_before)
                }

                4 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.thirty_minnutes_before)
                }

                5 -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.one_day_before)
                }


                POS_OFFSET_TIME_CUSTOM -> {
                    Application.getInstance().applicationContext.setUpLanguage().getString(R.string.set_reminder_time)
                }

                else -> {
                    ""
                }
            }
        }

        private fun getTimeOffset(i: Int): Long {

            return when (i) {
                0 -> {
                    ZERO
                }

                1 -> {
                    FIVE_MINUTES // 5 minutes
                }

                2 -> {
                    TEN_MINUTES // 10 minutes
                }

                3 -> {
                    FIFTEEN_MINUTES // 15 minutes
                }

                4 -> {
                    THIRTY_MINUTES // 30 minutes
                }

                5 -> {
                    ONE_DAY // 1 day
                }

                else -> {
                    CUSTOM// custom
                }
            }
        }

    }

}

