package com.mertbek.taskexplorer

import android.app.Application
import com.mertbek.taskexplorer.data.local.AppDatabase
import com.mertbek.taskexplorer.data.local.SessionManager
import com.mertbek.taskexplorer.data.network.RetrofitClient
import com.mertbek.taskexplorer.data.repository.TaskRepository
import kotlin.getValue

class TaskExplorerApp : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val sessionManager by lazy { SessionManager(this) }

    val repository by lazy {
        TaskRepository(
            apiService = RetrofitClient.apiService,
            taskDao = database.taskDao()
        )
    }
}