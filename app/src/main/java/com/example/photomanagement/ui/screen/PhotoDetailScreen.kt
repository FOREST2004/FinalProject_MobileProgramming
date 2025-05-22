package com.example.photomanagement.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.utils.ImageUtils
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

    // Thêm state để track favorite status locally
    var isFavorite by remember(photo.id) { mutableStateOf(photo.isFavorite) }

    // Kiểm tra URI hợp lệ
    var isUriValid by remember { mutableStateOf(true) }

    // Cập nhật isFavorite khi photo thay đổi
    LaunchedEffect(photo.isFavorite) {
        isFavorite = photo.isFavorite
    }

    // Kiểm tra URI khi màn hình được hiển thị
    LaunchedEffect(photo.uri) {
        isUriValid = ImageUtils.isUriValid(context, photo.uri)
        Log.d("PhotoDetailScreen", "URI hợp lệ: $isUriValid cho ${photo.uri}")
    }

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
                    // Nút chia sẻ
                    IconButton(
                        onClick = {
                            if (isUriValid) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/*"
                                    putExtra(Intent.EXTRA_STREAM, Uri.parse(photo.uri))
                                    putExtra(Intent.EXTRA_SUBJECT, photo.title)
                                    photo.description?.let { desc ->
                                        if (desc.isNotEmpty()) {
                                            putExtra(Intent.EXTRA_TEXT, desc)
                                        }
                                    }
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"))
                            } else {
                                Log.e("PhotoDetailScreen", "Không thể chia sẻ - URI không hợp lệ")
                            }
                        },
                        enabled = isUriValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ ảnh"
                        )
                    }

                    // Nút yêu thích - Cập nhật logic
                    IconButton(
                        onClick = {
                            // Cập nhật state local ngay lập tức để UI responsive
                            isFavorite = !isFavorite
                            // Gọi callback để cập nhật database
                            onToggleFavorite(photo)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Bỏ yêu thích" else "Yêu thích",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Nút chỉnh sửa
                    IconButton(
                        onClick = { onEditClick(photo) },
                        enabled = isUriValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa ảnh"
                        )
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
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Sử dụng SubcomposeAsyncImage thay vì AsyncImage để xử lý lỗi tốt hơn
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        // Hiển thị icon lỗi và thông báo
                        isUriValid = false
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = "Lỗi hình ảnh",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Không thể tải hình ảnh",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
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
                            text = "Thêm vào: ${dateFormat.format(Date(photo.dateAdded))}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Trạng thái ảnh
                    if (!isUriValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Có vấn đề với hình ảnh này",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}