package com.mertbek.taskexplorer.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.mertbek.taskexplorer.R
import com.mertbek.taskexplorer.data.model.TaskItem
import com.mertbek.taskexplorer.ui.camera.CameraPreviewScreen
import com.mertbek.taskexplorer.ui.login.LoginActivity
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: TaskViewModel
) {
    val taskList by viewModel.taskList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessageId by viewModel.errorMessage.observeAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as? Activity

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userInfo = remember { viewModel.getUserInfo() }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }

    val sessionExpired by viewModel.sessionExpired.observeAsState(false)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera = true
        } else {
            Toast.makeText(context, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.refreshTasks() }
    )

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let { id ->
            Toast.makeText(context, context.getString(id), Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            Toast.makeText(context, context.getString(R.string.error_session_expired), Toast.LENGTH_LONG).show()

            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Text(
                        text = userInfo["name"] ?: stringResource(id = R.string.user_default),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(id = R.string.id_label) + " ${userInfo["id"]}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = userInfo["unit"] ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(4.dp))

                NavigationDrawerItem(
                    label = { Text( text = stringResource(id = R.string.menu_tasks)) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.menu_languages),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    fun changeLanguage(code: String) {
                        viewModel.setLanguage(code)
                        scope.launch { drawerState.close() }
                        activity?.recreate()
                    }

                    OutlinedButton(onClick = { changeLanguage("en") }) { Text("EN") }
                    OutlinedButton(onClick = { changeLanguage("tr") }) { Text("TR") }
                    OutlinedButton(onClick = { changeLanguage("de") }) { Text("DE") }
                }

                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.menu_logout)) },
                    icon = { Icon(Icons.Default.ExitToApp, null) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchTasks(it)
                    },
                    onSearch = {
                        isSearchActive = false
                        focusManager.clearFocus()
                    },
                    active = false,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text( text = stringResource(id = R.string.search_placeholder)) },
                    leadingIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu_desc))
                        }
                    },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    viewModel.searchTasks("")
                                    focusManager.clearFocus()
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear_desc))
                                }
                            }
                            IconButton(onClick = {
                                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) showCamera = true
                                else permissionLauncher.launch(Manifest.permission.CAMERA)
                            }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(id = R.string.qr_scan_desc))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
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
                    Text(text = stringResource(id = R.string.no_tasks_message), modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                }
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
                    text = task.task ?: stringResource(R.string.no_tasks_message),
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