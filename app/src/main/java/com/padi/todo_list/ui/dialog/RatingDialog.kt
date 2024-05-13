package com.padi.todo_list.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.padi.todo_list.R
import com.padi.todo_list.databinding.DialogRatingBinding

class RatingDialog(context: Context) : Dialog(context) {
    private var onPress: OnPress? = null

    @Override
    override fun onStart() {
        val attributes = window!!.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        window!!.setSoftInputMode(16)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes = attributes
        setCancelable(false)
        super.onStart()
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogRatingBinding.inflate(layoutInflater)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        registerEvent(binding)
        changTextColor(binding)
        handleBackDevice()
    }

    private fun registerEvent(binding: DialogRatingBinding) {

        binding.btnSubmitRate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (binding.ratingBar.rating == 0f) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.txt_please_feedback),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else {
                    onPress!!.rating()
                }
            }
        })
        binding.btnCancel.setOnClickListener{dismiss()}

    }

    private fun changTextColor(binding: DialogRatingBinding) {
        val text = context.getString(R.string.title_rate_app)
        val builder = SpannableStringBuilder(text)
        val startIndex = text.indexOf(context.getString(R.string.star_color))
        val endIndex = startIndex + context.getString(R.string.star_color).length
        val colorStateList =
            ContextCompat.getColorStateList(binding.root.context, R.color.primary_purpul)
        val defaultColor =
            colorStateList?.getColorForState(intArrayOf(), colorStateList.defaultColor)
                ?: Color.BLACK
        builder.setSpan(ForegroundColorSpan(defaultColor), startIndex, endIndex, 0)
        binding.titleRate.text = builder

    }

    fun init(onPress: OnPress?) {
        this.onPress = onPress
    }

    interface OnPress {
        fun rating()
    }
    private fun handleBackDevice() {
        this.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                return@setOnKeyListener true
            } else
                return@setOnKeyListener false
        }
    }
}