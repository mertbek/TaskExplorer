package com.mertbek.taskexplorer.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mertbek.taskexplorer.data.local.SessionManager
import com.mertbek.taskexplorer.data.model.LoginRequest
import com.mertbek.taskexplorer.data.repository.TaskRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: TaskRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Kullanıcı adı veya şifre boş olamaz"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(username, password))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = response.body()!!.oauth.accessToken

                    val user = body.userInfo
                    val fullName = "${user.firstName} ${user.lastName}"
                    sessionManager.saveUserInfo(
                        name = fullName,
                        personalNo = user.personalNo.toString(),
                        unit = user.businessUnit ?: "-"
                    )

                    sessionManager.saveAuthToken(token)
                    _loginResult.value = true
                } else {
                    _errorMessage.value = "Giriş başarısız! Lütfen bilgileri kontrol edin."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}