package com.example.domain.usecase

import com.example.domain.repository.MangaRepository

class GetLastPageUseCase(
    private val repository: MangaRepository
) {
    suspend fun execute() = repository.fetchLastPage()
}