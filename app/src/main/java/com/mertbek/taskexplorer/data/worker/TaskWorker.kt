package com.mertbek.taskexplorer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mertbek.taskexplorer.TaskExplorerApp

class TaskWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as TaskExplorerApp
        val repository = app.repository
        val sessionManager = app.sessionManager

        return try {
            val token = sessionManager.fetchAuthToken()

            if (token.isNullOrBlank()) {
                Result.failure()
            } else {
                repository.refreshTasks(token)

                Result.success()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}