package com.padi.todo_list.ui.task.model

import androidx.annotation.DrawableRes
import com.padi.todo_list.R

data class FlagUI(
    var name: String? = null,
    @DrawableRes val icon: Int = R.drawable.ic_menu_item,
)
