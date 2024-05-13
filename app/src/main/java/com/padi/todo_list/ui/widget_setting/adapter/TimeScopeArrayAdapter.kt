package com.padi.todo_list.ui.widget_setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.padi.todo_list.R
import com.padi.todo_list.ui.widget_setting.model.TimeScopeModel

class TimeScopeArrayAdapter(
    context: Context,
    private var selectedItemScope: Int,
    private var onClickItem: (item: TimeScopeModel) -> Unit,
) : ArrayAdapter<TimeScopeModel>(context, 0) {
    private var list = ArrayList<TimeScopeModel>()
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view =
                LayoutInflater.from(context).inflate(R.layout.row_time_scope_option, parent, false)
        }
        val item = list[position]
        val textView = view as TextView
        textView.text = item.name
        if (item.scope == selectedItemScope) {
            textView.setTextColor(context.getColor(R.color.primary_purpul))
        }
        textView.setOnClickListener {
            onClickItem.invoke(list[position])
        }
        return view
    }

    fun updateSelectItem(scope: Int) {
        selectedItemScope = scope
    }

    fun setData(data: ArrayList<TimeScopeModel>) {
        list.clear()
        list.addAll(data)
    }
}