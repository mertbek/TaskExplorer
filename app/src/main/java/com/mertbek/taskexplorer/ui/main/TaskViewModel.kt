package com.mertbek.taskexplorer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertbek.taskexplorer.R
import com.mertbek.taskexplorer.data.local.SessionManager
import com.mertbek.taskexplorer.data.model.TaskItem
import com.mertbek.taskexplorer.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _taskList = MutableLiveData<List<TaskItem>>()
    val taskList: LiveData<List<TaskItem>> get() = _taskList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> get() = _errorMessage

    private val _sessionExpired = MutableLiveData<Boolean>()
    val sessionExpired: LiveData<Boolean> get() = _sessionExpired

    init {
        loadTasksFromDb()

        refreshTasks()
    }

    private fun loadTasksFromDb() {
        viewModelScope.launch {
            _taskList.value = repository.getTasksFromDb()
        }
    }

    fun refreshTasks() {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            _errorMessage.value = R.string.error_session_expired
            _sessionExpired.value = true
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.refreshTasks(token)
                loadTasksFromDb()
                _errorMessage.value = null
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""

                if (errorMsg.contains("401")) {
                    sessionManager.clearSession()
                    _sessionExpired.value = true
                } else {
                    _errorMessage.value = R.string.error_unknown
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadTasksFromDb()
            } else {
                _taskList.value = repository.searchTasks(query)
            }
        }
    }

    fun getUserInfo(): Map<String, String> {
        return sessionManager.fetchUserInfo()
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun setLanguage(code: String) {
        sessionManager.saveLanguage(code)
    }
}