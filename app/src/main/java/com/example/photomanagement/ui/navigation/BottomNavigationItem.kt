package com.example.photomanagement.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector
)

val bottomNavigationItems = listOf(
    BottomNavigationItem("Gallery", Icons.Default.Photo),
    BottomNavigationItem("Albums", Icons.Default.PhotoAlbum),
    BottomNavigationItem("Settings", Icons.Default.Settings)
)