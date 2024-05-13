package com.padi.todo_list.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.padi.todo_list.R
import com.padi.todo_list.common.base.SchedulerProvider
import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.database.dao.FileImageDao
import com.padi.todo_list.data.local.database.dao.NoteDao
import com.padi.todo_list.data.local.database.dao.ReminderTimeDao
import com.padi.todo_list.data.local.database.dao.SubtaskDao
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.EventTaskEntity
import com.padi.todo_list.data.local.model.FileImageEntity
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.data.local.model.ReminderTimeEntity
import com.padi.todo_list.data.local.model.SubtaskEntity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber

@Database(
    entities = [EventTaskEntity::class, SubtaskEntity::class, CategoryEntity::class, ReminderTimeEntity::class,NoteEntity::class, FileImageEntity::class],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventTaskDao(): EventTaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderTimeDao(): ReminderTimeDao
    abstract fun noteDao(): NoteDao
    abstract fun fileImageDao(): FileImageDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "todo-db")
                .createFromAsset("database/default.db")
                .build()
        }

    }

}