package com.mertbek.taskexplorer.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mertbek.taskexplorer.TaskExplorerApp
import com.mertbek.taskexplorer.ui.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TaskExplorerApp
        val factory = ViewModelFactory(app.repository, app.sessionManager)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val loginViewModel: LoginViewModel = viewModel(factory = factory)

                    LoginScreen(viewModel = loginViewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loginResult by viewModel.loginResult.observeAsState(initial = false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    var username by remember { mutableStateOf("365") }
    var password by remember { mutableStateOf("1") }

    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = loginResult) {
        if (loginResult) {
            Toast.makeText(context, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()

            val intent = android.content.Intent(context, com.mertbek.taskexplorer.ui.main.MainActivity::class.java)
            context.startActivity(intent)

            (context as? android.app.Activity)?.finish()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Task Explorer",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Kullanıcı Adı") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Giriş Yap")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}