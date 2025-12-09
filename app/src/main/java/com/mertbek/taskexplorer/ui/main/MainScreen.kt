package com.mertbek.taskexplorer.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.mertbek.taskexplorer.data.model.TaskItem
import com.mertbek.taskexplorer.ui.camera.CameraPreviewScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: TaskViewModel
) {
    val taskList by viewModel.taskList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera = true
        } else {
            Toast.makeText(context, "Kamera izni gerekli!", Toast.LENGTH_SHORT).show()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.refreshTasks() }
    )

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    if (showCamera) {
        Dialog(
            onDismissRequest = { showCamera = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreviewScreen(
                    onQrDetected = { qrCode ->
                        searchQuery = qrCode
                        viewModel.searchTasks(qrCode)
                        showCamera = false
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.searchTasks(it)
                },
                onSearch = { isSearchActive = false },
                active = false,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Task ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            showCamera = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scan")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {}
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(taskList) { task ->
                    TaskItemRow(task = task)
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )

            if (taskList.isEmpty() && !isLoading) {
                Text(
                    text = "Gösterilecek görev yok.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TaskItemRow(task: TaskItem) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val colorHex = task.colorCode ?: "#CCCCCC"
            val color = try {
                Color(AndroidColor.parseColor(colorHex))
            } catch (e: Exception) {
                Color.Gray
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = task.task ?: "İsimsiz Görev",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = task.title ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!task.description.isNullOrEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }
        }
    }
}