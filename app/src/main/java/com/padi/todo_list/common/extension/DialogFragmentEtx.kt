package com.padi.todo_list.common.extension

import android.content.res.Resources
import android.graphics.Rect
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import timber.log.Timber

fun DialogFragment.setSizePercent(
    percentW: Int? = 0,
    percentH: Int? = 0
) {
    val pW = percentW!!.toFloat() / 100
    val pH = percentH!!.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * pW
    val percentHeight = rect.height() * pH
    dialog?.window?.setLayout(
        if (percentWidth.toInt() != 0) {
            percentWidth.toInt()
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        },
        if (percentHeight.toInt() != 0) {
            percentHeight.toInt()
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }
    )
}

fun DialogFragment.setPositionOfView(x: Int?, y: Int?, gravity: Int? = null) {
    val params = dialog?.window?.attributes
    x?.let { params?.x = it }
    y?.let { params?.y = it }
    dialog?.window?.attributes = params
    dialog?.window?.setGravity(
        gravity ?: (Gravity.TOP or Gravity.CENTER_HORIZONTAL)
    )
    Timber.d("${params?.x} ${params?.y}")
}