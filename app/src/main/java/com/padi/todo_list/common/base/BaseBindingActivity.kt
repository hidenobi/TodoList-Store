package com.padi.todo_list.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.padi.todo_list.R
import com.padi.todo_list.common.extension.changeContextByLanguageCode
import com.padi.todo_list.common.utils.PermissionUtils
import com.padi.todo_list.common.utils.UtilsJ.hideNavigationBar
import com.padi.todo_list.ui.permisiondialog.BatteryPermissionDialog
import com.padi.todo_list.ui.permisiondialog.NotifyPermissionDialog
import com.padi.todo_list.ui.permisiondialog.OverlayPermissionDialog

@SuppressLint("BatteryLife")
abstract class BaseBindingActivity<ViewModel : BaseViewModel, ViewBinding : ViewDataBinding>(private val layoutRes: Int) :
    AppCompatActivity() {
    private var _binding: ViewBinding? = null
    val viewBinding: ViewBinding get() = _binding!!
    abstract val viewModel: ViewModel
    protected val notificationIntent: Intent by lazy {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
    }
    protected val ignoreBatteryIntent: Intent by lazy {
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
    }
    protected val overlayIntent: Intent by lazy {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    }
    private val emptyLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    abstract fun initView(savedInstanceState: Bundle?)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutRes)
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//             Apply the insets as a margin to the view. This solution sets
//             only the bottom, left, and right dimensions, but you can apply whichever
//             insets are appropriate to your layout. You can also update the view padding
//             if that's more appropriate.
            v.updateLayoutParams<MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }
//             Return CONSUMED if you don't want want the window insets to keep passing
//             down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        viewBinding.lifecycleOwner = this
        initView(savedInstanceState)
//        hideNavigationBar(window)
        setNavigationColor(window, R.color.black, true)
//        lightStatusBar(window, true)

    }
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper(newBase?.changeContextByLanguageCode()))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setNavigationColor(window: Window, drawableRes: Int, isSetByColor: Boolean = false) {
        if (isSetByColor) {
            window.navigationBarColor = window.context.resources.getColor(drawableRes, null)
        } else {
            val background =
                ResourcesCompat.getDrawable(window.context.resources, drawableRes, null)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor =
                window.context.resources.getColor(android.R.color.transparent, null)
            window.setBackgroundDrawable(background)
        }
    }

    protected fun showBatteryPerDialog() {
        if (!PermissionUtils.checkBatteryPermission(this)) {
            BatteryPermissionDialog(onEstablish = {
                emptyLauncher.launch(ignoreBatteryIntent)
            }).apply {
                show(supportFragmentManager, BatteryPermissionDialog::javaClass.name)
            }
        }
    }

    protected fun showNotifyDialog() {
        if (!PermissionUtils.checkNotifyPermission(this)) {
            NotifyPermissionDialog(onSetNow = {
                emptyLauncher.launch(notificationIntent)
            }).apply {
                show(supportFragmentManager, NotifyPermissionDialog::javaClass.name)
            }
        }
    }

    protected fun showOverlayPerDialog() {
        if (!PermissionUtils.checkOverlayPermission(this)) {
            OverlayPermissionDialog(onEstablish = {
                emptyLauncher.launch(overlayIntent)
            }).apply {
                show(supportFragmentManager, OverlayPermissionDialog::javaClass.name)
            }
        }
    }
}