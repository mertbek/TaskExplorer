package com.mertbek.taskexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mertbek.taskexplorer.data.local.SessionManager
import com.mertbek.taskexplorer.data.repository.TaskRepository
import com.mertbek.taskexplorer.ui.login.LoginViewModel

class ViewModelFactory(
    private val repository: TaskRepository,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            if (sessionManager == null) throw IllegalArgumentException("SessionManager requirement")
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, sessionManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}