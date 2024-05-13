package com.padi.todo_list.ui.task.adapter

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.R
import com.padi.todo_list.ui.task.lib.SwipeRevealLayout
import com.padi.todo_list.ui.task.lib.ViewBinderHelper
import com.padi.todo_list.ui.task.model.EventTaskUI

class ItemTouchHelperCallback(
    private val listener: OnSwipeListener,
    private val items: ArrayList<EventTaskUI>,
) : ItemTouchHelper.Callback() {
    private lateinit var swipeLayout: SwipeRevealLayout
    private val  binderHelper: ViewBinderHelper = ViewBinderHelper()

    interface OnSwipeListener {
        fun onSwipeRight(eventTaskUI: EventTaskUI, position: Int)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder is TasksViewHolder) {
            swipeLayout = viewHolder.itemView.findViewById(R.id.swipe_layout)
        }
        val position = viewHolder.bindingAdapterPosition
        val items = items[position]
        // Kiểm tra trạng thái của swipeLayout trước khi thực hiện
        if (swipeLayout.isClosed) {
            val dragFlags = 0
            val swipeFlags = ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, swipeFlags)
        }else{
            return 0
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        val items = items[position]
        if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipeRight(items,position)
        }
    }

    fun setData(listTask: ArrayList<EventTaskUI>) {
        items.clear()
        items.addAll(listTask)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // Thực hiện vẽ nếu cần
    }
}
