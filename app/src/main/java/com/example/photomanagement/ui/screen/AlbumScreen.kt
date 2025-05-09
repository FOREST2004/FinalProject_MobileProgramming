package com.example.photomanagement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photomanagement.data.model.Album
import com.example.photomanagement.ui.components.AlbumItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    albums: List<Album>,
    onCreateAlbum: () -> Unit,
    onAlbumClick: (Album) -> Unit,
    onDeleteAlbum: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf<Album?>(null) }

    // Debug: In số lượng album khi AlbumScreen được render
    LaunchedEffect(albums) {
        println("AlbumScreen composed with ${albums.size} albums")
        albums.forEach { album ->
            println("Album in screen: ${album.id}, ${album.name}")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Albums",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (albums.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No albums yet. Create one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Debug: In số lượng album
                println("Rendering album list: ${albums.size} albums")

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(albums) { album ->
                        // Debug: In thông tin album
                        println("Rendering album item: ${album.id}, ${album.name}")

                        AlbumItem(
                            album = album,
                            onAlbumClick = onAlbumClick,
                            onDeleteClick = {
                                showDeleteConfirmDialog = album
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button - đặt trong Box để đảm bảo nó luôn ở trên cùng
        FloatingActionButton(
            onClick = onCreateAlbum,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp) // Kích thước tiêu chuẩn của FAB
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Album",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Dialog xác nhận xóa album
        showDeleteConfirmDialog?.let { album ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                title = { Text("Delete Album") },
                text = { Text("Are you sure you want to delete \"${album.name}\"? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteAlbum(album)
                            showDeleteConfirmDialog = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmDialog = null }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}