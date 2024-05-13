package com.padi.todo_list.ui.task.model

data class TitleDateUI(
    val isTop: Boolean = false,
    val date: String = "",
) : UiModel() {
    companion object {
        fun newInstance(date: String, isTop: Boolean): TitleDateUI =
            TitleDateUI(
                date = date,
                isTop = isTop,
            )
    }
}