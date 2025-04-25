package com.example.domain.di

import com.example.domain.usecase.GetLastPageUseCase
import com.example.domain.usecase.GetMangaUseCase
import com.example.domain.usecase.LoginUseCase
import org.koin.dsl.module

val useCaseModule = module{
    factory{ LoginUseCase(get()) }
    factory{ GetMangaUseCase(get()) }
    factory{ GetLastPageUseCase(get()) }
}