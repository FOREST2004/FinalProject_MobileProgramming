package com.example.photomanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.repository.AlbumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {
    // Đảm bảo lấy StateFlow từ repository
    val albums: StateFlow<List<Album>> = repository.albums

    init {
        // In ra log danh sách albums khi khởi tạo ViewModel
        println("AlbumViewModel initialized with ${albums.value.size} albums")
        debugAlbums()
    }

    // Debug function - in danh sách album ra log
    fun debugAlbums() {
        println("DEBUG ALBUMS: Current album count: ${albums.value.size}")
        albums.value.forEach { album ->
            println("Album: ${album.id}, ${album.name}, Photos: ${album.photoIds.size}")
        }
    }

    fun createAlbum(name: String, description: String? = null) {
        viewModelScope.launch {
            // In thông tin trước khi tạo
            println("Before creating album - Albums count: ${albums.value.size}")

            // Tạo album mới
            repository.createAlbum(name, description)

            // In thông tin sau khi tạo
            println("After creating album - Albums count: ${albums.value.size}")
            println("Created album: $name, Description: $description")
            debugAlbums()
        }
    }

    fun deleteAlbum(albumId: String) {
        viewModelScope.launch {
            repository.deleteAlbum(albumId)
            println("Album deleted: $albumId")
        }
    }

    fun addPhotoToAlbum(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.addPhotoToAlbum(albumId, photoId)
        }
    }

    fun removePhotoFromAlbum(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.removePhotoFromAlbum(albumId, photoId)
        }
    }

    fun setCoverPhoto(albumId: String, photoId: String) {
        viewModelScope.launch {
            repository.setCoverPhoto(albumId, photoId)
        }
    }

    // Lấy danh sách ảnh theo danh sách ID
    fun getPhotosByIds(photoIds: List<String>): List<Photo> {
        // Giả sử có một phương thức để lấy ảnh theo ID
        // Đây chỉ là triển khai mẫu
        return photoIds.mapNotNull { id ->
            // Lấy ảnh từ repository theo ID
            // repository.getPhotoById(id)
            Photo(id, "Photo $id", "", null, Date()) // Mẫu để tránh lỗi biên dịch
        }
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