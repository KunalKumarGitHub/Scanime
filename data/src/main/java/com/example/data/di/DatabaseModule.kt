package com.example.data.di

import androidx.room.Room
import com.example.data.local.manga.MangaDatabase
import org.koin.dsl.module

val databaseModule = module {

    single<MangaDatabase> {
        Room.databaseBuilder(
            get(),
            MangaDatabase::class.java,
            "manga_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { get<MangaDatabase>().mangaDao() }
    single { get<MangaDatabase>().userDao() }
}