package com.example.photomanagement.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photomanagement.data.model.EditOperation
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.repository.PhotoRepository
import com.example.photomanagement.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * ViewModel cập nhật - sử dụng context từ repository
 */
class PhotoViewModel(
    private val repository: PhotoRepository,
    private val context: Context
) : ViewModel() {

    // Danh sách ảnh
    val photos: Flow<List<Photo>> = repository.photos
    val favoritePhotos: Flow<List<Photo>> = repository.favoritePhotos

    // Thêm ảnh mới
    fun addPhoto(uri: String, title: String, description: String? = null) {
        viewModelScope.launch {
            repository.addPhoto(uri, title, description)
        }
    }

    // Thêm ảnh từ URI - PHƯƠNG THỨC CẢI TIẾN
    fun addPhotoFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                // Sao chép ảnh vào bộ nhớ nội bộ của ứng dụng
                val internalUri = ImageUtils.copyImageFromUri(context, uri)

                if (internalUri != null) {
                    // Tạo tên file từ thời gian hiện tại
                    val fileName = "photo_${System.currentTimeMillis()}"

                    // Lưu thông tin ảnh vào database sử dụng URI mới
                    repository.addPhoto(internalUri.toString(), fileName)

                    Log.d("PhotoViewModel", "Đã thêm ảnh mới với URI: ${internalUri}")
                } else {
                    Log.e("PhotoViewModel", "Không thể sao chép ảnh từ URI: $uri")
                }
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Lỗi khi thêm ảnh: ${e.message}", e)
            }
        }
    }

    // Kiểm tra URI hợp lệ
    fun validatePhotoUris() {
        viewModelScope.launch {
            try {
                // Lấy tất cả ảnh từ repository
                val allPhotos = repository.getAllPhotosSync()

                Log.d("PhotoViewModel", "Kiểm tra ${allPhotos.size} ảnh...")

                for (photo in allPhotos) {
                    // Kiểm tra từng URI
                    val isValid = ImageUtils.isUriValid(context, photo.uri)
                    if (!isValid) {
                        Log.w("PhotoViewModel", "Phát hiện URI không hợp lệ: ${photo.uri} cho photo ${photo.id}")
                        // Tùy chọn: có thể đánh dấu ảnh là không hợp lệ
                    }
                }
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Lỗi khi xác thực URI: ${e.message}")
            }
        }
    }

    // Xóa ảnh
    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
        }
    }

    // Đánh dấu ảnh yêu thích
    fun toggleFavorite(photoId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(photoId)
        }
    }

    // Lấy ảnh theo ID
    fun getPhotoById(photoId: String): Photo? {
        // Tạo một mutable state để lưu kết quả
        val resultHolder = MutableStateFlow<Photo?>(null)

        viewModelScope.launch {
            try {
                val photos = repository.getPhotosByIds(listOf(photoId))
                if (photos.isNotEmpty()) {
                    resultHolder.value = photos[0]
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }

        // Trả về null vì đây là phương thức đồng bộ, kết quả sẽ được cập nhật bất đồng bộ
        return null
    }

    // Lấy danh sách ảnh theo danh sách ID
    fun getPhotosByIds(photoIds: List<String>): List<Photo> {
        val result = mutableListOf<Photo>()

        viewModelScope.launch {
            try {
                val photos = repository.getPhotosByIds(photoIds)
                result.addAll(photos)
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }

        return result
    }

    // Lưu ảnh đã chỉnh sửa - CẬP NHẬT
    fun saveEditedPhoto(photo: Photo, newUri: String?, operations: List<EditOperation>): String {
        var resultId = photo.id

        viewModelScope.launch {
            try {
                if (newUri != null) {
                    // Lưu ảnh mới vào database với URI mới
                    repository.updatePhotoUri(photo.id, newUri)
                    Log.d("PhotoViewModel", "Đã cập nhật URI ảnh: $newUri")
                }
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Lỗi khi lưu ảnh đã chỉnh sửa: ${e.message}")
            }
        }

        return resultId
    }

    // Phương thức bất đồng bộ để lấy ảnh theo ID
    suspend fun getPhotoByIdAsync(photoId: String): Photo? {
        return withContext(Dispatchers.IO) {
            val photos = repository.getPhotosByIds(listOf(photoId))
            if (photos.isNotEmpty()) photos[0] else null
        }
    }

    // Phương thức bất đồng bộ để lấy danh sách ảnh theo danh sách ID
    suspend fun getPhotosByIdsAsync(photoIds: List<String>): List<Photo> {
        return withContext(Dispatchers.IO) {
            repository.getPhotosByIds(photoIds)
        }
    }
}

/**
 * Factory cho ViewModel cập nhật
 */
class PhotoViewModelFactory(
    private val repository: PhotoRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}