package com.example.photomanagement.data.db

import androidx.room.*
import com.example.photomanagement.data.model.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: String): Album?

    @Insert
    suspend fun insertAlbum(album: Album)

    @Update
    suspend fun updateAlbum(album: Album)

    @Delete
    suspend fun deleteAlbum(album: Album)

    @Query("DELETE FROM albums WHERE id = :albumId")
    suspend fun deleteAlbumById(albumId: String)

    @Query("SELECT * FROM albums WHERE id IN (:albumIds)")
    suspend fun getAlbumsByIds(albumIds: List<String>): List<Album>

    @Query("SELECT * FROM albums")
    suspend fun getAllAlbumsSync(): List<Album>
}