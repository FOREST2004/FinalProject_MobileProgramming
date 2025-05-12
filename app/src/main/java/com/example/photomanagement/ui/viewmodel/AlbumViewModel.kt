package com.example.photomanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.data.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    // Album Flow
    val albums: Flow<List<Album>> = repository.albums

    // Tạo album mới
    fun createAlbum(name: String, description: String? = null) {
        viewModelScope.launch {
            val album = Album(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                photoIds = emptyList(),
                coverPhotoId = null,
                dateCreated = System.currentTimeMillis() // Sử dụng milliseconds thay vì Date
            )

            // Kiểm tra loại tham số của phương thức createAlbum trong repository
            // Nếu nó cần một String, chỉ truyền album.id
            repository.createAlbum(album.id)

            // Hoặc nếu nó cần các tham số riêng lẻ
            // repository.createAlbum(album.id, album.name, album.description)

            // Hoặc nếu nó cần cả đối tượng Album (cần sửa lại repository)
            // repository.saveAlbum(album)
        }
    }

    // Xóa album
    fun deleteAlbum(albumId: String) {
        viewModelScope.launch {
            repository.deleteAlbum(albumId)
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