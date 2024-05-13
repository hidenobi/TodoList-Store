package com.padi.todo_list.ui.permisiondialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.databinding.DialogNotiPerBinding
import com.padi.todo_list.databinding.DialogQuitAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotifyPermissionDialog(
    private val onSetNow: () -> Unit,
) : BaseDialogFragment<DialogNotiPerBinding>() {
    override fun getLayout(): Int {
        return R.layout.dialog_noti_per
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
          btnSetNow.setOnDebounceClickListener(){
              onSetNow.invoke()
              dismiss()
          }
          root.setOnDebounceClickListener {
              dismiss()
          }
      }
    }
}