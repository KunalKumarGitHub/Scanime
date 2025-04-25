package com.example.scanime.navigation

import com.example.scanime.model.UiMangaModel
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Serializable
object HomeScreen

@Serializable
object FaceScreen

@Serializable
data class MangaDetails(val manga: UiMangaModel)