package com.example.demotodolist.presentation.di

import com.example.demotodolist.data.db.TaskCategoryDao
import com.example.demotodolist.data.repository.TaskCategoryRepositoryImpl
import com.example.demotodolist.domain.TaskCategoryRepository


import javax.inject.Singleton


object RepositoryModule {
    @Singleton
    fun provideTaskCategoryRepository(taskCategoryDao: TaskCategoryDao) : TaskCategoryRepository {
        return TaskCategoryRepositoryImpl(taskCategoryDao)
    }
}