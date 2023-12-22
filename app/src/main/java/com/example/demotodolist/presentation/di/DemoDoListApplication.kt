package com.example.demotodolist.presentation.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.demotodolist.data.db.TaskCategoryDatabase
import com.example.demotodolist.data.db.TaskDatabase
import com.example.demotodolist.data.repository.TaskCategoryRepositoryImpl
import com.example.demotodolist.domain.TaskCategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DemoDoListApplication:Application() {
//    lateinit var sharedPreferences: SharedPreferences
//    override fun onCreate() {
//        super.onCreate()
//        if(sharedPreferences.getBoolean("dark_theme", false))
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//    }
val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { TaskCategoryDatabase.getDataBase(this,applicationScope) }
    val repository by lazy { TaskCategoryRepositoryImpl(database.taskCategoryDao()) }

}