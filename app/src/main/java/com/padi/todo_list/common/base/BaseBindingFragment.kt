package com.padi.todo_list.common.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.ui.common.LoadingDialog
import timber.log.Timber

abstract class BaseBindingFragment<ViewModel : BaseViewModel, ViewBinding : ViewDataBinding>(
    private val layoutRes: Int
) : Fragment() {
    private var _binding: ViewBinding? = null
    val viewBinding: ViewBinding get() = _binding!!
    private var loadingDialog: LoadingDialog? = null

    abstract val viewModel: ViewModel
    abstract fun initView(view: View, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        viewBinding.lifecycleOwner = this
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            root.isClickable = true
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        viewBinding.root.setOnDebounceClickListener {
            hideKeyboard()
        }
        with(viewModel) {
            showLoadingEvent.observe(viewLifecycleOwner) { show ->
                show?.let {
                    showDialogLoading(it)
                }
            }
            errorMessage.observe(viewLifecycleOwner) {
                it?.let {
                    // show error msg
                }
            }
            errorId.observe(viewLifecycleOwner) {
                it?.let {
                    // show error msg
                }
            }
        }
        initView(view, savedInstanceState)
    }

    protected fun hideKeyboard() {
        Timber.d("hideKeyboard")
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            requireActivity().currentFocus?.let {
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }

    private fun showDialogLoading(show: Boolean) {
        if (show) showLoading()
        else hideLoading()
    }

    private fun showLoading() {
        if (loadingDialog == null) loadingDialog = LoadingDialog(requireContext())
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

}