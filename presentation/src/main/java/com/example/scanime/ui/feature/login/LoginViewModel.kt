package com.example.scanime.ui.feature.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage = _errorMessage

    fun login(email: String, password: String, context: Context) {
        _errorMessage.value = ""
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val response = loginUseCase.execute(email, password, context)
            when (response) {
                is ResultWrapper.Success -> {
                    _loginState.value = LoginState.Success
                }

                is ResultWrapper.Failure -> {
                    _errorMessage.value = response.exception.message?: "Something went wrong"
                    _loginState.value = LoginState.Idle
                }
            }
        }
    }

    fun setErrorMessage(message: String){
        _errorMessage.value = message
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
}