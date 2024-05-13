package com.padi.todo_list.ui.tutorial

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.invisible
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.visible
import com.padi.todo_list.databinding.DialogTutorialBinding

class TutorialDialog(context: Context, var name: String = "") : Dialog(context) {
    private lateinit var slideIn: Animation

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
        val binding = DialogTutorialBinding.inflate(layoutInflater)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        registerEvent(binding)
    }

    var position: Int = 0
    private fun registerEvent(binding: DialogTutorialBinding) {
        binding.apply {
            slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in)
            slideIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}
            })
            // Start animation
            llTutorial1.startAnimation(slideIn)
        btnNext.setOnDebounceClickListener(700) {
            when (position) {
                0 -> {
                    imgTutorial1.invisible()
                    txtTutorial1.invisible()
                    imgTouch1.invisible()
                    llTutorial1.invisible()
                    imgTutorial2.visible()
                    txtTutorial2.visible()
                    imgTouch2.visible()
                    itemTask.visible()
                    txtNameTask.text = name
                    position = 1
                }

                1 -> {
                    btnNext.text = context.getString(R.string.ok)
                    imgTutorial2.invisible()
                    txtTutorial2.invisible()
                    imgTouch2.invisible()
                    imgTutorial3.visible()
                    txtTutorial3.visible()
                    imgTouch3.visible()
                    position = 2
                }

                2 -> {
                    dismiss()
                }
            }
        }
    }
}

}