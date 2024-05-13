package com.padi.todo_list.data.local.repository

import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.ui.task.model.TabCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ManageCategoryRepository {
    fun getTabTaskCounts(): Single<List<TabCategory>>
    fun updateItemPosition(list: List<CategoryEntity>): Completable
    fun deleteById(id: Long?): Completable
    fun updateName(name:String,id: Long): Completable

}