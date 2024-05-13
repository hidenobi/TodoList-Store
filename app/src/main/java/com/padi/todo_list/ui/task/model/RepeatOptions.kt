package com.padi.todo_list.ui.task.model

import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.extension.changeContextByLanguageCode
import com.padi.todo_list.common.utils.RepeatConstants
import java.io.Serializable

data class RepeatOptions(
    val id: Int? = NOT_INIT_ID,
    var name: String = "",
    var repeat: Int = RepeatConstants.NO_REPEAT,
    var isSelected: Boolean = false,
) : Serializable {
    companion object {
        const val NOT_INIT_ID = -1
        fun getData(): ArrayList<RepeatOptions> {
            val output = ArrayList<RepeatOptions>()
            for (i in 0..4) {
                output.add(
                    RepeatOptions(
                        i + 1,
                        getRepeatName(i),
                        getRepeat(i)
                    )
                )
            }
            return output
        }

        private fun getRepeat(position: Int): Int {
            return when (position) {
                0 -> {
                    RepeatConstants.HOURLY
                }

                1 -> {
                    RepeatConstants.DAILY
                }

                2 -> {
                    RepeatConstants.WEEKLY
                }

                3 -> {
                    RepeatConstants.MONTHLY
                }

                else -> {
                    RepeatConstants.YEARLY
                }
            }
        }

        private fun getRepeatName(position: Int): String {
            return when (position) {
                0 -> {
                    Application.getInstance().applicationContext.changeContextByLanguageCode().getString(R.string.hour)
                }

                1 -> {
                    Application.getInstance().applicationContext.changeContextByLanguageCode().getString(R.string.daily)
                }

                2 -> {
                    Application.getInstance().applicationContext.changeContextByLanguageCode().getString(R.string.weekly)
                }

                3 -> {
                    Application.getInstance().applicationContext.changeContextByLanguageCode().getString(R.string.monthly)
                }

                else -> {
                    Application.getInstance().applicationContext.changeContextByLanguageCode().getString(R.string.yearly)
                }
            }
        }

        fun getInstance(repeat: Int): RepeatOptions {
            return when (repeat) {
                RepeatConstants.HOURLY -> {
                    RepeatOptions(
                        id = 1, name = getRepeatName(0), repeat = repeat, isSelected = true
                    )
                }

                RepeatConstants.DAILY -> {
                    RepeatOptions(
                        id = 2, name = getRepeatName(1), repeat = repeat, isSelected = true
                    )
                }

                RepeatConstants.WEEKLY -> {
                    RepeatOptions(
                        id = 3, name = getRepeatName(2), repeat = repeat, isSelected = true
                    )
                }

                RepeatConstants.MONTHLY -> {
                    RepeatOptions(
                        id = 4, name = getRepeatName(3), repeat = repeat, isSelected = true
                    )
                }

                RepeatConstants.YEARLY -> {
                    RepeatOptions(
                        id = 5, name = getRepeatName(4), repeat = repeat, isSelected = true
                    )
                }

                else -> {
                    RepeatOptions()
                }
            }
        }
    }

}
