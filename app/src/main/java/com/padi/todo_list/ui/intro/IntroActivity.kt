package com.padi.todo_list.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.setStatusBar
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityIntroBinding
import com.padi.todo_list.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity() :
    BaseBindingActivity<BaseViewModel, ActivityIntroBinding>(R.layout.activity_intro) {
    override val viewModel: BaseViewModel by viewModels()

    private val tabFirst = 0
    private val tabSecond = 1
    private val tabThree = 2
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        setStatusBar(window, R.color.main_screen, true)
        setUpViewpager()
        registerEvent()
    }

    private fun setUpViewpager() {
        val page1 = IntroModel(
            R.drawable.image_intro_1,
            getString(R.string.title_intro_1),
            getString(R.string.content_intro_1)
        )
        val page2 = IntroModel(
            R.drawable.image_intro_2,
            getString(R.string.title_intro_2),
            getString(R.string.content_intro_2)
        )
        val page3 = IntroModel(
            R.drawable.image_intro_3,
            getString(R.string.title_intro_3),
            getString(R.string.content_intro_3)
        )

        viewBinding.viewPagerIntro.adapter = IntroAdapter(arrayListOf(page1, page2, page3))
        viewBinding.indicator.apply {
            setSliderWidth(
                resources.getDimension(com.intuit.sdp.R.dimen._8sdp),
                resources.getDimension(com.intuit.sdp.R.dimen._18sdp)
            )
            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._8sdp))
            setupWithViewPager(viewBinding.viewPagerIntro)
        }

    }

    private fun registerEvent() {

        viewBinding.tvNext.setOnClickListener {
            nextAction()
        }

        viewBinding.viewPagerIntro.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateValueOnPage(position)
            }
        })
    }
    private fun nextAction(){
        val currentItem = viewBinding.viewPagerIntro.currentItem
        if (currentItem < viewBinding.viewPagerIntro.adapter!!.itemCount - 1) {
            viewBinding.viewPagerIntro.setCurrentItem(currentItem + 1, true)
        } else {
            val isFirstLaunchPermission = AppPrefs.getIsFirstPermission()
            if (isFirstLaunchPermission) {
                val intent = Intent(this, PermissionActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateValueOnPage(position: Int) {
        when (position) {
            tabFirst -> {
                viewBinding.tvNext.text = getString(R.string.next)
            }

            tabSecond -> {
                viewBinding.tvNext.text = getString(R.string.next)

            }

            tabThree -> {
                viewBinding.tvNext.text = getString(R.string.start)

            }
        }
    }

}