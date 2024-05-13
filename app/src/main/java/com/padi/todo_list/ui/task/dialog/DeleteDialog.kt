package com.padi.todo_list.ui.task.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.databinding.DialogDeleteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DeleteDialog(
    private val title: String = "",
    private val content: String = "",
    private val onOK: () -> Unit,
) : BaseDialogFragment<DialogDeleteBinding>() {
    override fun getLayout(): Int {
        return R.layout.dialog_delete
    }

    override fun initView() {
        if (title.isNotEmpty()) binding.tvTitle.text = title
        binding.tvDelete.text = content
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
          btnCancel.setOnClickListener(){
              dismiss()
          }
          btnDone.setOnClickListener(){
              onOK.invoke()
              dismiss()
          }
      }
    }
}