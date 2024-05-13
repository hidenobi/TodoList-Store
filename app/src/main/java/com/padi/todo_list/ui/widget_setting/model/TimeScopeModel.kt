package com.padi.todo_list.ui.widget_setting.model

import com.padi.todo_list.R
import com.padi.todo_list.common.Application
import com.padi.todo_list.common.extension.setUpLanguage

class TimeScopeModel(
    val scope: Int,
    val name: String
) {
    companion object {
        fun getData(): ArrayList<TimeScopeModel> {
            val output = ArrayList<TimeScopeModel>()
            //1: ScopeTask.ALL
            //2: ScopeTask.TODAY
            val stringArray =
                Application.getInstance().applicationContext.setUpLanguage().resources.getStringArray(R.array.time_scope_name)
            for (i in 1..2) {
                output.add(
                    TimeScopeModel(
                        i,
                        stringArray[i - 1]
                    )
                )
            }
            return output
        }
    }
}
