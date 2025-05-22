package com.example.photomanagement.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.preferences.AppPreferences
import com.example.photomanagement.ui.viewmodel.AlbumViewModel
import com.example.photomanagement.ui.viewmodel.PhotoViewModel
import com.example.photomanagement.ui.screen.*
import com.example.photomanagement.ui.components.AddPhotoDialog
import com.example.photomanagement.ui.components.CreateAlbumDialog
import com.example.photomanagement.ui.components.PhotoPickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoManagementApp(
    photoViewModel: PhotoViewModel,
    albumViewModel: AlbumViewModel,
    appPreferences: AppPreferences, // Thêm tham số appPreferences
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(0) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var showCreateAlbumDialog by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var showPhotoPickerDialog by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var isEditingPhoto by remember { mutableStateOf(false) }

    // Counter để buộc refresh UI khi có thay đổi
    var refreshCounter by remember { mutableStateOf(0) }

    // Mutable state để lưu ảnh trong album hiện tại
    var currentAlbumPhotos by remember { mutableStateOf<List<Photo>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // Sửa các dòng này để thêm giá trị initial
    val photos by photoViewModel.photos.collectAsState(initial = emptyList())
    val favoritePhotos by photoViewModel.favoritePhotos.collectAsState(initial = emptyList())
    val albums by albumViewModel.albums.collectAsState(initial = emptyList())

    // Debug: Kiểm tra khi albums thay đổi
    LaunchedEffect(albums) {
        println("PhotoManagementApp: Albums changed, current count: ${albums.size}")
        albums.forEach { album ->
            println("Album in UI: ${album.id}, ${album.name}")
        }
    }

    LaunchedEffect(Unit) {
        // Xác thực tất cả URI ảnh khi ứng dụng khởi động
        photoViewModel.validatePhotoUris()
    }

    // Debug: Kiểm tra khi refreshCounter thay đổi
    LaunchedEffect(refreshCounter) {
        println("PhotoManagementApp: refreshCounter changed to $refreshCounter")
    }

    // Cập nhật danh sách ảnh khi album thay đổi hoặc khi counter thay đổi
    LaunchedEffect(selectedAlbum, refreshCounter, albums) {
        if (selectedAlbum != null) {
            // Tìm album trong danh sách albums mới nhất (có thể đã được cập nhật)
            val updatedAlbum = albums.find { it.id == selectedAlbum!!.id }
            if (updatedAlbum != null) {
                // Cập nhật danh sách ảnh dựa trên album mới nhất
                currentAlbumPhotos = photoViewModel.getPhotosByIdsAsync(updatedAlbum.photoIds)
                selectedAlbum = updatedAlbum
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (selectedAlbum == null && selectedPhoto == null) {
                NavigationBar {
                    bottomNavigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Photo detail screen
        if (selectedPhoto != null && !isEditingPhoto) {
            PhotoDetailScreen(
                photo = selectedPhoto!!,
                onBackClick = { selectedPhoto = null },
                onEditClick = { photo ->
                    isEditingPhoto = true
                },
                onDeleteClick = { photo ->
                    photoViewModel.deletePhoto(photo.id)
                    selectedPhoto = null
                },
                onToggleFavorite = { photo ->
                    photoViewModel.toggleFavorite(photo.id)
                    // Cập nhật lại đối tượng Photo hiện tại nếu đang xem detail
                    scope.launch {
                        val updatedPhoto = photoViewModel.getPhotoByIdAsync(photo.id)
                        if (updatedPhoto != null && selectedPhoto?.id == photo.id) {
                            selectedPhoto = updatedPhoto
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
// Photo edit screen
        else if (selectedPhoto != null && isEditingPhoto) {
            PhotoEditScreen(
                photo = selectedPhoto!!,
                onSaveEdit = { photo, newUri, operations ->
                    // Lưu ảnh đã chỉnh sửa
                    val newPhotoId = photoViewModel.saveEditedPhoto(photo, newUri, operations)

                    scope.launch {
                        // Đợi một chút để đảm bảo file đã được lưu
                        delay(500)

                        // Lấy photo đã cập nhật từ database
                        val updatedPhoto = photoViewModel.getPhotoByIdAsync(photo.id)
                        if (updatedPhoto != null) {
                            selectedPhoto = updatedPhoto

                        }

                        // Thoát khỏi chế độ chỉnh sửa
                        isEditingPhoto = false
                    }
                },
                onCancel = {
                    isEditingPhoto = false
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
        else if (selectedAlbum != null) {
            AlbumDetailScreen(
                album = selectedAlbum!!,
                albumPhotos = currentAlbumPhotos,
                onBackClick = { selectedAlbum = null },
                onAddPhotoClick = { showPhotoPickerDialog = true },
                onPhotoClick = { photo ->
                    selectedPhoto = photo
                },
                onFavoriteToggle = { photo -> photoViewModel.toggleFavorite(photo.id) },
                onRemovePhoto = { photo ->
                    // Xóa ảnh khỏi album
                    albumViewModel.removePhotoFromAlbum(selectedAlbum!!.id, photo.id)

                    // Cập nhật UI ngay lập tức
                    scope.launch {
                        delay(100) // Đợi một chút để repository cập nhật

                        // Cập nhật danh sách ảnh bằng cách loại bỏ ảnh đã xóa
                        currentAlbumPhotos = currentAlbumPhotos.filter { it.id != photo.id }

                        // Buộc refresh UI
                        refreshCounter++
                    }
                },
                onRefresh = {
                    // Tăng counter để buộc LaunchedEffect chạy lại
                    refreshCounter++
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            when (selectedItem) {
                0 -> GalleryScreen(
                    photos = photos,
                    favoritePhotos = favoritePhotos,
                    onAddPhotoClick = { showAddPhotoDialog = true },
                    onPhotoClick = { photo ->
                        selectedPhoto = photo
                    },
                    onFavoriteToggle = { photo -> photoViewModel.toggleFavorite(photo.id) },
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> AlbumScreen(
                    albums = albums,
                    onCreateAlbum = {
                        println("Opening CreateAlbumDialog")
                        showCreateAlbumDialog = true
                    },
                    onAlbumClick = { album ->
                        selectedAlbum = album
                        // Khởi tạo danh sách ảnh khi đầu tiên chọn album
                        scope.launch {
                            currentAlbumPhotos = photoViewModel.getPhotosByIdsAsync(album.photoIds)
                        }
                    },
                    onDeleteAlbum = { album ->
                        // Xóa album trong repository
                        albumViewModel.deleteAlbum(album.id)

                        // Nếu album bị xóa đang được chọn, quay về màn hình album
                        if (selectedAlbum?.id == album.id) {
                            selectedAlbum = null
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                2 -> SettingsScreen(
                    appPreferences = appPreferences,
                    photoViewModel = photoViewModel,  // Thêm photoViewModel
                    albumViewModel = albumViewModel,  // Thêm albumViewModel
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        // Show Add Photo Dialog
        if (showAddPhotoDialog) {
            AddPhotoDialog(
                onDismiss = { showAddPhotoDialog = false },
                onPhotoSelected = { uri ->
                    photoViewModel.addPhotoFromUri(uri)
                    showAddPhotoDialog = false
                }
            )
        }

        // Show Create Album Dialog
        if (showCreateAlbumDialog) {
            CreateAlbumDialog(
                onCreateAlbum = { name, description ->
                    // In log để debug
                    println("Creating album: $name, $description")

                    // In danh sách album hiện tại trước khi tạo
                    println("Albums before creation: ${albums.size}")

                    // Tạo album
                    albumViewModel.createAlbum(name, description)

                    // Làm mới UI ngay lập tức
                    refreshCounter++

                    // In danh sách album hiện tại
                    println("Albums right after creation: ${albums.size}")

                    // Thêm một lần làm mới UI sau một khoảng thời gian
                    scope.launch {
                        delay(200)
                        println("Force refreshing UI after delay")
                        refreshCounter++

                        // Kiểm tra lại sau khi delay
                        delay(300)
                        println("Albums after delay: ${albums.size}")
                        albums.forEach { album ->
                            println("Album after delay: ${album.id}, ${album.name}")
                        }
                    }

                    // Đóng dialog
                    showCreateAlbumDialog = false
                },
                onDismiss = { showCreateAlbumDialog = false }
            )
        }

        // Xử lý khi người dùng chọn ảnh trong PhotoPickerDialog
        if (showPhotoPickerDialog && selectedAlbum != null) {
            PhotoPickerDialog(
                availablePhotos = photos,
                onPhotosSelected = { selectedPhotos ->
                    // Khi người dùng chọn xong ảnh trong dialog
                    scope.launch {
                        // DEBUG LOG
                        println("PhotoManagementApp: Đã chọn ${selectedPhotos.size} ảnh: ${selectedPhotos.map { it.id }}")

                        // Sử dụng phương thức mới để thêm nhiều ảnh cùng lúc
                        albumViewModel.addPhotosToAlbum(selectedAlbum!!.id, selectedPhotos)

                        // Đóng dialog
                        showPhotoPickerDialog = false

                        // QUAN TRỌNG: Đợi một chút để đảm bảo repository đã cập nhật
                        delay(200)

                        // Buộc refresh UI bằng cách tăng counter
                        refreshCounter++

                        // Log cho debug
                        println("PhotoManagementApp: Đã thêm ảnh vào album và làm mới UI, counter = $refreshCounter")
                    }
                },
                onDismiss = { showPhotoPickerDialog = false }
            )
        }
    }
}