package com.example.scanime.model

import android.os.Parcelable
import com.example.domain.model.MangaDomain
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UiMangaModel(
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
): Parcelable{
    companion object {
        fun fromMangaDomain(manga: MangaDomain) = UiMangaModel(
            authors = manga.authors,
            create_at = manga.create_at,
            genres = manga.genres,
            id = manga.id,
            nsfw = manga.nsfw,
            status = manga.status,
            sub_title = manga.sub_title,
            summary = manga.summary,
            thumb = manga.thumb,
            title = manga.title,
            total_chapter = manga.total_chapter,
            type = manga.type,
            update_at = manga.update_at,
        )
    }
}