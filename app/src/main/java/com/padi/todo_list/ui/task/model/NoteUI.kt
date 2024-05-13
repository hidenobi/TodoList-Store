package com.padi.todo_list.ui.task.model

data class NoteUI(
    val id: Long = 0,
    var idTask: Long = 0,
    var content: String? = null,
    var titLe: String? = null
)
