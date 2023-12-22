package com.example.demotodolist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demotodolist.data.model.CategoryInfo
import com.example.demotodolist.data.model.NoOfTaskForEachCategory
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import com.example.demotodolist.data.repository.TaskCategoryRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivityViewModel(private val repository: TaskCategoryRepositoryImpl) : ViewModel() {
    fun updateTaskStatus(task: TaskInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTaskStatus(task)
        }
    }

    fun getUncompletedTaskOfCategory(category: String): LiveData<List<TaskCategoryInfo>> {
        return repository.getUncompletedTaskOfCategory(category)
    }

    fun getCompletedTask(): LiveData<List<TaskCategoryInfo>> {
        return repository.getCompletedTask()
    }

    fun deleteTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskAndCategory(taskInfo, categoryInfo)
        }
    }

    fun deleteTask(task: TaskInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun updateTaskAndAddCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTaskAndAddCategory(taskInfo, categoryInfo)
        }
    }

    public fun insertTaskAndCategory(taskInfo: TaskInfo, categoryInfo: CategoryInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTaskAndCategory(taskInfo, categoryInfo)
        }
    }

    public fun getUncompletedTask(): LiveData<List<TaskCategoryInfo>> {
        return repository.getUncompletedTask()
    }

    fun getNoOfTaskForEachCategory(): LiveData<List<NoOfTaskForEachCategory>> {
        return repository.getNoOfTaskForEachCategory()
    }

    fun getCategories(): LiveData<List<CategoryInfo>> {
        return repository.getCategories()
    }

    suspend fun getCountOfCategory(category: String): Int {
        var count: Int
        coroutineScope() {
            count = withContext(Dispatchers.IO) { repository.getCountOfCategory(category) }
        }
        return count
    }

    fun updateTaskAndAddDeleteCategory(
        taskInfo: TaskInfo,
        categoryInfoAdd: CategoryInfo,
        categoryInfoDelete: CategoryInfo
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTaskAndAddDeleteCategory(taskInfo, categoryInfoAdd, categoryInfoDelete)
        }
    }

    class MainActivityViewModelFactory(private val repository: TaskCategoryRepositoryImpl) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}