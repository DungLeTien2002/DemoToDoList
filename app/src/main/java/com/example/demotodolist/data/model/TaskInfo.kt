package com.example.demotodolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "taskInfo")
data class TaskInfo(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "priority") var priority: Int,
    @ColumnInfo(name = "status") var status: Boolean,
    @ColumnInfo(name = "category") var category: String
) : Serializable
