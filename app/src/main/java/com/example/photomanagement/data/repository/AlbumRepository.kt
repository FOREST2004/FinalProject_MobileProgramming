package com.example.photomanagement.data.repository

import android.content.Context
import android.util.Log
import com.example.photomanagement.data.db.AppDatabase
import com.example.photomanagement.data.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class AlbumRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val albumDao = database.albumDao()

    // Flow quan sát danh sách album
    val albums: Flow<List<Album>> = albumDao.getAllAlbums()

    // Tạo album mới
    suspend fun createAlbum(name: String, description: String? = null): String {
        return withContext(Dispatchers.IO) {
            try {
                val albumId = UUID.randomUUID().toString()
                val newAlbum = Album(
                    id = albumId,
                    name = name,
                    description = description,
                    coverPhotoId = null,
                    dateCreated = System.currentTimeMillis(),
                    photoIds = emptyList()
                )

                albumDao.insertAlbum(newAlbum)
                Log.d("AlbumRepository", "Đã tạo album mới: $name với ID: $albumId")
                return@withContext albumId
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi tạo album: ${e.message}")
                throw e
            }
        }
    }

    // Lấy album theo ID
    suspend fun getAlbumById(albumId: String): Album? {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext albumDao.getAlbumById(albumId)
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi lấy album: ${e.message}")
                return@withContext null
            }
        }
    }

    // Xóa album
    suspend fun deleteAlbum(albumId: String) {
        withContext(Dispatchers.IO) {
            try {
                albumDao.deleteAlbumById(albumId)
                Log.d("AlbumRepository", "Đã xóa album: $albumId")
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi xóa album: ${e.message}")
            }
        }
    }

    // Thêm ảnh vào album
    suspend fun addPhotoToAlbum(albumId: String, photoId: String) {
        withContext(Dispatchers.IO) {
            try {
                val album = albumDao.getAlbumById(albumId) ?: return@withContext

                // Kiểm tra nếu ảnh đã tồn tại trong album
                if (!album.photoIds.contains(photoId)) {
                    val updatedPhotoIds = album.photoIds + photoId
                    val updatedAlbum = album.copy(photoIds = updatedPhotoIds)

                    // Cập nhật ảnh bìa nếu chưa có
                    val finalAlbum = if (album.coverPhotoId == null) {
                        updatedAlbum.copy(coverPhotoId = photoId)
                    } else {
                        updatedAlbum
                    }

                    albumDao.updateAlbum(finalAlbum)
                    Log.d("AlbumRepository", "Đã thêm ảnh $photoId vào album $albumId")
                }
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi thêm ảnh vào album: ${e.message}")
            }
        }
    }

    // Xóa ảnh khỏi album
    suspend fun removePhotoFromAlbum(albumId: String, photoId: String) {
        withContext(Dispatchers.IO) {
            try {
                val album = albumDao.getAlbumById(albumId) ?: return@withContext

                // Xóa ảnh khỏi danh sách
                val updatedPhotoIds = album.photoIds.filter { it != photoId }

                // Cập nhật ảnh bìa nếu cần thiết
                val newCoverPhotoId = if (album.coverPhotoId == photoId) {
                    updatedPhotoIds.firstOrNull()
                } else {
                    album.coverPhotoId
                }

                val updatedAlbum = album.copy(
                    photoIds = updatedPhotoIds,
                    coverPhotoId = newCoverPhotoId
                )

                albumDao.updateAlbum(updatedAlbum)
                Log.d("AlbumRepository", "Đã xóa ảnh $photoId khỏi album $albumId")
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi xóa ảnh khỏi album: ${e.message}")
            }
        }
    }

    // Thay đổi ảnh bìa của album
    suspend fun setCoverPhoto(albumId: String, photoId: String) {
        withContext(Dispatchers.IO) {
            try {
                val album = albumDao.getAlbumById(albumId) ?: return@withContext

                // Chỉ cập nhật nếu ảnh tồn tại trong album
                if (album.photoIds.contains(photoId)) {
                    val updatedAlbum = album.copy(coverPhotoId = photoId)
                    albumDao.updateAlbum(updatedAlbum)
                    Log.d("AlbumRepository", "Đã đặt ảnh bìa mới cho album $albumId")
                }
            } catch (e: Exception) {
                Log.e("AlbumRepository", "Lỗi khi thay đổi ảnh bìa: ${e.message}")
            }
        }
    }
}