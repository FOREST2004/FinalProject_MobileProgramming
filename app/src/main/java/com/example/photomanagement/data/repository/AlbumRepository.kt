package com.example.photomanagement.data.repository

import com.example.photomanagement.data.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class AlbumRepository {
    // Khởi tạo với danh sách rỗng
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    init {
        // In ra log khi khởi tạo repository
        println("AlbumRepository initialized")
        // Bỏ dòng này đi để không tạo album test
        // addTestAlbum()
    }

    // Thêm album vào danh sách
    fun createAlbum(name: String, description: String? = null) {
        val newAlbum = Album(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            photoIds = emptyList(),
            coverPhotoId = null
        )

        // Sử dụng phép gán trực tiếp thay vì toMutableList và add
        _albums.value = _albums.value + newAlbum

        // Debug
        println("AlbumRepository: Created album: $name, id: ${newAlbum.id}, current count: ${_albums.value.size}")
        println("All albums: ${_albums.value.map { "${it.id}: ${it.name}" }}")
    }

    // Các phương thức khác
    fun deleteAlbum(albumId: String) {
        _albums.value = _albums.value.filter { it.id != albumId }
        println("AlbumRepository: Deleted album: $albumId, remaining: ${_albums.value.size}")
    }

    fun addPhotoToAlbum(albumId: String, photoId: String) {
        _albums.value = _albums.value.map { album ->
            if (album.id == albumId) {
                val updatedPhotoIds = album.photoIds + photoId
                album.copy(photoIds = updatedPhotoIds.distinct())
            } else album
        }
    }

    fun removePhotoFromAlbum(albumId: String, photoId: String) {
        _albums.value = _albums.value.map { album ->
            if (album.id == albumId) {
                album.copy(photoIds = album.photoIds.filter { it != photoId })
            } else album
        }
    }

    fun setCoverPhoto(albumId: String, photoId: String) {
        _albums.value = _albums.value.map { album ->
            if (album.id == albumId) {
                album.copy(coverPhotoId = photoId)
            } else album
        }
    }

    // Lấy album theo ID
    fun getAlbum(albumId: String): Album? {
        return _albums.value.find { it.id == albumId }
    }

    // Giữ phương thức này nhưng không gọi nó nữa
    // Trong trường hợp bạn muốn sử dụng nó trong tương lai
    private fun addTestAlbum() {
        val testAlbum = Album(
            id = "test-album-id",
            name = "Test Album",
            description = "This is a test album",
            photoIds = emptyList(),
            coverPhotoId = null
        )
        _albums.value = _albums.value + testAlbum
        println("Test album added: ${testAlbum.name}")
    }
}