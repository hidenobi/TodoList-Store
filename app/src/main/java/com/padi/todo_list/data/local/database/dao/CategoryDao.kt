package com.padi.todo_list.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.padi.todo_list.data.local.database.BaseDao
import com.padi.todo_list.data.local.model.CategoryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
abstract class CategoryDao : BaseDao<CategoryEntity> {
    @Query("SELECT * FROM category_entity ORDER BY position")
    abstract fun getAllCategories(): Maybe<List<CategoryEntity>>

    @Query("SELECT * FROM category_entity WHERE id =:id")

    abstract fun getCategory(id: Long): Single<CategoryEntity>
    @Query("UPDATE category_entity SET position = :newPosition WHERE id = :itemId")
    abstract fun updateItemPosition(itemId: Long, newPosition: Int)

    @Update
    abstract fun updateItemPosition(list: List<CategoryEntity>): Completable
    @Query("DELETE FROM category_entity WHERE id = :id")
    abstract fun deleteById(id: Long?): Completable
    @Query("UPDATE category_entity SET category_name = :name WHERE id = :id")
    abstract fun updateName(name: String, id: Long): Completable
}