package com.example.photomanagement.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(
    onCreateAlbum: (name: String, description: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var albumName by remember { mutableStateOf("") }
    var albumDescription by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        println("CreateAlbumDialog displayed")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Album") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = albumName,
                    onValueChange = {
                        albumName = it
                        isNameError = it.isBlank()
                    },
                    label = { Text("Album Name") },
                    isError = isNameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                if (isNameError) {
                    Text(
                        text = "Album name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                TextField(
                    value = albumDescription,
                    onValueChange = { albumDescription = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (albumName.isNotBlank()) {
                        println("Dialog: Creating album: $albumName")
                        onCreateAlbum(
                            albumName.trim(),
                            albumDescription.takeIf { it.isNotBlank() }?.trim()
                        )
                    } else {
                        isNameError = true
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}