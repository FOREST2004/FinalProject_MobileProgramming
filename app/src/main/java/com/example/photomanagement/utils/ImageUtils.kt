package com.example.photomanagement.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

object ImageUtils {

    private const val TAG = "ImageUtils"

    /**
     * Loads a bitmap from a URI
     */
    suspend fun loadBitmapFromUri(context: Context, uri: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val imageUri = Uri.parse(uri)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options().apply {
                // Đọc thông tin kích thước trước
                inJustDecodeBounds = true
            }

            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            Log.d(TAG, "Original image size: ${options.outWidth}x${options.outHeight}")

            // Đọc bitmap thực tế
            val newInputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(newInputStream)

            if (bitmap != null) {
                Log.d(TAG, "Loaded bitmap size: ${bitmap.width}x${bitmap.height}")
            } else {
                Log.e(TAG, "Failed to load bitmap")
            }

            return@withContext bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Rotates a bitmap by the specified degrees
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Flips a bitmap horizontally or vertically
     */
    fun flipBitmap(bitmap: Bitmap, horizontal: Boolean): Bitmap {
        val matrix = Matrix().apply {
            if (horizontal) {
                postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            } else {
                postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Cắt ảnh với kích thước container được cung cấp
     * Đây là phương thức chính được sử dụng để cắt ảnh
     */
    fun cropBitmapSimple(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int, containerWidth: Int, containerHeight: Int): Bitmap {
        try {
            Log.d(TAG, "Simple crop - Bitmap size: ${bitmap.width}x${bitmap.height}")
            Log.d(TAG, "Simple crop - Container size: ${containerWidth}x${containerHeight}")
            Log.d(TAG, "Simple crop - Crop params: x=$x, y=$y, width=$width, height=$height")

            // Tính toán kích thước thực tế của ảnh trong container
            val bitmapAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val containerAspectRatio = containerWidth.toFloat() / containerHeight.toFloat()

            var imageWidth: Float
            var imageHeight: Float
            var imageX: Float
            var imageY: Float

            if (bitmapAspectRatio > containerAspectRatio) {
                // Ảnh lấp đầy chiều rộng, có dải đen trên/dưới
                imageWidth = containerWidth.toFloat()
                imageHeight = imageWidth / bitmapAspectRatio
                imageX = 0f
                imageY = (containerHeight - imageHeight) / 2f
            } else {
                // Ảnh lấp đầy chiều cao, có dải đen trái/phải
                imageHeight = containerHeight.toFloat()
                imageWidth = imageHeight * bitmapAspectRatio
                imageX = (containerWidth - imageWidth) / 2f
                imageY = 0f
            }

            Log.d(TAG, "Image display rect: x=$imageX, y=$imageY, width=$imageWidth, height=$imageHeight")

            // Chuyển đổi tọa độ vùng chọn sang tọa độ trong ảnh thực tế
            val relativeX = (x - imageX) / imageWidth
            val relativeY = (y - imageY) / imageHeight
            val relativeRight = ((x + width) - imageX) / imageWidth
            val relativeBottom = ((y + height) - imageY) / imageHeight

            // Chuyển đổi tọa độ tương đối thành pixel trên bitmap
            val cropX = (relativeX * bitmap.width).toInt()
            val cropY = (relativeY * bitmap.height).toInt()
            val cropRight = (relativeRight * bitmap.width).toInt()
            val cropBottom = (relativeBottom * bitmap.height).toInt()

            // Tính toán kích thước cắt
            val cropWidth = (cropRight - cropX)
            val cropHeight = (cropBottom - cropY)

            // Đảm bảo tọa độ nằm trong bitmap
            val safeX = cropX.coerceIn(0, bitmap.width - 1)
            val safeY = cropY.coerceIn(0, bitmap.height - 1)
            val safeWidth = cropWidth.coerceIn(1, bitmap.width - safeX)
            val safeHeight = cropHeight.coerceIn(1, bitmap.height - safeY)

            Log.d(TAG, "Final crop rect: x=$safeX, y=$safeY, width=$safeWidth, height=$safeHeight")

            // Cắt bitmap
            return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
        } catch (e: Exception) {
            Log.e(TAG, "Error in simple crop: ${e.message}", e)
            return bitmap
        }
    }

    /**
     * Phương thức cắt ảnh dựa trên tỷ lệ trực tiếp
     * Phương thức này sử dụng khi không có thông tin về container
     */
    fun cropBitmapDirect(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        try {
            Log.d(TAG, "Direct crop - Bitmap size: ${bitmap.width}x${bitmap.height}")
            Log.d(TAG, "Direct crop - Crop params: x=$x, y=$y, width=$width, height=$height")

            // Kích thước khung vẽ (ước tính nếu không biết kích thước thực tế)
            val displayWidth = bitmap.width.toFloat()
            val displayHeight = bitmap.height.toFloat()

            // Tính toán tỷ lệ dựa trên kích thước hiển thị
            val cropX = (x / displayWidth * bitmap.width).toInt()
            val cropY = (y / displayHeight * bitmap.height).toInt()
            val cropWidth = (width / displayWidth * bitmap.width).toInt()
            val cropHeight = (height / displayHeight * bitmap.height).toInt()

            // Đảm bảo giới hạn nằm trong bitmap
            val safeX = cropX.coerceIn(0, bitmap.width - 1)
            val safeY = cropY.coerceIn(0, bitmap.height - 1)
            val safeWidth = cropWidth.coerceIn(1, bitmap.width - safeX)
            val safeHeight = cropHeight.coerceIn(1, bitmap.height - safeY)

            Log.d(TAG, "Final crop rect: x=$safeX, y=$safeY, width=$safeWidth, height=$safeHeight")

            // Cắt bitmap
            return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
        } catch (e: Exception) {
            Log.e(TAG, "Error in direct crop: ${e.message}", e)
            return bitmap
        }
    }

    /**
     * Cắt ảnh với tọa độ tuyệt đối (sử dụng khi biết chính xác tọa độ pixel)
     */
    fun cropBitmapAbsolute(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        try {
            Log.d(TAG, "Absolute crop - Bitmap size: ${bitmap.width}x${bitmap.height}")
            Log.d(TAG, "Absolute crop - Crop params: x=$x, y=$y, width=$width, height=$height")

            // Đảm bảo tọa độ nằm trong bitmap
            val safeX = x.coerceIn(0, bitmap.width - 1)
            val safeY = y.coerceIn(0, bitmap.height - 1)
            val safeWidth = width.coerceIn(1, bitmap.width - safeX)
            val safeHeight = height.coerceIn(1, bitmap.height - safeY)

            // Cắt bitmap
            return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
        } catch (e: Exception) {
            Log.e(TAG, "Error in absolute crop: ${e.message}", e)
            return bitmap
        }
    }

    /**
     * Saves a bitmap to the device and returns its URI
     */
    suspend fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        val filename = "edit_${UUID.randomUUID()}.jpg"
        var outputStream: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, use MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val resolver = context.contentResolver
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                imageUri?.let {
                    outputStream = resolver.openOutputStream(it)
                }
            } else {
                // For older versions, use local app storage + FileProvider
                val imagesDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PhotoManagement")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                val image = File(imagesDir, filename)
                outputStream = FileOutputStream(image)

                // Get content URI via FileProvider
                imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    image
                )
            }

            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
            }

            Log.d(TAG, "Image saved successfully: $imageUri")
            return@withContext imageUri
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap: ${e.message}")
            e.printStackTrace()
            return@withContext null
        } finally {
            outputStream?.close()
        }
    }
}