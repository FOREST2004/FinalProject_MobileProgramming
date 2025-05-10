package com.example.photomanagement.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.data.model.EditOperation
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: Photo,
    onBackClick: () -> Unit,
    onEditClick: (Photo) -> Unit,
    onDeleteClick: (Photo) -> Unit,
    onToggleFavorite: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photo.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    // Nút chia sẻ - sử dụng intent chia sẻ hệ thống trực tiếp
                    IconButton(
                        onClick = {
                            // Tạo Intent chia sẻ
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, Uri.parse(photo.uri))

                                // Thêm tiêu đề và mô tả nếu có
                                putExtra(Intent.EXTRA_SUBJECT, photo.title)
                                photo.description?.let { desc ->
                                    if (desc.isNotEmpty()) {
                                        putExtra(Intent.EXTRA_TEXT, desc)
                                    }
                                }

                                // Cấp quyền đọc tạm thời
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            // Hiển thị trực tiếp hộp thoại chia sẻ của hệ thống
                            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ ảnh"
                        )
                    }

                    // Nút yêu thích
                    IconButton(onClick = { onToggleFavorite(photo) }) {
                        Icon(
                            imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (photo.isFavorite) "Bỏ yêu thích" else "Yêu thích",
                            tint = if (photo.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Nút chỉnh sửa
                    IconButton(onClick = { onEditClick(photo) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa ảnh")
                    }

                    // Nút xóa
                    IconButton(onClick = { onDeleteClick(photo) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa ảnh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Hiển thị ảnh
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Thông tin ảnh
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Tiêu đề
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mô tả
                    photo.description?.let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Ngày thêm
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm vào: ${dateFormat.format(photo.dateAdded)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thẻ
                    if (photo.tags.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = photo.tags.joinToString(", "),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}