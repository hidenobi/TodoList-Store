package com.padi.todo_list.ui.widget.dialog

import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.setSizePercent
import com.padi.todo_list.databinding.DialogWidgetGuideBinding


class WidgetGuideDialog : BaseDialogFragment<DialogWidgetGuideBinding>() {
    override fun getLayout(): Int = R.layout.dialog_widget_guide

    override fun initView() {
        isCancelable = true
        setSizePercent(90)
        setAction()
    }

    private fun setAction() {
        binding.tvGotIt.setOnDebounceClickListener {
            dismiss()
        }
    }

}