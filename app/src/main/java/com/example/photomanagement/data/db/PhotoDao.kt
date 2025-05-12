package com.example.photomanagement.data.db

import androidx.room.*
import com.example.photomanagement.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * DAO cực kỳ đơn giản, chỉ định nghĩa các phương thức mà Repository gọi
 */
@Dao
interface PhotoDao {
    // Lấy tất cả ảnh
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<Photo>>

    // Lấy ảnh yêu thích
    @Query("SELECT * FROM photos WHERE isFavorite = 1")
    fun getFavoritePhotos(): Flow<List<Photo>>

    // Lấy ảnh theo ID
    @Query("SELECT * FROM photos WHERE id IN (:photoIds)")
    suspend fun getPhotosByIds(photoIds: List<String>): List<Photo>

    // Lấy ảnh theo ID (một ảnh)
    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: String): Photo?

    // Lấy tất cả ảnh (không phải Flow)
    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosSync(): List<Photo>

    // Thêm ảnh mới
    @Insert
    suspend fun insertPhoto(photo: Photo)

    // Cập nhật ảnh
    @Update
    suspend fun updatePhoto(photo: Photo)

    // Cập nhật URI của ảnh
    @Query("UPDATE photos SET uri = :newUri WHERE id = :photoId")
    suspend fun updatePhotoUri(photoId: String, newUri: String)

    // Xóa ảnh theo ID
    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: String)

    // Xóa ảnh
    @Delete
    suspend fun deletePhoto(photo: Photo)

    // Tìm kiếm ảnh
//    @Query("SELECT * FROM photos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
//    suspend fun searchPhotos(query: String): List<Photo>
}