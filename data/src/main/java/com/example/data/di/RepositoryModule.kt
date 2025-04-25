package com.example.data.di

import com.example.data.repository.MangaRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.MangaRepository
import com.example.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module{
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<MangaRepository> { MangaRepositoryImpl(get(),get(), get()) }
}