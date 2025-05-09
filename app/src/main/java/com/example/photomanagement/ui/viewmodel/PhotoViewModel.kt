package com.example.photomanagement.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.model.EditOperation
import com.example.photomanagement.data.repository.PhotoRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoViewModel(private val repository: PhotoRepository) : ViewModel() {
    val photos: StateFlow<List<Photo>> = repository.photos
    val favoritePhotos: StateFlow<List<Photo>> = repository.favoritePhotos

    fun addPhotoFromUri(uri: Uri) {
        viewModelScope.launch {
            // Generate a simple filename based on URI
            val fileName = "photo_${System.currentTimeMillis()}"
            repository.addPhoto(uri.toString(), fileName)
        }
    }

    fun addPhoto(uri: String, title: String, description: String? = null) {
        viewModelScope.launch {
            repository.addPhoto(uri, title, description)
        }
    }

    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
        }
    }

    fun toggleFavorite(photoId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(photoId)
        }
    }

    fun addTag(photoId: String, tag: String) {
        viewModelScope.launch {
            repository.addTagToPhoto(photoId, tag)
        }
    }

    fun searchPhotos(query: String): List<Photo> {
        return repository.searchPhotos(query)
    }

    fun editPhoto(photoId: String, operations: List<EditOperation>) {
        viewModelScope.launch {
            repository.editPhoto(photoId, operations)
        }
    }

    fun getPhotosByIds(photoIds: List<String>): List<Photo> {
        return photos.value.filter { photoIds.contains(it.id) }
    }

    fun saveEditedPhoto(photo: Photo, newUri: String, operations: List<EditOperation>): String {
        var newPhotoId = ""
        viewModelScope.launch {
            // Lưu ảnh đã chỉnh sửa
            newPhotoId = repository.saveEditedPhoto(photo.id, newUri, operations)
        }
        return newPhotoId
    }

    fun getPhotoById(photoId: String): Photo? {
        return photos.value.find { it.id == photoId }
    }
}

class PhotoViewModelFactory(private val repository: PhotoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}