package com.mertbek.taskexplorer.ui.main

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mertbek.taskexplorer.TaskExplorerApp
import com.mertbek.taskexplorer.ui.ViewModelFactory
import com.mertbek.taskexplorer.util.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TaskExplorerApp
        val factory = ViewModelFactory(app.repository, app.sessionManager)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val viewModel: TaskViewModel = viewModel(factory = factory)

                    MainScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("task_explorer_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "en") ?: "en"

        super.attachBaseContext(LocaleHelper.setLocale(newBase, languageCode))
    }
}