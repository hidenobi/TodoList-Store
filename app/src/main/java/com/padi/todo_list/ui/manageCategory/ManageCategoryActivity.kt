package com.padi.todo_list.ui.manageCategory

import android.os.Bundle
import android.view.View
import android.view.ViewParent
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.extension.sendUpdateTaskActivityIntent
import com.padi.todo_list.common.extension.sendUpdateTaskFragmentIntent
import com.padi.todo_list.common.extension.showPopupMenuCategory
import com.padi.todo_list.common.utils.ItemHelperCallback
import com.padi.todo_list.common.utils.ItemTouchListener
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.common.utils.UtilsJ.getViewPointOnScr
import com.padi.todo_list.common.utils.UtilsJ.hideNavigationBar
import com.padi.todo_list.databinding.ActivityManageCategoriBinding
import com.padi.todo_list.ui.task.dialog.DeleteDialog
import com.padi.todo_list.ui.task.dialog.new_category.NewCategoryDialog
import com.padi.todo_list.ui.task.model.TabCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCategoryActivity :
    BaseBindingActivity<ManageCategoryViewModel, ActivityManageCategoriBinding>(R.layout.activity_manage_categori) {
    override val viewModel: ManageCategoryViewModel by viewModels()

    private val manageCategoryAdapter: ManageCategoryAdapter by lazy {
        ManageCategoryAdapter(arrayListOf(),
            showDialog = { item, view, position ->
                view.parent?.let {
                    showMenuDialog(it, view, position, item.id)
                }
            },
            onDrag = {
                itemTouchHelper.startDrag(it)
            }
        )
    }
    private val itemTouchHelper = ItemTouchHelper(ItemHelperCallback(object : ItemTouchListener {
        override fun onRowMoved(fromPosition: Int, toPosition: Int) {
            viewModel.move(fromPosition, toPosition, manageCategoryAdapter.getData())
            manageCategoryAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onRowSelected(viewHolder: RecyclerView.ViewHolder?) {

        }

        override fun onRowClear(viewHolder: RecyclerView.ViewHolder?) {

        }
    }))
    private fun showMenuDialog(parent: ViewParent, view: View, position: Int, id: Long) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val screenHeight =
            getViewPointOnScr(this.windowManager).y
        val disToBottom = screenHeight - (location[1] + view.height)
        showPopupMenuCategory(
            context = this@ManageCategoryActivity,
            anchorView = view,
            parent = parent,
            disToBottom = disToBottom,
            position,
            id,
            onEditClick = {
                val newCategoryDialog = NewCategoryDialog { newItem ->
                    viewModel.updateName(newItem, id)
                }
                newCategoryDialog.show(supportFragmentManager, NewCategoryDialog::javaClass.name)
            },
            onDeleteCategory = { _, _ ->
                deleteCategory(id)
            }
        )
    }
    private fun deleteCategory(id:Long){
        val dialogDelete =
            DeleteDialog(
                title = getString(R.string.txt_delete_category),
                content = getString(R.string.text_delete),
                onOK = {
                    viewModel.deleteEventTaskById(id)
                },
            )
        dialogDelete.show(supportFragmentManager, DeleteDialog::javaClass.name)
    }

    override fun initView(savedInstanceState: Bundle?) {
        hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        setUpCategoryView()
        observeData()
        action()
        viewModel.fetchData()
    }

    private fun setUpCategoryView() {
        viewBinding.rcvManageCategory.apply {
            itemTouchHelper.attachToRecyclerView(this)
            adapter = manageCategoryAdapter
        }
    }

    private fun action() {
        viewBinding.apply {
            toolbar.handleAction(leftAction = {
                finish()
            })
            createNew.setOnClickListener() {
                val newCategoryDialog = NewCategoryDialog { newItem ->
                    viewModel.insertCategoryManager(newItem)
                }
                newCategoryDialog.show(supportFragmentManager, NewCategoryDialog::javaClass.name)
            }
        }
    }

    private fun observeData() {
        viewModel.run {
            categories.observe(this@ManageCategoryActivity) {
                it.let { manageCategoryAdapter.setData(it) }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        sendUpdateTaskFragmentIntent()
        sendUpdateTaskActivityIntent()
    }
}