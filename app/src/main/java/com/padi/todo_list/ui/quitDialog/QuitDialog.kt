package com.padi.todo_list.ui.quitDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.databinding.DialogQuitAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuitDialog(
    private val onQuit: () -> Unit,
) : BaseDialogFragment<DialogQuitAppBinding>() {
    override fun getLayout(): Int {
        return R.layout.dialog_quit_app
    }

    override fun initView() {
        setupAction()
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupAction() {
      binding.apply {
          tvCancel.setOnClickListener(){
              dismiss()
          }
          tvQuit.setOnClickListener(){
              onQuit.invoke()
              dismiss()
          }
      }
    }
}