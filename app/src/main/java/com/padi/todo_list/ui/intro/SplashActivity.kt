package com.padi.todo_list.ui.intro

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.utils.IntentConstants
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.setStatusBar
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.prefs.AppPrefs.isFirstLanguage
import com.padi.todo_list.databinding.ActivitySplashBinding
import com.padi.todo_list.ui.language.LanguageActivity
import com.padi.todo_list.ui.work_manager.EventWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseBindingActivity<BaseViewModel, ActivitySplashBinding>(R.layout.activity_splash) {
    override val viewModel: BaseViewModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        setStatusBar(window, R.color.main_screen, true)
        autoFillProgressbar()
        scheduleNotify()
        AppPrefs.isFirstMainScrApp = false
    }

    private fun scheduleNotify() {
        if (AppPrefs.getEnableTodoReminder()) {
            EventWorker.scheduleTodoRemind(this@SplashActivity, false)
        }

        if (AppPrefs.getEnableAddTaskFromNotification()) {
            EventWorker.schedulePinReminder(this@SplashActivity)
        }
    }

    private fun autoFillProgressbar() {
        val progressAnimator =
            ObjectAnimator.ofInt(viewBinding.progressHorizontal, "progress", 0, 100)
        progressAnimator.duration = 3000L
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()
        progressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                runOnUiThread {
                    navigateIntro()
                }
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        })
    }
    private fun navigateIntro(){
        if (isFirstLanguage){
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra(IntentConstants.splashData, true)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, IntroActivity::class.java)
            intent.putExtra(IntentConstants.splashData, true)
            startActivity(intent)
            finish()
        }

    }
}