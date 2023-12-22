package com.example.demotodolist.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.demotodolist.data.db.TaskCategoryDao
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.NoOfTaskForEachCategory
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import com.example.demotodolist.domain.TaskCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date


class TaskCategoryRepositoryImpl(private val taskCategoryDao:TaskCategoryDao):TaskCategoryRepository{

    override  fun updateTaskStatus(task: TaskInfo) : Int{
        return taskCategoryDao.updateTaskStatus(task)
    }

    override  fun deleteTask(task: TaskInfo) {
        taskCategoryDao.deleteTask(task)
    }

    override  fun insertTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        taskCategoryDao.insertTaskAndCategory(taskInfo, categoryInfo)
    }

    override  fun deleteTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        taskCategoryDao.deleteTaskAndCategory(taskInfo, categoryInfo)
    }

    override  fun updateTaskAndAddDeleteCategory(
        taskInfo: TaskInfo,
        categoryInfoAdd: CategoryInfo,
        categoryInfoDelete: CategoryInfo
    ) {
        taskCategoryDao.updateTaskAndAddDeleteCategory(taskInfo, categoryInfoAdd, categoryInfoDelete)
    }

    override  fun updateTaskAndAddCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        taskCategoryDao.updateTaskAndAddCategory(taskInfo, categoryInfo)
    }

    override fun getUncompletedTask(): LiveData<List<TaskCategoryInfo>> = taskCategoryDao.getUncompletedTask()
    override fun getCompletedTask(): LiveData<List<TaskCategoryInfo>> = taskCategoryDao.getCompletedTask()
    override fun getUncompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>> = taskCategoryDao.getUncompletedTaskOfCategory(category)
    override fun getCompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>> = taskCategoryDao.getCompletedTaskOfCategory(category)
    override fun getNoOfTaskForEachCategory(): LiveData<List<NoOfTaskForEachCategory>> = taskCategoryDao.getNoOfTaskForEachCategory()
    override fun getCategories(): LiveData<List<CategoryInfo>> = taskCategoryDao.getCategories()
    override  fun getCountOfCategory(category: String): Int = taskCategoryDao.getCountOfCategory(category)
    override suspend  fun getActiveAlarms(currentTime: Date): List<TaskInfo> {
        var list: List<TaskInfo>
        coroutineScope {
            list = withContext(Dispatchers.IO){taskCategoryDao.getActiveAlarms(currentTime)}
        }
        return list
    }

}