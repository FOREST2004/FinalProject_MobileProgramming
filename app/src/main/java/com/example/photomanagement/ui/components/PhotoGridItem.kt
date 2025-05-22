package com.example.photomanagement.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Sửa lại cách sử dụng remember - Sử dụng key để reset state khi photo thay đổi
    var isFavorite by remember(photo.id, photo.isFavorite) {
        mutableStateOf(photo.isFavorite)
    }

    // Đồng bộ state khi photo.isFavorite thay đổi từ bên ngoài
    LaunchedEffect(photo.isFavorite) {
        isFavorite = photo.isFavorite
    }

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
                modifier = Modifier.fillMaxSize()
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
                    onClick = {
                        // Cập nhật state local ngay lập tức để UI responsive
                        isFavorite = !isFavorite
                        // Gọi callback để cập nhật database
                        onFavoriteClick()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Bỏ yêu thích" else "Yêu thích",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}