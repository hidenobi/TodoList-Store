package com.padi.todo_list.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category_entity")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "category_name")
    var name: String,
    @ColumnInfo(name = "position")
    var position : Int = 0
) : Serializable
