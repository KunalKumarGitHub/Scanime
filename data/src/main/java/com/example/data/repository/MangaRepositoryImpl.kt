package com.example.data.repository

import android.content.Context
import android.util.Log
import coil.annotation.ExperimentalCoilApi
import com.example.data.local.manga.MangaDao
import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
import com.example.data.model.PageEntity
import com.example.data.utils.NetworkUtils.isNetworkAvailable
import com.example.domain.model.MangaDomain
import com.example.domain.model.MangaDomainModel
import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper
import com.example.domain.repository.MangaRepository

class MangaRepositoryImpl(
    private val networkService: NetworkService,
    private val dao: MangaDao,
    private val context: Context
) : MangaRepository {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetchMangaData(
        page: String,
        genres: String,
        nsfw: String,
        type: String
    ): ResultWrapper<MangaDomainModel> {
        return try {
            if (isNetworkAvailable(context)) {
                when (val response = networkService.fetchMangaData(page, genres, nsfw, type)) {
                    is ResultWrapper.Success -> {
                        val domainList = response.value.data
                        val entityList = domainList.map { it.toEntity() }

                        if (page == "1") {
                            dao.clearAll()
                        }

                        dao.insertManga(entityList)
                        dao.insertPage(PageEntity(lastFetchedPage = page.toInt()))
                        ResultWrapper.Success(
                            MangaDomainModel(
                                code = response.value.code,
                                data = domainList
                            )
                        )
                    }

                    is ResultWrapper.Failure -> {
                        Log.e("MangaRepo", "API failure: ${response.exception.message}")
                        response
                    }
                }
            } else {
                var cached = emptyList<MangaDomain>()
                val lastPage = fetchLastPage() ?: 1
                if(page == lastPage.toString()) {
                    cached = dao.getAllManga().map { it.toDomain() }
                }
                ResultWrapper.Success(MangaDomainModel(200, cached))
            }
        } catch (e: Exception) {
            Log.e("MangaRepo", "Unexpected error: ${e.message}")
            ResultWrapper.Failure(e)
        }
    }

    override suspend fun fetchLastPage(): Int? {
        if(isNetworkAvailable(context))return 1
        return dao.getLastFetchedPage()
    }
}

