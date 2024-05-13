package com.padi.todo_list.common.extension

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.ui.detail_task.KEY_DELETE
import com.padi.todo_list.ui.detail_task.KEY_DUPLICATE
import com.padi.todo_list.ui.detail_task.KEY_MARK_DONE
import com.padi.todo_list.ui.detail_task.KEY_SHARE
import com.padi.todo_list.ui.my.KEY_ALL
import com.padi.todo_list.ui.my.KEY_ONE_MONTH
import com.padi.todo_list.ui.my.KEY_ONE_WEEK
import com.padi.todo_list.ui.task.adapter.FlagAdapter
import com.padi.todo_list.ui.task.adapter.ItemClickListener
import com.padi.todo_list.ui.task.model.FlagUI
import com.padi.todo_list.ui.task.model.TabCategory

//import com.padi.todo_list.ui.task.adapter.FlagAdapter
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.temporaryLockView() {
    if (isEnabled) {
        isEnabled = false
        postDelayed({ isEnabled = true }, 450)
    }
}

typealias ViewOnClickListener = (View) -> Unit

fun View.setOnDebounceClickListener(debounce: Long = 500L, action: ViewOnClickListener) {
    setOnClickListener(object : ViewOnClickListener {

        private var lastClickTime: Long = 0

        override fun invoke(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounce) return
            action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


@SuppressLint("Recycle")
fun showPopupMenuFlags(
    context: Context, imageView: ImageView,
    onFlagClick: (flagType: Int) -> Unit,
) {
    val inflater = LayoutInflater.from(context)
    val popupView = inflater.inflate(R.layout.custom_popup_menu_flags, null)
    val popupWindow = PopupWindow(
        popupView, WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT, true
    )
    val recyclerView: RecyclerView = popupView.findViewById(R.id.rvFlag)
    recyclerView.layoutManager = GridLayoutManager(context, 5)
    val drawableIds = context.resources.obtainTypedArray(R.array.array_flag)
    val listItem = ArrayList<FlagUI>()

    for (i in 0 until drawableIds.length()) {
        val drawableId = drawableIds.getResourceId(i, 0)
        listItem.add(FlagUI(icon = drawableId))
    }
    val btnClear:TextView = popupView.findViewById(R.id.clear)
    btnClear.setOnClickListener(){
        val flagType = getDrawableIdForFlagType(-1)
        onFlagClick.invoke(flagType)
        imageView.setImageResource(R.drawable.ic_flag_unselect)
        imageView.invalidate()
        popupWindow.dismiss()
    }
    val adapter = FlagAdapter(listItem, object : ItemClickListener {
        override fun onClick(view: View, position: Int) {
//            val drawableId =
//            val flagType = listItem[position].icon
            val drawableId = listItem[position].icon
            imageView.setImageResource(drawableId)
            val flagType = getDrawableIdForFlagType(drawableId)
            onFlagClick.invoke(flagType)
            imageView.invalidate()
            popupWindow.dismiss()
            Log.d("TAG", "onClick: $drawableId")
            Log.d("TAG", "onClick: $flagType")

        }

    })

    recyclerView.adapter = adapter
    val contentView = popupWindow.contentView
    contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val density = context.resources.displayMetrics.density
    val xOffsetDp = 210
    val xOffsetPx = (xOffsetDp * density + 0.5f).toInt()

    popupWindow.showAsDropDown(imageView, -imageView.width - xOffsetPx, 0)
}
fun showPopupMenuCategory(
    context: Context,
    anchorView: View,
    parent: ViewParent,
    disToBottom: Int,
    position: Int,
    id: Long,
    onEditClick: () -> Unit,
    onDeleteCategory: (id: Long, position: Int) -> Unit
) {

    LayoutInflater.from(context)
        .inflate(R.layout.custom_popup_menu_category, parent as ViewGroup, false).apply {
            PopupWindow(
                this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true
            ).apply {
                findViewById<View>(R.id.edit_category)?.setOnClickListener{
                    onEditClick.invoke()
                    dismiss()
                }

                findViewById<View>(R.id.delete_category)?.setOnClickListener {
                    onDeleteCategory.invoke(id,position)
                    dismiss()
                }
                contentView.apply {
                    measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    showAsDropDown(anchorView, -(measuredWidth) + (anchorView.width / 2), 0)
                }
            }
        }
}
fun getDrawableIdForFlagType(flagType: Int?): Int {
    return when (flagType) {
        R.drawable.ic_flag_select_1 -> 1
        R.drawable.ic_flag_select_2 -> 2
        R.drawable.ic_flag_select_3 -> 3
        R.drawable.ic_flag_select_4 -> 4
        R.drawable.ic_flag_select_5 -> 5
        R.drawable.ic_flag_select_6 -> 6
        R.drawable.ic_flag_select_7 -> 7
        R.drawable.ic_flag_select_8 -> 8
        R.drawable.ic_flag_select_9 -> 9
        R.drawable.ic_flag_select_10 -> 10
        R.drawable.ic_flag_select_11 -> 11
        R.drawable.ic_flag_select_12 -> 12
        R.drawable.ic_flag_select_13 -> 13
        R.drawable.ic_flag_select_14 -> 14
        R.drawable.ic_flag_select_15 -> 15
        R.drawable.ic_flag_select_16 -> 16
        R.drawable.ic_flag_select_17 -> 17
        R.drawable.ic_flag_select_18 -> 18
        R.drawable.ic_flag_select_19 -> 19
        R.drawable.ic_flag_select_20 -> 20
        else -> R.drawable.ic_flag_unselect
    }
}

fun hideItem() {

}

fun showPopupMenu(
    context: Context, anchorView: View,
    onClickManager: () -> Unit,
    onClickSearch: () -> Unit,
    onCLickArrange: () -> Unit
) {
    val inflater = LayoutInflater.from(context)
    val popupView = inflater.inflate(R.layout.custom_menu_item, null)
    val popupWindow = PopupWindow(
        popupView, WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT, true
    )

    popupView.findViewById<View>(R.id.manage_categories)?.setOnClickListener {
        onClickManager.invoke()
        popupWindow.dismiss()
    }

    popupView.findViewById<View>(R.id.search)?.setOnClickListener {
        onClickSearch.invoke()
        popupWindow.dismiss()
    }
    popupView.findViewById<View>(R.id.arrage_task)?.setOnClickListener {
        onCLickArrange.invoke()
        popupWindow.dismiss()
    }

    val density = context.resources.displayMetrics.density
    val xOffsetDp = 70
    val xOffsetPx = (xOffsetDp * density + 0.5f).toInt()

    // Hiển thị PopupWindow tại vị trí của anchorView
    popupWindow.showAsDropDown(anchorView, -anchorView.width - xOffsetPx, 0)
}

fun showPeriodMenu(
    context: Context,
    anchorView: View,
    onClickMenu: (type: String) -> Unit,
    parent: ViewParent,
    disToBottom: Int,
) {
    LayoutInflater.from(context)
        .inflate(R.layout.custom_menu_minescr, parent as ViewGroup, false).apply {
            PopupWindow(
                this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true
            ).apply {
                findViewById<View>(R.id.tvOneWeek)?.setOnClickListener {
                    onClickMenu.invoke(KEY_ONE_WEEK)
                    dismiss()
                }

                findViewById<View>(R.id.tvOneMonth)?.setOnClickListener {
                    onClickMenu.invoke(KEY_ONE_MONTH)
                    dismiss()
                }
                findViewById<View>(R.id.tvAll)?.setOnClickListener {
                    onClickMenu.invoke(KEY_ALL)
                    dismiss()
                }
                contentView.apply {
                    measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    showAsDropDown(anchorView, -anchorView.width-70, if(disToBottom<measuredHeight)-measuredHeight-anchorView.height else 0)
                }
            }
        }
}

fun showDetailMenu(
    isComplete: Boolean,
    context: Context,
    anchorView: View,
    onClickMenu: (type: String) -> Unit,
) {
    LayoutInflater.from(context)
        .inflate(R.layout.menu_detail_scrn, null, false).apply {
            PopupWindow(
                this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true
            ).apply {
                findViewById<TextView>(R.id.tvMarkDone)?.run{
                    text = context.getString(if(isComplete) R.string.mark_it_undone else R.string.mark_it_done)
                    setOnClickListener {
                        onClickMenu.invoke(KEY_MARK_DONE)
                        dismiss()
                    }
                }
                findViewById<View>(R.id.tvDuplicate)?.setOnClickListener {
                    onClickMenu.invoke(KEY_DUPLICATE)
                    dismiss()
                }
                findViewById<View>(R.id.tvShare)?.setOnClickListener {
                    onClickMenu.invoke(KEY_SHARE)
                    dismiss()
                }
                findViewById<View>(R.id.tvDelete)?.setOnClickListener {
                    onClickMenu.invoke(KEY_DELETE)
                    dismiss()
                }
                contentView.apply {
                    measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    showAsDropDown(anchorView, -(measuredWidth) + (anchorView.width / 2), 0)
                }
            }
        }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatEditText.requestFocusWithKeyboard() {
    postDelayed({
        dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                0f,
                0f,
                0
            )
        )
        dispatchTouchEvent(
            MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP,
                0f,
                0f,
                0
            )
        )
        setSelection(length())
    }, 100)
}

fun View.showKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


/**
 * Runs a FragmentTransaction, then calls commit().
 */
inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}


