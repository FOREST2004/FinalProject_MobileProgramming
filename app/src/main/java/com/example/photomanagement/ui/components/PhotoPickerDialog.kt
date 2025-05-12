package com.example.photomanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photomanagement.data.model.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerDialog(
    availablePhotos: List<Photo>,
    onPhotosSelected: (List<Photo>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPhotos by remember { mutableStateOf<List<Photo>>(emptyList()) }

    // Debug log
    LaunchedEffect(selectedPhotos) {
        println("PhotoPickerDialog: Đã chọn ${selectedPhotos.size} ảnh: ${selectedPhotos.map { it.id }}")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn ảnh") },
        text = {
            Column {
                Text(
                    text = "Chọn ảnh để thêm vào album (đã chọn: ${selectedPhotos.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(availablePhotos) { photo ->
                        val isSelected = selectedPhotos.any { it.id == photo.id }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .toggleable(
                                    value = isSelected,
                                    onValueChange = {
                                        selectedPhotos = if (isSelected) {
                                            selectedPhotos.filter { it.id != photo.id }
                                        } else {
                                            selectedPhotos + photo
                                        }
                                    }
                                )
                        ) {
                            // Hiển thị ảnh
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay và icon nếu đã chọn
                            if (isSelected) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.Black.copy(alpha = 0.4f)
                                ) {}

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Debug log
                    println("PhotoPickerDialog: Xác nhận ${selectedPhotos.size} ảnh: ${selectedPhotos.map { it.id }}")
                    onPhotosSelected(selectedPhotos)
                }
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}