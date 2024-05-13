package com.padi.todo_list.di

import com.padi.todo_list.data.local.repository.AlarmRepository
import com.padi.todo_list.data.local.repository.AlarmRepositoryImpl
import com.padi.todo_list.data.local.repository.ManageCategoryRepository
import com.padi.todo_list.data.local.repository.ManageCategoryRepositoryImpl
import com.padi.todo_list.data.local.repository.NewTaskRepository
import com.padi.todo_list.data.local.repository.NewTaskRepositoryImpl
import com.padi.todo_list.data.local.repository.WidgetSettingRepository
import com.padi.todo_list.data.local.repository.WidgetSettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideNewTaskRepository(
        repo: NewTaskRepositoryImpl
    ): NewTaskRepository = repo

    @Provides
    @Singleton
    fun provideManageCategoryRepository(
        repo: ManageCategoryRepositoryImpl
    ): ManageCategoryRepository = repo

    @Provides
    @Singleton
    fun provideAlarmRepositoryImpl(
        repo: AlarmRepositoryImpl
    ): AlarmRepository = repo

    @Provides
    @Singleton
    fun provideWidgetSettingRepositoryImpl(
        repo: WidgetSettingRepositoryImpl
    ): WidgetSettingRepository = repo

}