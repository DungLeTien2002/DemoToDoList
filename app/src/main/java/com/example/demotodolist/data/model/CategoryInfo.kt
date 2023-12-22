package com.example.demotodolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "categoryInfo")
data class CategoryInfo(
    @PrimaryKey
    var categoryInformation: String,
    @ColumnInfo(name = "color") var color: String
) : Serializable
