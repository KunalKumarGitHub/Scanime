package com.example.data.remote

import com.example.data.model.MangaResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MangaApiService {

    @Headers(
        "x-rapidapi-host: mangaverse-api.p.rapidapi.com",
        "x-rapidapi-key: b1ceb7a406mshb9d6176abc3ba95p10cd1djsnbb210ca37bf5"
    )
    @GET("manga/fetch")
    suspend fun fetchMangaData(
        @Query("page") page: String,
        @Query("genres") genres: String,
        @Query("nsfw") nsfw: String,
        @Query("type") type: String
    ): MangaResponse
}