package com.example.photomanagement.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.min

object ImageUtils {
    private const val TAG = "ImageUtils"

    /**
     * Lưu ảnh vào bộ nhớ nội bộ của ứng dụng
     */
    suspend fun saveImageToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        filename: String
    ): String = withContext(Dispatchers.IO) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            val directory = File(context.filesDir, "photos")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Tạo file và lưu bitmap
            val file = File(directory, filename)
            FileOutputStream(file).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
            }

            // Trả về đường dẫn đến file
            return@withContext file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image to internal storage: ${e.message}", e)
            throw e
        }
    }

    /**
     * Tạo bản sao của một ảnh từ URI nguồn và lưu vào bộ nhớ nội bộ
     */
    suspend fun copyImageFromUri(
        context: Context,
        sourceUri: Uri
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Bắt đầu sao chép ảnh từ URI: $sourceUri")
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                Log.d(TAG, "Đã đọc bitmap thành công, kích thước: ${bitmap.width}x${bitmap.height}")
                val filename = "photo_${System.currentTimeMillis()}.jpg"
                val path = saveImageToInternalStorage(context, bitmap, filename)
                Log.d(TAG, "Đã lưu bitmap vào đường dẫn nội bộ: $path")
                val uri = getUriFromPath(context, path)
                Log.d(TAG, "Đã tạo URI từ đường dẫn: $uri")
                return@withContext uri
            } else {
                Log.e(TAG, "Không thể đọc bitmap từ URI: $sourceUri")
            }
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi sao chép ảnh: ${e.message}", e)
            return@withContext null
        }
    }

    /**
     * Kiểm tra xem URI có còn hợp lệ không
     */
    fun isUriValid(context: Context, uriString: String): Boolean {
        try {
            if (uriString.isBlank()) {
                Log.e(TAG, "URI trống")
                return false
            }

            val uri = Uri.parse(uriString)

            // Kiểm tra xem URI có phải là file nội bộ không
            if (uriString.startsWith("file://")) {
                val path = uri.path
                return path != null && File(path).exists()
            }
            // Kiểm tra xem URI có phải là content URI không
            else if (uriString.startsWith("content://")) {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                val isValid = cursor != null
                cursor?.close()
                return isValid
            }

            return false
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi kiểm tra tính hợp lệ của URI: ${e.message}")
            return false
        }
    }

    /**
     * Loads a bitmap from a URI
     */
    suspend fun loadBitmapFromUri(context: Context, uri: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val imageUri = Uri.parse(uri)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) {
                Log.e(TAG, "Không thể giải mã bitmap từ URI: $uri")
            }

            return@withContext bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi tải bitmap từ URI $uri: ${e.message}")
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
     * Phương thức cải tiến để cắt ảnh chính xác hơn
     * Tính toán vùng cắt dựa trên kích thước thực tế của bitmap và container
     */
    fun cropBitmapSimple(
        bitmap: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        containerWidth: Int,
        containerHeight: Int
    ): Bitmap {
        Log.d(TAG, "Bitmap thực: ${bitmap.width}x${bitmap.height}, Container: ${containerWidth}x${containerHeight}")
        Log.d(TAG, "Vùng cắt gốc: x=$x, y=$y, width=$width, height=$height")

        // Tính toán tỷ lệ fit thực tế (khi ảnh được scale để vừa container)
        val actualScale = minOf(
            containerWidth.toFloat() / bitmap.width.toFloat(),
            containerHeight.toFloat() / bitmap.height.toFloat()
        )

        // Tính kích thước ảnh sau khi scale
        val scaledImageWidth = bitmap.width * actualScale
        val scaledImageHeight = bitmap.height * actualScale

        // Tính offset nếu ảnh được căn giữa trong container
        val offsetX = (containerWidth - scaledImageWidth) / 2f
        val offsetY = (containerHeight - scaledImageHeight) / 2f

        // Chuyển đổi tọa độ từ container sang tọa độ bitmap gốc
        val actualX = ((x - offsetX) / actualScale).toInt()
        val actualY = ((y - offsetY) / actualScale).toInt()
        val actualWidth = (width / actualScale).toInt()
        val actualHeight = (height / actualScale).toInt()

        Log.d(TAG, "Offset: x=$offsetX, y=$offsetY, Scale: $actualScale")
        Log.d(TAG, "Ảnh scaled: ${scaledImageWidth}x${scaledImageHeight}")
        Log.d(TAG, "Vùng cắt thực: x=$actualX, y=$actualY, width=$actualWidth, height=$actualHeight")

        // Đảm bảo vùng cắt nằm trong bitmap
        val safeX = actualX.coerceIn(0, bitmap.width - 1)
        val safeY = actualY.coerceIn(0, bitmap.height - 1)
        val safeWidth = actualWidth.coerceIn(1, bitmap.width - safeX)
        val safeHeight = actualHeight.coerceIn(1, bitmap.height - safeY)

        // Log thông tin vùng cắt cuối cùng
        Log.d(TAG, "Vùng cắt áp dụng: x=$safeX, y=$safeY, width=$safeWidth, height=$safeHeight")

        return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
    }

    /**
     * Tính toán vùng hiển thị thực tế của ảnh trong container
     */
    fun calculateActualImageBounds(
        containerWidth: Int,
        containerHeight: Int,
        bitmapWidth: Int,
        bitmapHeight: Int
    ): RectF {
        // Tính tỷ lệ để ảnh vừa với container
        val scale = min(
            containerWidth.toFloat() / bitmapWidth.toFloat(),
            containerHeight.toFloat() / bitmapHeight.toFloat()
        )

        // Kích thước ảnh sau khi scale
        val scaledWidth = bitmapWidth * scale
        val scaledHeight = bitmapHeight * scale

        // Vị trí của ảnh trong container (căn giữa)
        val left = (containerWidth - scaledWidth) / 2f
        val top = (containerHeight - scaledHeight) / 2f

        return RectF(left, top, left + scaledWidth, top + scaledHeight)
    }

    /**
     * Phương thức thay thế để cắt ảnh trong trường hợp cách tiếp cận thông thường không hoạt động
     * Sử dụng RectF đã tính toán
     */
    fun cropBitmapFromActualBounds(
        bitmap: Bitmap,
        cropX: Int,
        cropY: Int,
        cropWidth: Int,
        cropHeight: Int,
        containerWidth: Int,
        containerHeight: Int
    ): Bitmap {
        // Tính toán vùng hiển thị thực tế của ảnh trong container
        val imageBounds = calculateActualImageBounds(
            containerWidth, containerHeight, bitmap.width, bitmap.height
        )

        Log.d(TAG, "Image bounds: left=${imageBounds.left}, top=${imageBounds.top}, " +
                "right=${imageBounds.right}, bottom=${imageBounds.bottom}")

        // Kiểm tra xem vùng chọn có nằm trong vùng hiển thị thực tế không
        if (cropX < imageBounds.left || cropY < imageBounds.top ||
            cropX + cropWidth > imageBounds.right || cropY + cropHeight > imageBounds.bottom) {
            Log.w(TAG, "Vùng cắt nằm ngoài vùng hiển thị của ảnh, điều chỉnh lại")
        }

        // Chuyển từ tọa độ container sang tọa độ bitmap
        val scaleX = bitmap.width / imageBounds.width()
        val scaleY = bitmap.height / imageBounds.height()

        val actualX = ((cropX - imageBounds.left) * scaleX).toInt()
        val actualY = ((cropY - imageBounds.top) * scaleY).toInt()
        val actualWidth = (cropWidth * scaleX).toInt()
        val actualHeight = (cropHeight * scaleY).toInt()

        Log.d(TAG, "Vùng cắt thực: x=$actualX, y=$actualY, w=$actualWidth, h=$actualHeight")

        // Đảm bảo vùng cắt nằm trong bitmap
        val safeX = actualX.coerceIn(0, bitmap.width - 1)
        val safeY = actualY.coerceIn(0, bitmap.height - 1)
        val safeWidth = actualWidth.coerceIn(1, bitmap.width - safeX)
        val safeHeight = actualHeight.coerceIn(1, bitmap.height - safeY)

        return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
    }

    /**
     * Delete an image from internal storage
     */
    fun deleteImageFromInternalStorage(filePath: String): Boolean {
        try {
            val file = File(filePath)
            if (file.exists()) {
                return file.delete()
            }
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image: ${e.message}")
            return false
        }
    }

    /**
     * Gets a content Uri from a file path
     */
    fun getUriFromPath(context: Context, filePath: String): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    /**
     * Lưu bitmap thành URI
     */
    suspend fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val filename = "edited_photo_${System.currentTimeMillis()}.jpg"
        try {
            val path = saveImageToInternalStorage(context, bitmap, filename)
            return getUriFromPath(context, path)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to URI: ${e.message}")
            return null
        }
    }
}