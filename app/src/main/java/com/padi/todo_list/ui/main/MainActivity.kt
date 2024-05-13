package com.padi.todo_list.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.sendUpdateTaskFragmentIntent
import com.padi.todo_list.common.utils.ACTION_NOTIFY_PER
import com.padi.todo_list.common.utils.ACTION_OVERLAY_PER
import com.padi.todo_list.common.utils.ADD_ACTION
import com.padi.todo_list.common.utils.BundleKey
import com.padi.todo_list.common.utils.CategoryConstants
import com.padi.todo_list.common.utils.IntentConstants
import com.padi.todo_list.common.utils.SenMail
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.databinding.ActivityMainBinding
import com.padi.todo_list.notfication.NotificationController
import com.padi.todo_list.ui.MainViewModel
import com.padi.todo_list.ui.completeTask.CompleteActivity
import com.padi.todo_list.ui.detail_task.DetailTaskActivity
import com.padi.todo_list.ui.dialog.RatingDialog
import com.padi.todo_list.ui.language.LanguageActivity
import com.padi.todo_list.ui.quitDialog.QuitDialog
import com.padi.todo_list.ui.setting.PolicyActivity
import com.padi.todo_list.ui.setting.SettingActivity
import com.padi.todo_list.ui.setting.StarActivity
import com.padi.todo_list.ui.task.TaskFragment
import com.padi.todo_list.ui.task.TaskFragment.Companion.ACTION_SELECT_CATEGORY
import com.padi.todo_list.ui.task.dialog.new_category.NewCategoryDialog
import com.padi.todo_list.ui.task.dialog.new_task.NewTaskBottomSheet
import com.padi.todo_list.ui.task.model.TabCategory
import com.padi.todo_list.ui.widget.WidgetMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

const val ACTION_COMPLETE_TASK = "ACTION_COMPLETE_TASK"
const val ACTION_COMPLETE_SRC_RETURN = "ACTION_COMPLETE_SRC_RETURN"
const val CLICK_NOTIFICATION = "CLICK_NOTIFICATION"
const val ACTION_UPDATE_UI_TASK_ACTIVITY = "ACTION_UPDATE_UI_TASK_ACTIVITY"

