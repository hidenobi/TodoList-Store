package com.padi.todo_list.ui.setting

import com.padi.todo_list.common.Application
import com.padi.todo_list.common.base.BaseViewModel
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.common.extension.add
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import com.padi.todo_list.receiver.widget.WidgetStandard
import com.padi.todo_list.ui.task.model.EventTaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val widgetSettingRepo: WidgetSettingRepositoryImpl) :
    BaseViewModel() {
    fun updateWidget() {
        widgetSettingRepo.getPrevData(
            AppPrefs.scopeTime,
            AppPrefs.scopeCategory,
            AppPrefs.showCompletedTask
        )
            .observeOn(SchedulerProvider.ui())
            .subscribeBy(
                onSuccess = {
                    Timber.d("updateWidget-WidgetStandard: success}")
                    WidgetStandard.forceUpdateAll(
                        Application.getInstance().applicationContext,
                        it as ArrayList<EventTaskUI>
                    )
                },
                onError = {
                    Timber.d("updateWidget-WidgetStandard: error onUpdate: ${it.message}")
                },
            ).add(subscriptions)
    }

}