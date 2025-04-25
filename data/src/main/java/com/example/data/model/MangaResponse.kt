package com.example.data.model

import com.example.domain.model.MangaDomainModel

data class MangaResponse(
    val code: Int,
    val `data`: List<Manga>
){
    fun toDomainModel() = MangaDomainModel(
        code = code,
        `data` = `data`.map{it.toDomainModel()}
    )
}