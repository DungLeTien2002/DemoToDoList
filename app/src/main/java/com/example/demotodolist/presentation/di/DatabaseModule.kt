package com.example.demotodolist.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.demotodolist.data.db.TaskCategoryDao
import com.example.demotodolist.data.db.TaskDatabase
import javax.inject.Singleton


object DatabaseModule {
    @Singleton
    fun provideTaskDatabase(app: Application): TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java, "task_db")
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    fun provideTaskCategoryDao(taskDatabase: TaskDatabase): TaskCategoryDao {
        return taskDatabase.getTaskCategoryDao()
    }
}