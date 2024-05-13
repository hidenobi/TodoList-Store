package com.padi.todo_list.ui.task.dialog.new_category

import android.text.Editable
import android.text.TextWatcher
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseDialogFragment
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.extension.setSizePercent
import com.padi.todo_list.common.extension.showToast
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.databinding.DialogNewCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCategoryDialog(private val onClickSave: (category: CategoryEntity) -> Unit) :
    BaseDialogFragment<DialogNewCategoryBinding>() {

    override fun getLayout(): Int {
        return R.layout.dialog_new_category
    }

    override fun initView() {
        binding.count = 0
        isCancelable = true
        setSizePercent(90)
        setAction()
    }

    private fun setAction() {
        binding.edtCategoryName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                binding.count = text.toString().length
            }

            override fun afterTextChanged(text: Editable?) {
                binding.edtCategoryName.setSelection(binding.edtCategoryName.text!!.length)
            }
        })
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnDebounceClickListener {
            if (binding.edtCategoryName.text.toString().trim().isEmpty()) {
                showToast(requireContext(), getString(R.string.cannot_be_empty))
            } else {
                onClickSave.invoke(
                    CategoryEntity(
                        name = binding.edtCategoryName.text.toString().trim()
                    )
                )
                dismiss()
            }
        }
    }
}