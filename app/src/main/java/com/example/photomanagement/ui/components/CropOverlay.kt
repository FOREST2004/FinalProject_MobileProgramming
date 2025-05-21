package com.example.photomanagement.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.min

private const val TAG = "CropOverlay"

@Composable
fun CropOverlay(
    onCropChanged: (x: Int, y: Int, width: Int, height: Int, containerWidth: Int, containerHeight: Int) -> Unit,
    imageWidth: Int = 0,  // Kích thước thực của ảnh
    imageHeight: Int = 0, // Kích thước thực của ảnh
    modifier: Modifier = Modifier
) {
    // Size of the container
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Current crop rectangle
    var cropRect by remember { mutableStateOf<Rect?>(null) }

    // Initialize crop rectangle when container size changes
    LaunchedEffect(containerSize) {
        if (containerSize.width > 0 && containerSize.height > 0) {
            // Initialize crop rect to 80% of container
            val initialSize = min(containerSize.width, containerSize.height) * 0.8f
            val x = (containerSize.width - initialSize) / 2
            val y = (containerSize.height - initialSize) / 2

            cropRect = Rect(
                left = x,
                top = y,
                right = x + initialSize,
                bottom = y + initialSize
            )

            // Notify initial crop dimensions with container size
            cropRect?.let { rect ->
                Log.d(TAG, "Initial crop: left=${rect.left}, top=${rect.top}, width=${rect.width}, height=${rect.height}")
                onCropChanged(
                    rect.left.toInt(),
                    rect.top.toInt(),
                    rect.width.toInt(),
                    rect.height.toInt(),
                    containerSize.width,
                    containerSize.height
                )
            }
        }
    }

    // Edge handle states
    var activeHandle by remember { mutableStateOf(Handle.NONE) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                containerSize = size
                Log.d(TAG, "Container size changed: $size")
                Log.d(TAG, "Image size: ${imageWidth}x${imageHeight}")
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Determine which handle (if any) was touched
                        cropRect?.let { rect ->
                            val handleSize = 48f // Touch area for handles

                            activeHandle = when {
                                isNear(offset, Offset(rect.left, rect.top), handleSize) -> Handle.TOP_LEFT
                                isNear(offset, Offset(rect.right, rect.top), handleSize) -> Handle.TOP_RIGHT
                                isNear(offset, Offset(rect.left, rect.bottom), handleSize) -> Handle.BOTTOM_LEFT
                                isNear(offset, Offset(rect.right, rect.bottom), handleSize) -> Handle.BOTTOM_RIGHT
                                isNear(offset, Offset((rect.left + rect.right) / 2, rect.top), handleSize) -> Handle.TOP
                                isNear(offset, Offset((rect.left + rect.right) / 2, rect.bottom), handleSize) -> Handle.BOTTOM
                                isNear(offset, Offset(rect.left, (rect.top + rect.bottom) / 2), handleSize) -> Handle.LEFT
                                isNear(offset, Offset(rect.right, (rect.top + rect.bottom) / 2), handleSize) -> Handle.RIGHT
                                rect.contains(offset) -> Handle.CENTER
                                else -> Handle.NONE
                            }

                            Log.d(TAG, "Drag start at $offset, handle: $activeHandle")
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        cropRect?.let { rect ->
                            // Create a new rectangle based on the drag direction and active handle
                            val newRect = when (activeHandle) {
                                Handle.TOP_LEFT -> Rect(
                                    left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - MIN_CROP_SIZE),
                                    top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - MIN_CROP_SIZE),
                                    right = rect.right,
                                    bottom = rect.bottom
                                )
                                Handle.TOP_RIGHT -> Rect(
                                    left = rect.left,
                                    top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - MIN_CROP_SIZE),
                                    right = (rect.right + dragAmount.x).coerceIn(rect.left + MIN_CROP_SIZE, containerSize.width.toFloat()),
                                    bottom = rect.bottom
                                )
                                Handle.BOTTOM_LEFT -> Rect(
                                    left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - MIN_CROP_SIZE),
                                    top = rect.top,
                                    right = rect.right,
                                    bottom = (rect.bottom + dragAmount.y).coerceIn(rect.top + MIN_CROP_SIZE, containerSize.height.toFloat())
                                )
                                Handle.BOTTOM_RIGHT -> Rect(
                                    left = rect.left,
                                    top = rect.top,
                                    right = (rect.right + dragAmount.x).coerceIn(rect.left + MIN_CROP_SIZE, containerSize.width.toFloat()),
                                    bottom = (rect.bottom + dragAmount.y).coerceIn(rect.top + MIN_CROP_SIZE, containerSize.height.toFloat())
                                )
                                Handle.TOP -> Rect(
                                    left = rect.left,
                                    top = (rect.top + dragAmount.y).coerceIn(0f, rect.bottom - MIN_CROP_SIZE),
                                    right = rect.right,
                                    bottom = rect.bottom
                                )
                                Handle.BOTTOM -> Rect(
                                    left = rect.left,
                                    top = rect.top,
                                    right = rect.right,
                                    bottom = (rect.bottom + dragAmount.y).coerceIn(rect.top + MIN_CROP_SIZE, containerSize.height.toFloat())
                                )
                                Handle.LEFT -> Rect(
                                    left = (rect.left + dragAmount.x).coerceIn(0f, rect.right - MIN_CROP_SIZE),
                                    top = rect.top,
                                    right = rect.right,
                                    bottom = rect.bottom
                                )
                                Handle.RIGHT -> Rect(
                                    left = rect.left,
                                    top = rect.top,
                                    right = (rect.right + dragAmount.x).coerceIn(rect.left + MIN_CROP_SIZE, containerSize.width.toFloat()),
                                    bottom = rect.bottom
                                )
                                Handle.CENTER -> {
                                    // Move the entire rectangle
                                    var newLeft = rect.left + dragAmount.x
                                    var newTop = rect.top + dragAmount.y

                                    // Make sure rectangle stays within bounds
                                    if (newLeft < 0) newLeft = 0f
                                    if (newTop < 0) newTop = 0f
                                    if (newLeft + rect.width > containerSize.width) {
                                        newLeft = containerSize.width - rect.width
                                    }
                                    if (newTop + rect.height > containerSize.height) {
                                        newTop = containerSize.height - rect.height
                                    }

                                    Rect(
                                        left = newLeft,
                                        top = newTop,
                                        right = newLeft + rect.width,
                                        bottom = newTop + rect.height
                                    )
                                }
                                Handle.NONE -> rect
                            }

                            cropRect = newRect

                            // Notify updated crop dimensions with container size
                            onCropChanged(
                                newRect.left.toInt(),
                                newRect.top.toInt(),
                                newRect.width.toInt(),
                                newRect.height.toInt(),
                                containerSize.width,
                                containerSize.height
                            )

                            Log.d(TAG, "Crop changed: left=${newRect.left}, top=${newRect.top}, width=${newRect.width}, height=${newRect.height}")
                        }
                    },
                    onDragEnd = {
                        activeHandle = Handle.NONE
                        Log.d(TAG, "Drag ended")
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            cropRect?.let { rect ->
                // Draw semi-transparent overlay outside the crop rectangle
                val path = androidx.compose.ui.graphics.Path().apply {
                    // Outer rectangle (full canvas)
                    addRect(Rect(Offset.Zero, Size(size.width, size.height)))
                    // Inner rectangle (crop area) - will be cut out
                    addRect(rect)
                }
                drawPath(
                    path = path,
                    color = Color.Black.copy(alpha = 0.5f),
                    style = Stroke(width = size.width)
                )

                // Draw crop rectangle outline
                drawRect(
                    color = Color.White,
                    topLeft = Offset(rect.left, rect.top),
                    size = Size(rect.width, rect.height),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )

                // Draw corner handles
                val handleRadius = 8f
                val handleColor = Color.White

                // Top left handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.left, rect.top)
                )

                // Top right handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.right, rect.top)
                )

                // Bottom left handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.left, rect.bottom)
                )

                // Bottom right handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.right, rect.bottom)
                )

                // Middle top handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset((rect.left + rect.right) / 2, rect.top)
                )

                // Middle bottom handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset((rect.left + rect.right) / 2, rect.bottom)
                )

                // Middle left handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.left, (rect.top + rect.bottom) / 2)
                )

                // Middle right handle
                drawCircle(
                    color = handleColor,
                    radius = handleRadius,
                    center = Offset(rect.right, (rect.top + rect.bottom) / 2)
                )
            }
        }
    }
}

// Minimum crop size in pixels
private const val MIN_CROP_SIZE = 100f

// Check if an offset is near a point
private fun isNear(offset: Offset, point: Offset, threshold: Float): Boolean {
    return (offset.x - point.x).let { it * it } +
            (offset.y - point.y).let { it * it } < threshold * threshold
}

// Represent different handles of the crop rectangle
private enum class Handle {
    NONE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER
}