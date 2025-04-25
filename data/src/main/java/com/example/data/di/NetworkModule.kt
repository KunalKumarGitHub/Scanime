package com.example.data.di

import com.example.data.network.NetworkServiceImpl
import com.example.data.remote.MangaApiService
import com.example.domain.network.NetworkService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module{
    single {
        Retrofit.Builder()
            .baseUrl("https://mangaverse-api.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(MangaApiService::class.java)
    }
    single<NetworkService> {
        NetworkServiceImpl(get())
    }
}