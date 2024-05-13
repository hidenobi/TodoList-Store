package com.padi.todo_list.ui.language

import androidx.annotation.DrawableRes

data class LanguageModel(
    var id: Int = -1,
    @DrawableRes var  src: Int,
    var country: String,
    var codeLang: String,
    var isSelected : Boolean = false
)