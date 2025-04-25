package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signed_in_user")
data class SignedInUserEntity(
    @PrimaryKey val id: Int = 1,
    val email: String
)
