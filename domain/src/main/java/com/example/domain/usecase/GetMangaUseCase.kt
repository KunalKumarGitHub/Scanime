package com.example.domain.usecase

import com.example.domain.repository.MangaRepository


class GetMangaUseCase(
    private val repository: MangaRepository
) {
    suspend fun execute(page: String, genres: String, nsfw: String, type: String) = repository.fetchMangaData(page, genres, nsfw, type)
}
