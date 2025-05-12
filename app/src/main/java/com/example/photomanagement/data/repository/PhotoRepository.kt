package com.example.photomanagement.data.repository

import android.content.Context
import android.util.Log
import com.example.photomanagement.data.db.AppDatabase
import com.example.photomanagement.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Repository cực kỳ đơn giản để quản lý ảnh
 */
class PhotoRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val photoDao = database.photoDao()

    // Flow quan sát ảnh
    val photos: Flow<List<Photo>> = photoDao.getAllPhotos()
    val favoritePhotos: Flow<List<Photo>> = photoDao.getFavoritePhotos()

    // Thêm ảnh mới
    suspend fun addPhoto(uri: String, title: String, description: String? = null) {
        withContext(Dispatchers.IO) {
            try {
                val photo = Photo(
                    id = UUID.randomUUID().toString(),
                    uri = uri,
                    title = title,
                    description = description,
                    dateAdded = System.currentTimeMillis(),
                    isFavorite = false,
                    tags = ""
                )
                photoDao.insertPhoto(photo)
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error adding photo: ${e.message}")
            }
        }
    }

    // Xóa ảnh
    suspend fun deletePhoto(photoId: String) {
        withContext(Dispatchers.IO) {
            try {
                photoDao.deletePhotoById(photoId)
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error deleting photo: ${e.message}")
            }
        }
    }

    // Đánh dấu ảnh yêu thích
    suspend fun toggleFavorite(photoId: String) {
        withContext(Dispatchers.IO) {
            try {
                val photos = photoDao.getPhotosByIds(listOf(photoId))
                if (photos.isNotEmpty()) {
                    val photo = photos[0]
                    val updatedPhoto = photo.copy(isFavorite = !photo.isFavorite)
                    photoDao.updatePhoto(updatedPhoto)
                }
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error toggling favorite: ${e.message}")
            }
        }
    }

    // Lấy ảnh theo danh sách ID
    suspend fun getPhotosByIds(photoIds: List<String>): List<Photo> {
        return withContext(Dispatchers.IO) {
            try {
                photoDao.getPhotosByIds(photoIds)
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error getting photos by ids: ${e.message}")
                emptyList()
            }
        }
    }

    // Lấy ảnh theo ID
    suspend fun getPhotoById(photoId: String): Photo? {
        return withContext(Dispatchers.IO) {
            try {
                photoDao.getPhotoById(photoId)
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error getting photo by id: ${e.message}")
                null
            }
        }
    }

    // Cập nhật URI của ảnh
    suspend fun updatePhotoUri(photoId: String, newUri: String) {
        withContext(Dispatchers.IO) {
            try {
                photoDao.updatePhotoUri(photoId, newUri)
                Log.d("PhotoRepository", "Updated URI for photo $photoId to $newUri")
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error updating photo URI: ${e.message}")
            }
        }
    }

    // Lấy tất cả ảnh (không phải flow)
    suspend fun getAllPhotosSync(): List<Photo> {
        return withContext(Dispatchers.IO) {
            try {
                photoDao.getAllPhotosSync()
            } catch (e: Exception) {
                Log.e("PhotoRepository", "Error getting all photos sync: ${e.message}")
                emptyList()
            }
        }
    }
}