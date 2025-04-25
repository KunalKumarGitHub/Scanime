package com.example.domain.repository

import com.example.domain.model.MangaDomainModel
import com.example.domain.network.ResultWrapper

interface MangaRepository {
    suspend fun fetchMangaData(page: String, genres: String, nsfw: String, type: String): ResultWrapper<MangaDomainModel>
    suspend fun fetchLastPage(): Int?
}
