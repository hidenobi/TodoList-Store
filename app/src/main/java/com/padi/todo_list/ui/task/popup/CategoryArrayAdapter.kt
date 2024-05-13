package com.padi.todo_list.ui.task.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.padi.todo_list.R
import com.padi.todo_list.data.local.model.CategoryEntity

class CategoryArrayAdapter(
    context: Context,
    private var selectedItemID: Int,
    private var onClickItem: (item: CategoryEntity) -> Unit,
) : ArrayAdapter<CategoryEntity>(context, 0) {

    private var list = ArrayList<CategoryEntity>()
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_category_option, parent, false)
        }
        val item = list[position]
        val textView = view as TextView

        textView.text = item.name

        textView.setOnClickListener {
            onClickItem.invoke(list[position])
        }

        if (item.id.toInt() == selectedItemID || position == list.size - 1) {
            textView.setTextColor(context.getColor(R.color.primary_purpul))
        }
        if (position == list.size - 1) {
            textView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, R.drawable.ic_plus), // drawableStart
                null, // drawableTop
                null, // drawableEnd
                null  // drawableBottom
            )
        }

        return view
    }

    fun updateSelectItem(id: Int) {
        selectedItemID = id
    }

    fun setData(data: ArrayList<CategoryEntity>) {
        list.clear()
        list.addAll(data)
    }
}