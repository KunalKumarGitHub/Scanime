package com.example.data.mappers

import com.example.data.model.MangaEntity
import com.example.domain.model.MangaDomain

fun MangaEntity.toDomain(): MangaDomain = MangaDomain(
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

fun MangaDomain.toEntity(): MangaEntity = MangaEntity(
    id = id,
    authors = authors,
    create_at = create_at,
    genres = genres,
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
