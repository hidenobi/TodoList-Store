package com.padi.todo_list.ui.widget.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.padi.todo_list.databinding.BottomSheetAddWidgetBinding

class AddWidgetBottomSheet(private val onClickAdd: () -> Unit, private val onClickCancel: () -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddWidgetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).apply {
                    isHideable = false
                }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddWidgetBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
    }

    private fun setAction() {
        binding.tvCancel.setOnClickListener { onClickCancel.invoke() }
        binding.tvAdd.setOnClickListener { onClickAdd.invoke() }

       handleBackDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    private fun handleBackDevice() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onClickCancel.invoke()
                return@setOnKeyListener true
            } else
                return@setOnKeyListener false
        }
    }

}