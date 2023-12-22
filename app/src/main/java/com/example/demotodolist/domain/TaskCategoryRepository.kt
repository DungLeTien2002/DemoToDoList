package com.example.demotodolist.domain

import androidx.lifecycle.LiveData
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.NoOfTaskForEachCategory
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskCategoryRepository {
     fun updateTaskStatus(task: TaskInfo) : Int
     fun deleteTask(task: TaskInfo)
     fun insertTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo)
     fun deleteTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo)
     fun updateTaskAndAddDeleteCategory(taskInfo: TaskInfo, categoryInfoAdd: CategoryInfo, categoryInfoDelete: CategoryInfo)
     fun updateTaskAndAddCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo)
    fun getUncompletedTask(): LiveData<List<TaskCategoryInfo>>
    fun getCompletedTask(): LiveData<List<TaskCategoryInfo>>
    fun getUncompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>>
    fun getCompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>>
    fun getNoOfTaskForEachCategory(): LiveData<List<NoOfTaskForEachCategory>>
    fun getCategories(): LiveData<List<CategoryInfo>>
     fun getCountOfCategory(category: String) : Int
    suspend fun getActiveAlarms(currentTime : Date) : List<TaskInfo>
}