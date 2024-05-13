package com.padi.todo_list.ui.task.model

import android.net.Uri

data class FileImageUI(
    val id: Long = 0,
    var idTask: Long = 0,
    var image: Uri? = null
)