package com.example.domain.network

import com.example.domain.model.MangaDomainModel
import com.example.domain.model.UserDomainModel

interface NetworkService {
    suspend fun fetchMangaData(page: String, genres: String, nsfw: String, type: String): ResultWrapper<MangaDomainModel>
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val exception: Exception) : ResultWrapper<Nothing>()
}