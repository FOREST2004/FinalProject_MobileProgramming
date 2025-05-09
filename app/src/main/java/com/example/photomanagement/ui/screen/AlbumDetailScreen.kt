package com.example.photomanagement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.ui.components.AlbumPhotoGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    album: Album,
    albumPhotos: List<Photo>,
    onBackClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onFavoriteToggle: (Photo) -> Unit, // Vẫn giữ parameter nhưng không dùng
    onRemovePhoto: (Photo) -> Unit, // Thêm callback xóa ảnh
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showRemoveConfirmDialog by remember { mutableStateOf<Photo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(album.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Album info section
            if (album.description != null) {
                Text(
                    text = album.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${albumPhotos.size} photos in this album",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Add Photo button
            Button(
                onClick = onAddPhotoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Photos")
            }

            // Photo grid - Sử dụng AlbumPhotoGrid với tham số onRemovePhoto
            if (albumPhotos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No photos in this album",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Thêm tham số onRemovePhoto
                AlbumPhotoGrid(
                    photos = albumPhotos,
                    onPhotoClick = onPhotoClick,
                    onRemovePhoto = { photo ->
                        // Hiển thị dialog xác nhận trước khi xóa
                        showRemoveConfirmDialog = photo
                    }
                )
            }
        }

        // Dialog xác nhận xóa ảnh
        showRemoveConfirmDialog?.let { photo ->
            AlertDialog(
                onDismissRequest = { showRemoveConfirmDialog = null },
                title = { Text("Remove Photo") },
                text = { Text("Are you sure you want to remove this photo from the album?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRemovePhoto(photo)
                            showRemoveConfirmDialog = null
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRemoveConfirmDialog = null }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}