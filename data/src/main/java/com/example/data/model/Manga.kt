package com.example.data.model

import com.example.domain.model.MangaDomain


data class Manga(
    val authors: List<String>,
    val create_at: Long,
    val genres: List<String>,
    val id: String,
    val nsfw: Boolean,
    val status: String,
    val sub_title: String,
    val summary: String,
    val thumb: String,
    val title: String,
    val total_chapter: Int,
    val type: String,
    val update_at: Long
){
    fun toDomainModel() = MangaDomain(
        authors = authors,
        create_at = create_at,
        genres = genres,
        id = id,
        nsfw = nsfw,
        status = status,
        sub_title = sub_title,
        summary = summary,
        thumb = thumb,
        title = title,
        total_chapter = total_chapter,
        type = type,
        update_at = update_at
    )
}