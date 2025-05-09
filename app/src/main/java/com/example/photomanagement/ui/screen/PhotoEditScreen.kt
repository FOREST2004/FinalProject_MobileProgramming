package com.example.photomanagement.ui.screen

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.photomanagement.data.model.EditOperation
import com.example.photomanagement.data.model.Photo
import com.example.photomanagement.ui.components.CropOverlay
import kotlinx.coroutines.launch
import com.example.photomanagement.utils.ImageUtils

private const val TAG = "PhotoEditScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditScreen(
    photo: Photo,
    onSaveEdit: (Photo, String, List<EditOperation>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Kích thước view hiển thị
    var imageContainerSize by remember { mutableStateOf(IntSize.Zero) }

    // Current bitmap state that will be modified during edits
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // List of operations applied to the image
    var operations by remember { mutableStateOf<List<EditOperation>>(emptyList()) }

    // Current edit mode
    var currentEditMode by remember { mutableStateOf(EditMode.NONE) }

    // Crop related state
    var cropRect by remember { mutableStateOf<EditOperation.Crop?>(null) }

    // Load image on first composition
    LaunchedEffect(photo.uri) {
        val bitmap = ImageUtils.loadBitmapFromUri(context, photo.uri)
        currentBitmap = bitmap
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Photo") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            currentBitmap?.let { bitmap ->
                                scope.launch {
                                    val newUri = ImageUtils.saveBitmapToUri(context, bitmap)
                                    newUri?.let {
                                        onSaveEdit(photo, it.toString(), operations)
                                    }
                                }
                            }
                        },
                        enabled = currentBitmap != null
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (currentEditMode == EditMode.CROP) {
                        // Khi đang ở chế độ cắt, hiển thị hai nút: Apply và Cancel
                        IconButton(
                            onClick = {
                                // Áp dụng vùng cắt khi nhấn nút Apply
                                cropRect?.let { crop ->
                                    scope.launch {
                                        currentBitmap?.let { bitmap ->
                                            Log.d(TAG, "Applying crop with container size: ${imageContainerSize.width}x${imageContainerSize.height}")
                                            Log.d(TAG, "Crop rect: x=${crop.x}, y=${crop.y}, width=${crop.width}, height=${crop.height}")

                                            // Sử dụng phương thức cropBitmapSimple với kích thước container
                                            val cropped = ImageUtils.cropBitmapSimple(
                                                bitmap,
                                                crop.x,
                                                crop.y,
                                                crop.width,
                                                crop.height,
                                                imageContainerSize.width,
                                                imageContainerSize.height
                                            )

                                            Log.d(TAG, "Cropped bitmap size: ${cropped.width}x${cropped.height}")
                                            currentBitmap = cropped
                                            operations = operations + crop
                                            cropRect = null
                                            currentEditMode = EditMode.NONE
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Apply Crop",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                // Hủy thao tác cắt
                                currentEditMode = EditMode.NONE
                                cropRect = null
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Crop",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        // Rotate button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    currentBitmap?.let { bitmap ->
                                        val rotated = ImageUtils.rotateBitmap(bitmap, 90f)
                                        currentBitmap = rotated
                                        operations = operations + EditOperation.Rotate(90f)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Rotate90DegreesCw, contentDescription = "Rotate")
                        }

                        // Flip horizontal button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    currentBitmap?.let { bitmap ->
                                        val flipped = ImageUtils.flipBitmap(bitmap, horizontal = true)
                                        currentBitmap = flipped
                                        operations = operations + EditOperation.FlipHorizontal
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.FlipToFront, contentDescription = "Flip Horizontal")
                        }

                        // Flip vertical button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    currentBitmap?.let { bitmap ->
                                        val flipped = ImageUtils.flipBitmap(bitmap, horizontal = false)
                                        currentBitmap = flipped
                                        operations = operations + EditOperation.FlipVertical
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.FlipToBack, contentDescription = "Flip Vertical")
                        }

                        // Crop button
                        IconButton(
                            onClick = {
                                currentEditMode = EditMode.CROP
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Crop,
                                contentDescription = "Crop",
                                tint = if (currentEditMode == EditMode.CROP)
                                    MaterialTheme.colorScheme.primary
                                else
                                    LocalContentColor.current
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            currentBitmap?.let { bitmap ->
                // Display the current edited image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp) // Thêm padding nhỏ để tránh trường hợp ảnh bị cắt sát cạnh
                        .onSizeChanged { size ->
                            if (size.width > 0 && size.height > 0) {
                                imageContainerSize = size
                                Log.d(TAG, "Image container size changed: ${size.width}x${size.height}")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Edited Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // ContentScale.Fit đảm bảo toàn bộ ảnh được hiển thị
                    )
                }

                // Show crop overlay if in crop mode
                if (currentEditMode == EditMode.CROP) {
                    CropOverlay(
                        modifier = Modifier.fillMaxSize(),
                        onCropChanged = { x, y, width, height ->
                            Log.d(TAG, "Crop changed: x=$x, y=$y, width=$width, height=$height")
                            cropRect = EditOperation.Crop(x, y, width, height)
                        }
                    )
                }
            } ?: run {
                // Show original image if bitmap not loaded yet
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp)
                        .onSizeChanged { size ->
                            if (size.width > 0 && size.height > 0) {
                                imageContainerSize = size
                                Log.d(TAG, "Image container size changed: ${size.width}x${size.height}")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photo.uri)
                            .size(Size.ORIGINAL)
                            .build(),
                        contentDescription = photo.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

enum class EditMode {
    NONE,
    CROP
}