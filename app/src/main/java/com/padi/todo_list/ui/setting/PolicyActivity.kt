package com.padi.todo_list.ui.setting

import android.os.Bundle
import androidx.activity.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity

import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.utils.LINK_POLICY
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityPolicyBinding
import com.padi.todo_list.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyActivity(): BaseBindingActivity<BaseViewModel, ActivityPolicyBinding>(R.layout.activity_policy) {
    override val viewModel: BaseViewModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        viewBinding.tbPolicy.handleAction(leftAction = {
            finish()
        })
        viewBinding.apply {
            webView.settings.javaScriptEnabled = true
               val linkPolicy = LINK_POLICY
            webView.loadUrl(linkPolicy)
        }
    }
}