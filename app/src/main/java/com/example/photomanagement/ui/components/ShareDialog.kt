package com.example.photomanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.photomanagement.R
import com.example.photomanagement.data.model.Photo
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share

/**
 * Hộp thoại hiển thị các tùy chọn chia sẻ cho một ảnh
 */
@Composable
fun ShareDialog(
    photo: Photo,
    onDismiss: () -> Unit,
    onShareGeneral: () -> Unit,
    onShareFacebook: () -> Unit,
    onShareInstagram: () -> Unit,
    onShareTwitter: () -> Unit,
    onShareMessaging: () -> Unit,
    onShareEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề và nút đóng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chia sẻ ảnh",
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Lưới tùy chọn chia sẻ
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hàng đầu tiên
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ShareOption(
                            icon = Icons.Default.Share,
                            label = "Tổng quát",
                            onClick = onShareGeneral
                        )

                        ShareOption(
                            icon = Icons.Default.Facebook,
                            label = "Facebook",
                            onClick = onShareFacebook
                        )

                        ShareOption(
                            iconResId = R.drawable.ic_instagram,
                            label = "Instagram",
                            onClick = onShareInstagram
                        )
                    }

                    // Hàng thứ hai
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ShareOption(
                            iconResId = R.drawable.ic_twitter,
                            label = "Twitter",
                            onClick = onShareTwitter
                        )

                        ShareOption(
                            icon = Icons.Default.Message,
                            label = "Tin nhắn",
                            onClick = onShareMessaging
                        )

                        ShareOption(
                            iconResId = R.drawable.ic_email,
                            label = "Email",
                            onClick = onShareEmail
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item tùy chọn chia sẻ riêng lẻ với biểu tượng vector
 */
@Composable
fun ShareOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Item tùy chọn chia sẻ riêng lẻ với biểu tượng từ resource drawable
 */
@Composable
fun ShareOption(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}