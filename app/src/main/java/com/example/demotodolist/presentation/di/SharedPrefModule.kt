package com.example.demotodolist.presentation.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

object SharedPrefModule {
    @Singleton
    fun provideTaskDatabase(app: Application): SharedPreferences {
        return app.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
}