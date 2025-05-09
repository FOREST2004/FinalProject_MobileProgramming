// app/src/main/java/com/example/photomanagement/ui/components/PhotoPickerDialog.kt
package com.example.photomanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photomanagement.data.model.Photo

@Composable
fun PhotoPickerDialog(
    availablePhotos: List<Photo>,
    onPhotosSelected: (List<Photo>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPhotos by remember { mutableStateOf<Set<String>>(emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Photos") },
        text = {
            Column {
                Text(
                    text = "Choose photos to add to the album",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (availablePhotos.isEmpty()) {
                    Text(
                        text = "No photos available",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(availablePhotos) { photo ->
                            PhotoPickerItem(
                                photo = photo,
                                isSelected = selectedPhotos.contains(photo.id),
                                onSelectionChange = { isSelected ->
                                    selectedPhotos = if (isSelected) {
                                        selectedPhotos + photo.id
                                    } else {
                                        selectedPhotos - photo.id
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selected = availablePhotos.filter { selectedPhotos.contains(it.id) }
                    onPhotosSelected(selected)
                },
                enabled = selectedPhotos.isNotEmpty()
            ) {
                Text("Add (${selectedPhotos.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PhotoPickerItem(
    photo: Photo,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onSelectionChange(!isSelected) },
        shape = MaterialTheme.shapes.small,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = if (isSelected) 2.dp else 1.dp,
            brush = ButtonDefaults.outlinedButtonBorder.brush
        )
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.title,
            modifier = Modifier.fillMaxSize()
        )
    }
}