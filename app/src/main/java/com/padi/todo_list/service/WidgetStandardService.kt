package com.padi.todo_list.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.padi.todo_list.common.Application
import com.padi.todo_list.factory.WidgetStandardFactory
import timber.log.Timber

class WidgetStandardService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Timber.d("WidgetStandardService: onGetViewFactory")

        return WidgetStandardFactory(Application.getInstance().applicationContext, intent)
    }

}