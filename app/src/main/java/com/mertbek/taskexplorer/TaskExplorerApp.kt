package com.mertbek.taskexplorer

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mertbek.taskexplorer.data.local.AppDatabase
import com.mertbek.taskexplorer.data.local.SessionManager
import com.mertbek.taskexplorer.data.network.RetrofitClient
import com.mertbek.taskexplorer.data.repository.TaskRepository
import com.mertbek.taskexplorer.data.worker.TaskWorker
import java.util.concurrent.TimeUnit

class TaskExplorerApp : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val sessionManager by lazy { SessionManager(this) }

    val repository by lazy {
        TaskRepository(
            apiService = RetrofitClient.apiService,
            taskDao = database.taskDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        setupBackgroundWork()
    }

    private fun setupBackgroundWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWorkRequest = PeriodicWorkRequest.Builder(
            TaskWorker::class.java,
            60, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UPDATE_TASKS",
            ExistingPeriodicWorkPolicy.KEEP,
            refreshWorkRequest
        )
    }
}