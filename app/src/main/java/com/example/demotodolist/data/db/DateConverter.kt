package com.example.demotodolist.data.db

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun formTimeStamp(value: Long): Date = Date(value)

    @TypeConverter
    fun dateToTimeStamp(date: Date): Long = date.time
}