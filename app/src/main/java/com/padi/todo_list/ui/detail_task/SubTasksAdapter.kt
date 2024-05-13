package com.padi.todo_list.ui.detail_task

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.ui.task.model.SubTaskUI

class SubTasksAdapter(
    private var data: MutableList<SubTaskUI>,
    private val onDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
    private val detailTaskViewModel: DetailTaskViewModel
) : RecyclerView.Adapter<SubTaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder =
        SubTaskViewHolder.create(
            parent,
            ::changeStatus,
            ::onChangeContent,
            ::onDelete,
            onDrag,
            ::onFocus
        )

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.setTag(position)
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun initData(news: List<SubTaskUI>) {
        data.clear()
        data.addAll(news)
        notifyDataSetChanged()
    }

    fun addNewItem(newItem: SubTaskUI){
        data.add(newItem)
        notifyItemChanged(data.size - 1)
    }

    fun getData(): List<SubTaskUI> = data

    private fun changeStatus(position: Int) {
        if (position >= data.size) return
        data[position].isDone = !data[position].isDone
        notifyItemChanged(position)
        detailTaskViewModel.saveSubTask(data)
    }

    private fun onDelete(position: Int){
        if (position >= data.size) return
        val item = data[position]
        data.remove(item)
        notifyItemRemoved(position)
        detailTaskViewModel.deleteSubtask(item.id)
    }

    private fun onChangeContent(content: String, position: Int){
        if (position >= data.size) return
        data[position].content = content
        detailTaskViewModel.saveSubTask(data)
    }

    private fun onFocus(position: Int, focused: Boolean){
        if(position >= data.size) return
        data[position].focused = focused
    }

}