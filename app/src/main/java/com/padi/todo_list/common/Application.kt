package com.padi.todo_list.common

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.padi.todo_list.BuildConfig
import com.padi.todo_list.common.utils.CrashReportingTree
import com.padi.todo_list.data.local.database.AppDatabase
import com.padi.todo_list.data.local.prefs.AppPrefs
import com.padi.todo_list.notfication.NotificationController
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        private lateinit var instance: com.padi.todo_list.common.Application

        fun getInstance(): com.padi.todo_list.common.Application {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        RxJavaPlugins.setErrorHandler {
            it.message?.let {
                Timber.e("rx error: $it")
            }
        }
        AppPrefs.init(applicationContext)
        NotificationController.createNotificationChannel()
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

}