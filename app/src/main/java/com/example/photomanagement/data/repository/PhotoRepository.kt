package com.example.photomanagement.data.repository

import android.content.Context
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.model.EditOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class PhotoRepository(private val context: Context) {
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _favoritePhotos = MutableStateFlow<List<Photo>>(emptyList())
    val favoritePhotos: StateFlow<List<Photo>> = _favoritePhotos

    fun addPhoto(uri: String, title: String, description: String? = null) {
        val newPhoto = Photo(
            id = UUID.randomUUID().toString(),
            uri = uri,
            title = title,
            description = description,
            dateAdded = Date()
        )
        _photos.value = _photos.value + newPhoto
    }

    fun deletePhoto(photoId: String) {
        _photos.value = _photos.value.filter { it.id != photoId }
        updateFavorites()
    }

    fun toggleFavorite(photoId: String) {
        _photos.value = _photos.value.map { photo ->
            if (photo.id == photoId) photo.copy(isFavorite = !photo.isFavorite)
            else photo
        }
        updateFavorites()
    }

    fun addTagToPhoto(photoId: String, tag: String) {
        _photos.value = _photos.value.map { photo ->
            if (photo.id == photoId) {
                val updatedTags = photo.tags + tag
                photo.copy(tags = updatedTags.distinct())
            } else photo
        }
    }

    fun searchPhotos(query: String): List<Photo> {
        return _photos.value.filter { photo ->
            photo.title.contains(query, ignoreCase = true) ||
                    photo.description?.contains(query, ignoreCase = true) == true ||
                    photo.tags.any { it.contains(query, ignoreCase = true) }
        }
    }

    fun editPhoto(photoId: String, operations: List<EditOperation>): String {
        // TODO: Implement photo editing logic
        // This will return a new URI for the edited photo
        return UUID.randomUUID().toString()
    }

    private fun updateFavorites() {
        _favoritePhotos.value = _photos.value.filter { it.isFavorite }
    }

    fun saveEditedPhoto(photoId: String, newUri: String, operations: List<EditOperation>): String {
        // Tìm ảnh cũ
        val originalPhoto = _photos.value.find { it.id == photoId }

        // Nếu không tìm thấy ảnh, trả về URI rỗng
        if (originalPhoto == null) return ""

        // Xóa ảnh cũ
        _photos.value = _photos.value.filter { it.id != photoId }

        // Tạo ảnh mới với URI mới và thông tin từ ảnh cũ
        val editedPhoto = Photo(
            id = UUID.randomUUID().toString(),
            uri = newUri,
            title = originalPhoto.title,
            description = originalPhoto.description,
            dateAdded = Date(),
            isFavorite = originalPhoto.isFavorite,
            tags = originalPhoto.tags
        )

        // Thêm ảnh mới vào danh sách
        _photos.value = _photos.value + editedPhoto

        // Cập nhật danh sách ảnh yêu thích
        updateFavorites()

        // Trả về ID của ảnh mới
        return editedPhoto.id
    }
}