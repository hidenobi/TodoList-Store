package com.padi.todo_list.data.local.repository

import com.padi.todo_list.data.local.database.dao.CategoryDao
import com.padi.todo_list.data.local.database.dao.EventTaskDao
import com.padi.todo_list.data.local.model.CategoryEntity
import com.padi.todo_list.data.local.model.NoteEntity
import com.padi.todo_list.ui.task.model.TabCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ManageCategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val eventTaskDao: EventTaskDao
) : ManageCategoryRepository {

    override fun getTabTaskCounts(): Single<List<TabCategory>> {
        return categoryDao.getAllCategories()
            .toSingle()
            .flatMap { categories ->
                Flowable.fromIterable(categories)
                    .flatMapSingle { category ->
                        eventTaskDao.getEventTasksByCategoryId(category.id)
                            .map { eventTasks ->
                                TabCategory.newInstance(
                                    category,
                                    eventTasks.size
                                )
                            }.switchIfEmpty(Single.just(TabCategory.newInstance(category, 0)))
                    }.toList()
            }
    }

    override fun updateItemPosition(list: List<CategoryEntity>): Completable{
        return categoryDao.updateItemPosition(list)
    }

    override fun deleteById(id: Long?): Completable {
        return categoryDao.deleteById(id)
    }

    override fun updateName(name: String, id: Long): Completable {
        return categoryDao.updateName(name,id)
    }

}