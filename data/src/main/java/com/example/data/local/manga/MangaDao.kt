package com.example.data.local.manga

import androidx.room.*
import com.example.data.model.MangaEntity
import com.example.data.model.PageEntity

@Dao
interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(mangaList: List<MangaEntity>)

    @Query("SELECT * FROM manga_table")
    suspend fun getAllManga(): List<MangaEntity>

    @Query("SELECT lastFetchedPage FROM fetched_pages WHERE id = 1")
    suspend fun getLastFetchedPage(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity)

    @Query("DELETE FROM manga_table")
    suspend fun clearAll()
}
