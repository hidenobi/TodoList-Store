package com.padi.todo_list.ui.task.popup

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.padi.todo_list.common.extension.toPx
import com.padi.todo_list.data.local.model.CategoryEntity

class CustomListPopupWindowBuilder<T>(private val context: Context) {

    private var list: List<T> = emptyList()
    private var anchor: View? = null
    private var backgroundDrawableRes: Int = 0
    private var horizontalOffsetValue: Int = 0
    private var verticalOffsetValue: Int = 0
    private var adapter: ArrayAdapter<T>? = null
    private var _width = 190
    private var _height = 287

    fun setList(list: List<T>) = apply { this.list = list }
    fun setWidth(width: Int) = apply { this._width = width }
    fun setHeight(height: Int) = apply { this._height = height }
    fun setAnchor(anchor: View) = apply { this.anchor = anchor }
    fun setBackgroundDrawableRes(backgroundDrawableRes: Int) =
        apply { this.backgroundDrawableRes = backgroundDrawableRes }

    fun setHorizontalOffset(horizontalOffsetValue: Int) =
        apply { this.horizontalOffsetValue = horizontalOffsetValue }

    fun setVerticalOffset(verticalOffsetValue: Int) =
        apply { this.verticalOffsetValue = verticalOffsetValue }

    fun setAdapter(adapter: ArrayAdapter<T>) = apply { this.adapter = adapter }

    fun build(): ListPopupWindow {
        val listPopupWindow = ListPopupWindow(context)
        adapter?.let { listPopupWindow.setAdapter(it) }
        listPopupWindow.width = _width.toPx
        listPopupWindow.height = _height.toPx
        listPopupWindow.isModal = true
        listPopupWindow.anchorView = anchor
        listPopupWindow.horizontalOffset = horizontalOffsetValue
        listPopupWindow.verticalOffset = verticalOffsetValue
        if (backgroundDrawableRes != 0) {
            listPopupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context, backgroundDrawableRes
                )
            )
        }

        return listPopupWindow
    }

}