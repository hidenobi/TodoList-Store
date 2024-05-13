package com.padi.todo_list.ui.task.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setButtonTintListCompat
import com.padi.todo_list.common.utils.BundleKey.OPTION_AZ
import com.padi.todo_list.common.utils.BundleKey.OPTION_DUE_DATE
import com.padi.todo_list.common.utils.BundleKey.OPTION_ZA
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.DialogArrageTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArrangeTaskDialog(
    private val onSave: () -> Unit,
) :
    BaseDialogFragment<DialogArrageTaskBinding>() {

    override fun getLayout(): Int {
        return R.layout.dialog_arrage_task
    }

    override fun initView() {
        setupAction()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupAction() {
        binding.apply {
            rbAlphaAZ.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rbAlphaZA.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)
            rbDueDate.setButtonTintListCompat(R.color.primary_purpul,R.color.false_color)

            groupRadio.check(R.id.rb_alphaAZ)
            btnDone.setOnClickListener() {
                when {
                    rbAlphaAZ.isChecked -> {
                        AppPrefs.saveArrangeTaskOption(OPTION_AZ)
                    }

                    rbAlphaZA.isChecked -> {
                        AppPrefs.saveArrangeTaskOption(OPTION_ZA)

                    }

                    rbDueDate.isChecked -> {
                        AppPrefs.saveArrangeTaskOption(OPTION_DUE_DATE)
                    }

                    else -> dismiss()
                }
                onSave.invoke()
                dismiss()
            }
            when (AppPrefs.getArrangeTaskOption()) {
                OPTION_AZ -> groupRadio.check(R.id.rb_alphaAZ)
                OPTION_ZA -> groupRadio.check(R.id.rb_alphaZA)
                OPTION_DUE_DATE -> groupRadio.check(R.id.rb_due_date)
                else -> {
                    groupRadio.clearCheck()
                }
            }
            btnCancel.setOnClickListener(){
                dismiss()
            }
        }
    }

}