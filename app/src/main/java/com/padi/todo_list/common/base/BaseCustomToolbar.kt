package com.padi.todo_list.common.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.databinding.LayoutCommonToolbarBinding

class BaseCustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {
    private var bindingView: LayoutCommonToolbarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_common_toolbar,
        this,
        true
    )

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
            if (a.hasValue(R.styleable.CustomToolbar_textTitle)) {
                var textTitle = a.getString(R.styleable.CustomToolbar_textTitle)
                bindingView.tvTitle.text = textTitle

            }
            if (a.hasValue(R.styleable.CustomToolbar_leftIcon)) {
                var icon = a.getDrawable(R.styleable.CustomToolbar_leftIcon)
                if (icon != null) {
                    bindingView.toolbarLeftIcon.setImageDrawable(icon)
                }
            }
            if (a.hasValue(R.styleable.CustomToolbar_rightIcon)) {
                var icon = a.getDrawable(R.styleable.CustomToolbar_rightIcon)
                if (icon != null) {
                    bindingView.toolbarRightIcon.apply {
                        setImageDrawable(icon)
                        visible()
                    }
                }
            }
        }
    }

    fun handleAction(leftAction: (() -> Unit)? = null) {
        bindingView.toolbarLeftIcon.setOnClickListener {
            leftAction?.invoke()
        }
    }
    fun handleRightAction(rightAction: (() -> Unit)? = null) {
        bindingView.toolbarRightIcon.setOnClickListener {
            rightAction?.invoke()
        }
    }

}
