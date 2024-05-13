package com.padi.todo_list.ui.permisiondialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.DialogWindowPerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverlayPermissionDialog(
    private val onEstablish: () -> Unit,
) : BaseDialogFragment<DialogWindowPerBinding>() {
    override fun getLayout(): Int {
        return R.layout.dialog_window_per
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
          tvLater.setOnDebounceClickListener(){
              dismiss()
          }
          btnEstablish.setOnDebounceClickListener(){
              onEstablish.invoke()
              dismiss()
          }
          root.setOnDebounceClickListener {
              dismiss()
          }
      }
    }
}