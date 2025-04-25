package com.example.domain.repository

import android.content.Context
import com.example.domain.network.ResultWrapper

interface UserRepository {
    suspend fun login(email: String, password: String, context: Context): ResultWrapper<Boolean>
}