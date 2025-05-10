package com.example.photomanagement.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.utils.SimplifiedShareUtils
import androidx.compose.material.icons.outlined.FavoriteBorder

@Composable
fun PhotoGridItem(
    photo: Photo,
    onPhotoClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onPhotoClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.title,
                modifier = Modifier
                    .fillMaxSize()
            )

            // Row để chứa các nút thao tác (yêu thích và chia sẻ)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                // Nút chia sẻ
                IconButton(
                    onClick = {
                        // Chia sẻ ảnh trực tiếp từ lưới
                        val imageUri = Uri.parse(photo.uri)
                        SimplifiedShareUtils.shareImage(
                            context = context,
                            imageUri = imageUri,
                            title = photo.title,
                            text = photo.description
                        )
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Chia sẻ ảnh",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Nút yêu thích
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (photo.isFavorite) "Bỏ yêu thích" else "Yêu thích",
                        tint = if (photo.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}