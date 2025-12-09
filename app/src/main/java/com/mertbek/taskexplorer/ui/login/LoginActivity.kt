package com.mertbek.taskexplorer.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mertbek.taskexplorer.R
import com.mertbek.taskexplorer.TaskExplorerApp
import com.mertbek.taskexplorer.ui.ViewModelFactory
import com.mertbek.taskexplorer.ui.main.MainActivity
import com.mertbek.taskexplorer.util.LocaleHelper

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TaskExplorerApp

        val token = app.sessionManager.fetchAuthToken()

        if (!token.isNullOrBlank()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

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

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("task_explorer_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, languageCode))
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loginResult by viewModel.loginResult.observeAsState(initial = false)
    val errorMessageId by viewModel.errorMessage.observeAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    val strPressBack = stringResource(R.string.exit_press_again)
    val strLoginSuccess = stringResource(R.string.login_success)
    val strEmptyError = stringResource(R.string.error_empty_fields)

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? android.app.Activity)?.finish()
        } else {
            Toast.makeText(context, strPressBack, Toast.LENGTH_SHORT).show()
            lastBackPressTime = currentTime
        }
    }

    LaunchedEffect(key1 = errorMessageId) {
        errorMessageId?.let { id ->
            Toast.makeText(context, context.getString(id), Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = loginResult) {
        if (loginResult) {
            Toast.makeText(context, strLoginSuccess, Toast.LENGTH_SHORT).show()

            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)

            (context as? android.app.Activity)?.finish()
        }
    }

    fun validateAndLogin() {
        isUsernameError = false
        isPasswordError = false

        if (username.isBlank()) {
            isUsernameError = true
        }
        if (password.isBlank()) {
            isPasswordError = true
        }

        if (isUsernameError || isPasswordError) {
            Toast.makeText(context, strEmptyError, Toast.LENGTH_SHORT).show()
            return
        }

        focusManager.clearFocus()
        viewModel.login(username, password)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (isUsernameError) isUsernameError = false
                },
                label = { Text(text = stringResource(R.string.username_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isUsernameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (isPasswordError) isPasswordError = false
                },
                label = { Text(text = stringResource(R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { validateAndLogin() }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { validateAndLogin() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = stringResource(R.string.login_button))
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}