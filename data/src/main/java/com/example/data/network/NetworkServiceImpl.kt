package com.example.data.network

import com.example.data.remote.MangaApiService
import com.example.domain.model.MangaDomainModel
import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper

class NetworkServiceImpl(private val mangaApiService: MangaApiService): NetworkService {

    override suspend fun fetchMangaData(page: String, genres: String, nsfw: String, type: String): ResultWrapper<MangaDomainModel> {
        val response = mangaApiService.fetchMangaData(page, genres, nsfw, type)
        return ResultWrapper.Success(response.toDomainModel())
    }
}