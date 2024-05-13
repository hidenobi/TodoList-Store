package com.padi.todo_list.ui.language

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.extension.gone
import com.padi.todo_list.common.extension.setOnDebounceClickListener
import com.padi.todo_list.common.utils.IntentConstants
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.setStatusBar
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityLanguageBinding
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.main.MainActivity
import com.padi.todo_list.ui.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseBindingActivity<BaseViewModel, ActivityLanguageBinding>(R.layout.activity_language) {
    override val viewModel: BaseViewModel by viewModels()
    private val languageAdapter: LanguageAdapter by lazy { LanguageAdapter {} }
    private lateinit var listLanguage: MutableList<LanguageModel>
    private lateinit var selectCode: String
    private var isScreenSplash=false

    companion object{
        const val ACTION_CHANGE_LANGUAGE_APP = "android.appwidget.action.ACTION_CHANGE_LANGUAGE_APP"
    }
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        setStatusBar(window, R.color.main_screen, true)
        getIntentValue()
        updateUi()
        selectCode = AppPrefs.languageCode
        listLanguage = UtilsJ.getListLanguage(this)
        setRecyclerView()
        getAction()
    }
    private fun getIntentValue() {
        isScreenSplash=intent.getBooleanExtra(IntentConstants.splashData,false)
    }
    private fun getAction(){
        viewBinding.icDone.setOnDebounceClickListener {
            selectLanguage()
        }
        viewBinding.btnBack.setOnDebounceClickListener {
            finish()
        }
    }
    private fun updateUi() {
        if (isScreenSplash){
            viewBinding.btnBack.gone()
        }
    }
    private fun selectLanguage(){
        AppPrefs.languageCode = selectCode
        AppPrefs.saveIsChangeLanguage(true)

        if (isScreenSplash) {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.action = ACTION_CHANGE_LANGUAGE_APP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }
    private fun setRecyclerView() {
        languageAdapter.setData(listLanguage)
        viewBinding.rvLanguage.run {
            layoutManager = LinearLayoutManager(this@LanguageActivity,RecyclerView.VERTICAL,false)
            adapter = languageAdapter
        }
        languageAdapter.onClickItem = { nationUI ->
            val index = listLanguage.indexOf(nationUI)
            for (i in 0 until listLanguage.size) {
                if (listLanguage[i].isSelected) {
                    listLanguage[i].isSelected = false
                    languageAdapter.notifyItemChanged(i)
                    break
                }
            }
            listLanguage[index].isSelected = true
            languageAdapter.notifyItemChanged(index)
            listLanguage[index].codeLang.let { selectCode = it }
        }
    }
}