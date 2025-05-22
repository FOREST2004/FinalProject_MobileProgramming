package com.example.photomanagement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.photomanagement.data.preferences.AppPreferences
import com.example.photomanagement.ui.viewmodel.AlbumViewModel
import com.example.photomanagement.ui.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    photoViewModel: PhotoViewModel,
    albumViewModel: AlbumViewModel,
    modifier: Modifier = Modifier
) {
    // Lấy trạng thái hiện tại từ AppPreferences
    var darkMode by remember { mutableStateOf(appPreferences.isDarkMode) }

    // Biến để kiểm soát dialog xác nhận xóa
    var showDeletePhotosDialog by remember { mutableStateOf(false) }
    var showDeleteAlbumsDialog by remember { mutableStateOf(false) }
    var showDeleteAllDataDialog by remember { mutableStateOf(false) }

    // Định nghĩa màu đỏ đậm cho các nút Delete
    val deleteRed = Color(0xFFB71C1C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Appearance
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Mode")
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { isChecked ->
                                darkMode = isChecked
                                // Lưu giá trị vào SharedPreferences
                                appPreferences.isDarkMode = isChecked
                            }
                        )
                    }
                }
            }

            // Card Data Management - Đã cập nhật UI
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Data Management",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Xóa tất cả ảnh - Cập nhật UI theo mẫu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Delete all photos")
                            Text(
                                "Remove all photos from the application",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Nút Delete với UI mới
                        OutlinedButton(
                            onClick = { showDeletePhotosDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = deleteRed
                            ),
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = deleteRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "DELETE",
                                color = deleteRed
                            )
                        }
                    }

                    // Xóa tất cả album - Cập nhật UI theo mẫu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Delete all albums")
                            Text(
                                "Remove all albums (photos will remain)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Nút Delete với UI mới
                        OutlinedButton(
                            onClick = { showDeleteAlbumsDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = deleteRed
                            ),
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = deleteRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "DELETE",
                                color = deleteRed
                            )
                        }
                    }



                    // Xóa tất cả dữ liệu - Cập nhật UI theo mẫu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Delete all data",
                                color = deleteRed,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Remove all photos and albums.",
                                style = MaterialTheme.typography.bodySmall,
                                color = deleteRed
                            )
                        }

                        // Nút Delete All - chỉnh sửa theo UI mới
                        Button(
                            onClick = { showDeleteAllDataDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = deleteRed,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .height(72.dp)  // Chiều cao lớn hơn
                                .width(80.dp),  // Chiều rộng nhỏ hơn
                            shape = RoundedCornerShape(36.dp)  // Bo tròn nhiều hơn
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // ĐÃ XÓA: Card App Info
        }

        // Dialog xác nhận xóa tất cả ảnh - đã cập nhật
        if (showDeletePhotosDialog) {
            AlertDialog(
                onDismissRequest = { showDeletePhotosDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Delete all photos?") },
                text = {
                    Text("Are you sure you want to delete all photos? This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            photoViewModel.deleteAllPhotos()
                            showDeletePhotosDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = deleteRed
                        )
                    ) {
                        Text("Confirm Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeletePhotosDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog xác nhận xóa tất cả album - đã cập nhật
        if (showDeleteAlbumsDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAlbumsDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Delete all albums?") },
                text = {
                    Text("Are you sure you want to delete all albums? Photos will remain.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            albumViewModel.deleteAllAlbums()
                            showDeleteAlbumsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = deleteRed
                        )
                    ) {
                        Text("Confirm Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteAlbumsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog xác nhận xóa tất cả dữ liệu - Thay "XÓA" bằng "DELETE"
        if (showDeleteAllDataDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDataDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = deleteRed
                    )
                },
                title = {
                    Text(
                        "Delete all data?",
                        color = deleteRed
                    )
                },
                text = {
                    Column {
                        Text(
                            "WARNING: You are about to delete all data in the app!",
                            color = deleteRed,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All photos and albums will be permanently deleted. This action CANNOT be undone.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Type \"DELETE\" to confirm:")

                        var confirmText by remember { mutableStateOf("") }

                        // Sử dụng OutlinedTextField từ Material 3
                        OutlinedTextField(
                            value = confirmText,
                            onValueChange = { confirmText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            isError = true
                        )

                        // Hiển thị nút xác nhận chỉ khi đã nhập đúng "DELETE"
                        if (confirmText == "DELETE") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    albumViewModel.deleteAllAlbums()
                                    photoViewModel.deleteAllPhotos()
                                    showDeleteAllDataDialog = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = deleteRed
                                )
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DELETE ALL DATA")
                            }
                        }
                    }
                },
                confirmButton = {
                    // Nút xác nhận đã được chuyển vào phần text khi nhập đúng "DELETE"
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteAllDataDialog = false },
                        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}