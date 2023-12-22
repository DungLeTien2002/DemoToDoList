package com.example.demotodolist.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = arrayOf(TaskInfo::class, CategoryInfo::class),
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
public abstract class TaskCategoryDatabase : RoomDatabase() {
    abstract fun taskCategoryDao(): TaskCategoryDao

    private class TaskCategoryDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var taskCategoryDao = database.taskCategoryDao()

                    var categoryInfo=CategoryInfo("coding","#1D59B3")
                    taskCategoryDao.insertCategory(categoryInfo)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskCategoryDatabase? = null

        fun getDataBase(context: Context, scope: CoroutineScope): TaskCategoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskCategoryDatabase::class.java,
                    "task_category_database"
                ).addCallback(TaskCategoryDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}