package com.example.domain.usecase

import android.content.Context
import com.example.domain.repository.UserRepository

class LoginUseCase(private val userRepository: UserRepository) {
    suspend fun execute(email: String, password: String, context: Context) = userRepository.login(email, password, context)
}