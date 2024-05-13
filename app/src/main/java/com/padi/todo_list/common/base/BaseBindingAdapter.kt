package com.padi.todo_list.common.base

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors

abstract class BaseBindingAdapter<M, H : BaseBindingViewHolder<M>>(diffCallback: DiffUtil.ItemCallback<M>) :
    ListAdapter<M, H>(
        AsyncDifferConfig.Builder<M>(diffCallback)
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
            .build()
    ), BindDataAdapter<List<M>> {

}