@AndroidEntryPoint
class MainActivity :
    BaseBindingActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {

    override val viewModel: MainViewModel by viewModels()
    private var _showStatus = ShowStatus.IDLE

    private val addTaskReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_COMPLETE_TASK -> toCompleteScr()
                ACTION_NOTIFY_PER -> showNotifyDialog()
                ACTION_OVERLAY_PER -> showOverlayPerDialog()
                ACTION_UPDATE_UI_TASK_ACTIVITY -> {
                    fetchData(viewBinding.drawerLayout)
                }
            }
        }
    }
    private var menuAdapter : LeftMenuAdapter? = null

    private fun toCompleteScr() {
        val intent = Intent(this, CompleteActivity::class.java)
        startActivity(intent)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val drawerLayout: DrawerLayout = viewBinding.drawerLayout
        viewBinding.mainContent.navMain.setupWithNavController(findNavController(R.id.nav_host_fragment_activity_main))
        fetchData(drawerLayout)
        viewBinding.mainContent.btnMenu.setOnClickListener {
            drawerLayout.open()
        }
        regisAddTaskReceiver()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.close()
                } else {
                    showQuitDialog()
                }
            }
        })

        showBottomSheet(intent)
        updateLanguageWidget(intent)
        getIntentClickNotification(intent)
        observeData()
        if (!AppPrefs.isFirstMainScrApp){
            showNotifyDialog()
            AppPrefs.isFirstMainScrApp = true
        }
        AppPrefs.isFirstLanguage = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showBottomSheet(intent)
        updateLanguageWidget(intent)
    }

    private fun getIntentClickNotification(intent: Intent?) {
        if (intent?.action == CLICK_NOTIFICATION) {
            _showStatus = ShowStatus.SHOW_DETAIL_TASK
            val id = intent.getLongExtra(NotificationController.KEY_ID_NOTIFICATION,-1)
            viewModel.getEventTaskInput(id)
        }
    }

    private fun showQuitDialog() {
        QuitDialog(onQuit = {
            finish()
        }).apply {
            show(supportFragmentManager, QuitDialog::javaClass.name)
        }
    }

    private fun observeData() {
        viewModel.isInsertTaskSuccess.observe(this) {
            Timber.d("CHECK-Notify-MainAct start observe: $it")
            if (it) {
                Timber.d("CHECK-Notify-MainAct observe: $it")
                viewModel.setAlarm()
                viewModel.resetLiveDataOfBottomSheet(getString(R.string.no_category))
                viewModel.updateStandardWidget()
                sendUpdateTaskFragmentIntent()
            }
        }
        viewModel.listCategoryMenu.observe(this){
            menuAdapter?.updateCategory(it)
        }

        viewModel.eventTaskLiveData.observe(this){
            if (_showStatus == ShowStatus.SHOW_DETAIL_TASK) {
                if (it.id != 0L) {
                    DetailTaskActivity.start(this@MainActivity, it)
                    _showStatus = ShowStatus.IDLE
                }
            }
        }

    }

    private fun setMenuAction(name: String) {
        when(name){
            getString(R.string.star_task) ->   {
                startActivity(Intent(this, StarActivity::class.java))
            }
            getString(R.string.widget) ->  startActivity(Intent(this, WidgetMenuActivity::class.java))
            getString(R.string.language) -> startActivity(Intent(this, LanguageActivity::class.java))
            getString(R.string.rate_us) -> showDialogRatedAppSetting()
            getString(R.string.share_app) ->  shareApp()
            getString(R.string.feedback) -> feedback()
            getString(R.string.setting) ->  startActivity(Intent(this, SettingActivity::class.java))
            getString(R.string.privacy_policy) -> startActivity(Intent(this, PolicyActivity::class.java))
            else -> {}
        }
    }

    private fun feedback(){
        val uriText =
            "mailto:" + SenMail.SUPPORT_EMAIL + "?subject=" + getString(R.string.app_name) + "&body="
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(uriText)
            setPackage("com.google.android.gm")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Send Email")
        if (chooser.resolveActivity(this@MainActivity.packageManager) != null) {
            startActivity(intent)
        }
    }
    private fun fetchData(drawerLayout: DrawerLayout) {
        initRecycleView(drawerLayout)
        viewModel.getALlCategory(this.getString(R.string.all))
    }
    private fun initRecycleView(drawerLayout: DrawerLayout) {
        val icon = resources.obtainTypedArray(R.array.menu_icon)
        val names =resources.getStringArray(R.array.left_menu)
        val menus = names.mapIndexed { index, s ->
            MenuItem(
                name = s,
                icon = icon.getResourceId(index, 0)
            )
        }.toMutableList()
        viewModel.getTaskCountAll()
        viewModel.taskCountLiveData.observe(this@MainActivity) { count ->
            menuAdapter = LeftMenuAdapter(
                categories = mutableListOf(),
                menus = menus,
                onClickItem = {
                    viewBinding.drawerLayout.close()
                    setMenuAction(it)
                },
                onCategoryClick = { item, position ->
                    categoryClick(drawerLayout, item, position)
                }, onCreateNewClick = {
                    val newCategoryDialog = NewCategoryDialog { newItem ->
                        viewModel.insertCategoryManager(newItem,getString(R.string.all))
                        sendUpdateTaskFragmentIntent()
                    }
                    newCategoryDialog.show(
                        supportFragmentManager,
                        NewCategoryDialog::javaClass.name
                    )
                },
                getALlTask = count.toString()
            )
            viewBinding.rcvMenu.adapter = menuAdapter
            viewBinding.rcvMenu.layoutManager =  LinearLayoutManager(this)
        }
    }
    private fun regisAddTaskReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_COMPLETE_TASK)
            addAction(ADD_ACTION)
            addAction(ACTION_NOTIFY_PER)
            addAction(ACTION_OVERLAY_PER)
            addAction(ACTION_UPDATE_UI_TASK_ACTIVITY)
        }
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
            addTaskReceiver,
            intentFilter
        )
    }
    private fun categoryClick(drawerLayout: DrawerLayout, item: TabCategory, position: Int) {
        drawerLayout.close()
        val intent = Intent(ACTION_SELECT_CATEGORY)
        val bundle = Bundle()
        bundle.putParcelable(IntentConstants.item, item.copy(isSelected = true))
        bundle.putInt(IntentConstants.position, position)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun showDialogRatedAppSetting() {
        val ratingDialog = RatingDialog(this)
        ratingDialog.init( object : RatingDialog.OnPress {
            override fun rating() {
                // goToGooglePlay
                ratingDialog.dismiss()
            }
        })
        ratingDialog.show()
    }
    private fun shareApp() {
        val shareMessage = "Let me recommend you this application\n\n".plus(
            "https://play.google.com/store/apps/details?id=$packageName"
        )
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun showBottomSheet(intent: Intent?) {
        if (intent?.action == ADD_ACTION) {
            viewModel.resetLiveDataOfBottomSheet(getString(R.string.no_category))
            val bottomSheet =
                NewTaskBottomSheet(onClickInsert = { _ -> })
            val bundle = Bundle()
            bundle.putLong(BundleKey.KEY_CATEGORY_ID, CategoryConstants.NO_CATEGORY_ID)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, NewTaskBottomSheet::class.java.simpleName)
        }

    }

    private fun updateLanguageWidget(intent: Intent) {
        if (intent.action == LanguageActivity.ACTION_CHANGE_LANGUAGE_APP) {
            viewModel.updateStandardWidget()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@MainActivity).unregisterReceiver(addTaskReceiver)
    }

    enum class ShowStatus {
        IDLE, SHOW_DETAIL_TASK
    }
}