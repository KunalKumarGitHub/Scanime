package com.example.data.local.manga


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.converters.RoomConverter
import com.example.data.local.user.UserDao
import com.example.data.model.MangaEntity
import com.example.data.model.PageEntity
import com.example.data.model.SignedInUserEntity
import com.example.data.model.UserEntity

@Database(
    entities = [MangaEntity::class, PageEntity::class, UserEntity::class, SignedInUserEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverter::class)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun userDao(): UserDao
}