fun RecyclerView.addOnLoadMoreListener(onLoadMore: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val itemCount = adapter?.itemCount ?: 0
            if (layoutManager is LinearLayoutManager) {
                var lastPost =
                    (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (lastPost > itemCount - 2 && itemCount >= 2) {
                    onLoadMore.invoke()
                }
            }
        }
    })
}


fun Context.showAlert(title: String, msg: String, ok: () -> Unit) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(msg)
        setCancelable(false)
        setPositiveButton("Ok") { dialog, v ->
            dialog.dismiss()
            ok.invoke()
        }
    }.show()
}

fun CompoundButton.setButtonTintListCompat(colorTrueResId: Int, colorFalseResId: Int) {
    val colorTrue = ContextCompat.getColorStateList(context, colorTrueResId)
    val colorFalse = ContextCompat.getColorStateList(context, colorFalseResId)
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        ),
        intArrayOf(colorTrue!!.defaultColor, colorFalse!!.defaultColor)
    )
    CompoundButtonCompat.setButtonTintList(this, colorStateList)
}

fun RecyclerView.setHeight(size: Int) {
    if (size >= 4) {
        val dip = 120f
        val r: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        val params: ViewGroup.LayoutParams = this.layoutParams
        params.height = px.toInt()
        this.setLayoutParams(params)
    } else {
        val params: ViewGroup.LayoutParams = this.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.setLayoutParams(params)
    }
}

fun View.startAnimTranslateY(context: Context) {
    this.startAnimation(
        AnimationUtils.loadAnimation(
            context,
            R.anim.content_fabbtn_anim
        )
    )
}


