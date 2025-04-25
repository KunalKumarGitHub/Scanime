package com.example.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SignedInUserEntity
import com.example.data.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user_data WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSignedInUser(user: SignedInUserEntity)

}

