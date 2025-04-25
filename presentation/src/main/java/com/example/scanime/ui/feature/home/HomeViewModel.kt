package com.example.scanime.ui.feature.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.MangaDomain
import com.example.domain.network.ResultWrapper
import com.example.domain.usecase.GetLastPageUseCase
import com.example.domain.usecase.GetMangaUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mangaUseCase: GetMangaUseCase,
    private val getLastPageUseCase: GetLastPageUseCase
) : ViewModel() {

    private val _mangaList = mutableStateListOf<MangaDomain>()
    val mangaList: List<MangaDomain> get() = _mangaList

    private val _isLoading = mutableStateOf<Boolean>(true)
    val isLoading: MutableState<Boolean> get() = _isLoading

    private val fetchedPages = mutableSetOf<String>()
    private var currentPage = 1

    init{
        viewModelScope.launch{
            currentPage = getLastPageUseCase.execute() ?: 1
            Log.d("MangaFetch initial", "$currentPage")
            fetchMangaData(
                genres = "Harem,Fantasy",
                nsfw = "true",
                type = "all"
            )
        }
    }

    fun fetchMangaData(
        genres: String,
        nsfw: String,
        type: String
    ) {
        val page = currentPage.toString()
        _isLoading.value = true

        viewModelScope.launch {
            Log.d("MangaFetch", "Fetching page $page")
            try {
                when (val response = mangaUseCase.execute(page, genres, nsfw, type)) {
                    is ResultWrapper.Success -> {
                        _mangaList.addAll(response.value.data)
                        fetchedPages.add(page)
                        if(response.value.data.isNotEmpty()) {
                            currentPage += 1
                        }
                    }
                    is ResultWrapper.Failure -> {
                        Log.e("MangaFetch", "Failed: ${response.exception.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("MangaFetch", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

