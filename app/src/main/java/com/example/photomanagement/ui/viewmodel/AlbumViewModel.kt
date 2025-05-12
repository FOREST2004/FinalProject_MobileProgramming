package com.example.photomanagement.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    // Flow quan sát danh sách album
    val albums: Flow<List<Album>> = repository.albums

    // Tạo album mới
    fun createAlbum(name: String, description: String? = null) {
        viewModelScope.launch {
            try {
                val albumId = repository.createAlbum(name, description)
                Log.d("AlbumViewModel", "Đã tạo album mới: $name với ID: $albumId")
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "Lỗi khi tạo album: ${e.message}")
            }
        }
    }

    // Xóa album
    fun deleteAlbum(albumId: String) {
        viewModelScope.launch {
            repository.deleteAlbum(albumId)
        }
    }

    // Thêm nhiều ảnh vào album cùng lúc - PHƯƠNG THỨC MỚI
    fun addPhotosToAlbum(albumId: String, photos: List<Photo>) {
        viewModelScope.launch {
            try {
                val photoIds = photos.map { it.id }
                Log.d("AlbumViewModel", "Thêm ${photos.size} ảnh vào album $albumId: $photoIds")
                repository.addPhotosToAlbum(albumId, photoIds)
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "Lỗi khi thêm nhiều ảnh vào album: ${e.message}", e)
            }
        }
    }

    // Thêm ảnh vào album
    fun addPhotoToAlbum(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.addPhotoToAlbum(albumId, photoId)
        }
    }

    // Xóa ảnh khỏi album
    fun removePhotoFromAlbum(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.removePhotoFromAlbum(albumId, photoId)
        }
    }

    // Thiết lập ảnh bìa cho album
    fun setCoverPhoto(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.setCoverPhoto(albumId, photoId)
        }
    }

    // Lấy album theo ID
    suspend fun getAlbumById(albumId: String): Album? {
        return repository.getAlbumById(albumId)
    }
}

class AlbumViewModelFactory(private val repository: AlbumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}