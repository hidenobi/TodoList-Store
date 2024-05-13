package com.padi.todo_list.data.local.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert( vararg obj: T): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert( obj: T): Single<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(objs: List<T>): Single<List<Long>>

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: T): Completable

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T): Completable
}