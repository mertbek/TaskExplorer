package com.mertbek.taskexplorer.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mertbek.taskexplorer.TaskExplorerApp
import com.mertbek.taskexplorer.ui.ViewModelFactory

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
}