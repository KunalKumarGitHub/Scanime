package com.example.data.di

import org.koin.dsl.module

val dataModule = module{
    includes(repositoryModule)
    includes(networkModule)
    includes(databaseModule)
}