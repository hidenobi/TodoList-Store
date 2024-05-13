package com.padi.todo_list.service

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.padi.todo_list.data.local.model.AlarmModel

class OverlayInfoAdapter : RecyclerView.Adapter<OverlayInfoViewHolder>() {
    private var data: ArrayList<AlarmModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverlayInfoViewHolder {
        return OverlayInfoViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: OverlayInfoViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: ArrayList<AlarmModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

}
