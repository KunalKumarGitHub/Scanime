package com.example.data.repository

import android.content.Context
import com.example.data.local.user.UserDao
import com.example.data.model.SignedInUserEntity
import com.example.data.model.UserEntity
import com.example.domain.network.ResultWrapper
import com.example.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun login(email: String, password: String, context: Context): ResultWrapper<Boolean> {
        val user = userDao.getUserByEmail(email)

        return if (user == null) {
            userDao.insertUser(UserEntity(email, password))
            userDao.setSignedInUser(SignedInUserEntity(email = email))
            setUserSignedIn(context)
            ResultWrapper.Success(true)

        } else if (user.password == password) {
            userDao.setSignedInUser(SignedInUserEntity(email = email))
            setUserSignedIn(context)
            ResultWrapper.Success(true)

        } else {
            ResultWrapper.Failure(Exception("Wrong Password"))
        }
    }

    private fun setUserSignedIn(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_signed_in", true).apply()
    }
}


