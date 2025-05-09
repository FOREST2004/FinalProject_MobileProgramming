package com.example.photomanagement.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.ui.components.PhotoGrid
import com.example.photomanagement.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    photos: List<Photo>,
    favoritePhotos: List<Photo>,
    onAddPhotoClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onFavoriteToggle: (Photo) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyFavorites by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Gallery") },
                actions = {
                    FilterChip(
                        selected = showOnlyFavorites,
                        onClick = { showOnlyFavorites = !showOnlyFavorites },
                        label = { Text("Favorites") }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(onSearch = onSearch)

            // Nút "Add Photo" luôn hiển thị ở đây
            Button(
                onClick = onAddPhotoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Photo")
            }

            val displayPhotos = if (showOnlyFavorites) favoritePhotos else photos

            if (displayPhotos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = if (showOnlyFavorites) "No favorite photos" else "No photos yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                PhotoGrid(
                    photos = displayPhotos,
                    onPhotoClick = onPhotoClick,
                    onFavoriteClick = onFavoriteToggle
                )
            }
        }
    }
}