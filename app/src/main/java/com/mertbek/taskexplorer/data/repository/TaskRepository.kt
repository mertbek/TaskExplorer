package com.mertbek.taskexplorer.data.repository

import com.mertbek.taskexplorer.data.local.TaskDao
import com.mertbek.taskexplorer.data.model.LoginRequest
import com.mertbek.taskexplorer.data.model.LoginResponse
import com.mertbek.taskexplorer.data.model.TaskItem
import com.mertbek.taskexplorer.data.network.ApiService
import retrofit2.Response

class TaskRepository(
    private val apiService: ApiService,
    private val taskDao: TaskDao
) {

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        val basicAuth = "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz"
        return apiService.login(basicAuth, loginRequest)
    }

    suspend fun refreshTasks(token: String) {
        val authHeader = "Bearer ${token.trim()}"

        val response = apiService.getTasks(authHeader)

        if (response.isSuccessful && response.body() != null) {
            val taskList = response.body()!!
            taskDao.deleteAll()
            taskDao.insertAll(taskList)
        } else {
            val errorMsg = "API Error: ${response.code()} ${response.message()}"
            throw Exception(errorMsg)
        }
    }

    suspend fun getTasksFromDb(): List<TaskItem> {
        return taskDao.getAllTasks()
    }

    suspend fun searchTasks(query: String): List<TaskItem> {
        return taskDao.searchTasks(query)
    }
}