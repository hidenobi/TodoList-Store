package com.padi.todo_list.di

import android.content.Context
import com.padi.todo_list.data.local.database.AppDatabase
import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.database.dao.FileImageDao
import com.padi.todo_list.data.local.database.dao.NoteDao
import com.padi.todo_list.data.local.database.dao.ReminderTimeDao
import com.padi.todo_list.data.local.database.dao.SubtaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideEventTaskDao(appDatabase: AppDatabase): EventTaskDao {
        return appDatabase.eventTaskDao()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    fun provideSubtaskDao(appDatabase: AppDatabase): SubtaskDao {
        return appDatabase.subtaskDao()
    }

    @Provides
    fun provideReminderTimeDao(appDatabase: AppDatabase): ReminderTimeDao {
        return appDatabase.reminderTimeDao()
    }
    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao{
        return appDatabase.noteDao()
    }

    @Provides
    fun provideFileImageDao(appDatabase: AppDatabase): FileImageDao{
        return appDatabase.fileImageDao()
    }

}