package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fetched_pages")
data class PageEntity(
    @PrimaryKey val id: Int = 1,
    val lastFetchedPage: Int = 1
)