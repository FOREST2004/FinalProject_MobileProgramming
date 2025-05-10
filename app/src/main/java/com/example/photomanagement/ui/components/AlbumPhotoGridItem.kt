package com.example.photomanagement.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.utils.SimplifiedShareUtils

@Composable
fun AlbumPhotoGridItem(
    photo: Photo,
    onPhotoClick: (Photo) -> Unit,
    onRemovePhoto: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onPhotoClick(photo) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Ảnh
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Row để chứa các nút thao tác
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                // Nút chia sẻ
                IconButton(
                    onClick = {
                        // Chia sẻ ảnh trực tiếp
                        val imageUri = Uri.parse(photo.uri)
                        SimplifiedShareUtils.shareImage(
                            context = context,
                            imageUri = imageUri,
                            title = photo.title,
                            text = photo.description
                        )
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ ảnh",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(18.dp)
                        )
                    }
                }

                // Nút Xóa ở góc phải
                IconButton(
                    onClick = { onRemovePhoto(photo) },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa khỏi album",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(18.dp)
                        )
                    }
                }
            }
        }
    }
}