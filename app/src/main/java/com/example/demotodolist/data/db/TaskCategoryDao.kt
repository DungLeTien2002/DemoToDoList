package com.example.demotodolist.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.NoOfTaskForEachCategory
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskCategoryDao {
    @Insert
   fun insertTask(task : TaskInfo) : Long

    @Update
     fun updateTaskStatus(task: TaskInfo) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCategory(categoryInfo: CategoryInfo) : Long

    @Delete
    fun deleteTask(task: TaskInfo)

    @Delete
     fun deleteCategory(categoryInfo: CategoryInfo)

    @Transaction
    fun insertTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo){
        insertTask(taskInfo)
        insertCategory(categoryInfo)
    }

    @Transaction
     fun updateTaskAndAddCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo){
        updateTaskStatus(taskInfo)
        insertCategory(categoryInfo)
    }

    @Transaction
     fun updateTaskAndAddDeleteCategory(taskInfo: TaskInfo, categoryInfoAdd: CategoryInfo, categoryInfoDelete: CategoryInfo){
        updateTaskStatus(taskInfo)
        insertCategory(categoryInfoAdd)
        deleteCategory(categoryInfoDelete)
    }

    @Transaction
    fun deleteTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo){
        deleteTask(taskInfo)
        deleteCategory(categoryInfo)
    }

    @Transaction
    @Query("SELECT * " +
            "FROM taskInfo " +
            "WHERE status = 0 " +
            "ORDER BY date")
    fun getUncompletedTask(): LiveData<List<TaskCategoryInfo>>

    @Transaction
    @Query("SELECT * " +
            "FROM taskInfo " +
            "WHERE status = 1 " +
            "ORDER BY date")
    fun getCompletedTask(): LiveData<List<TaskCategoryInfo>>

    @Transaction
    @Query("SELECT * " +
            "FROM taskInfo " +
            "WHERE status = 0 " +
            "AND category =:category " +
            "ORDER BY date")
    fun getUncompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>>

    @Transaction
    @Query("SELECT * " +
            "FROM taskInfo " +
            "WHERE status = 1 " +
            "AND category = :category " +
            "ORDER BY date")
    fun getCompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>>

    @Query("SELECT " +
            "taskInfo.category as category," +
            "Count(*) as count, " +
            "categoryInfo.color as color  " +
            "FROM taskInfo, categoryInfo " +
            "WHERE taskInfo.category == categoryInfo.categoryInformation " +
            "AND " +
            "taskInfo.status = 0 " +
            "GROUP BY category " +
            "ORDER BY count DESC, category")

    fun getNoOfTaskForEachCategory(): LiveData<List<NoOfTaskForEachCategory>>

    @Query("SELECT * " +
            "FROM categoryInfo")
    fun getCategories(): LiveData<List<CategoryInfo>>

    @Query("SELECT * " +
            "FROM taskInfo")
    fun getTasks(): LiveData<List<TaskInfo>>

    @Query("SELECT COUNT(*) " +
            "FROM taskInfo " +
            "WHERE category = :category ")
    fun getCountOfCategory(category: String) : Int

    @Query("SELECT * " +
            "FROM taskInfo " +
            "WHERE status = 0 " +
            "AND date > :currentTime")
    fun getActiveAlarms(currentTime : Date) : List<TaskInfo>
}

