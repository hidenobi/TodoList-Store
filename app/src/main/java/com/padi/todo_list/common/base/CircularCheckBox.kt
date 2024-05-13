package com.padi.todo_list.common.base

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.padi.todo_list.R

class CircularCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var isChecked: Boolean = false
        set(value) {
            field = value
            updateCheckBoxState()
        }

    init {
        // Set default state
        updateCheckBoxState()

        // Handle click event
        setOnClickListener {
            isChecked = !isChecked
            onCheckedChangeListener?.invoke(isChecked)
        }
    }
    private var onCheckedChangeListener: ((Boolean) -> Unit)? = null

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        onCheckedChangeListener = listener
    }

    private fun updateCheckBoxState() {
        if (isChecked) {
            // Set the checked state image
            setImageResource(R.drawable.checked)
        } else {
            // Set the unchecked state image
            setImageResource(R.drawable.unchecked)
        }
    }
}
