package com.example.photomanagement.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * Lớp tiện ích cho chức năng chia sẻ ảnh
 */
object ShareUtils {
    private const val TAG = "ShareUtils"

    /**
     * Chia sẻ một hình ảnh thông qua bảng chia sẻ hệ thống
     *
     * @param context Context để sử dụng cho việc chia sẻ
     * @param imageUri URI của hình ảnh cần chia sẻ
     * @param title Tiêu đề tùy chọn để kèm theo nội dung chia sẻ
     * @param text Văn bản tùy chọn để kèm theo nội dung chia sẻ
     */
    fun shareImage(
        context: Context,
        imageUri: Uri,
        title: String? = null,
        text: String? = null
    ) {
        try {
            // Tạo intent chia sẻ
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)

                // Thêm tiêu đề và văn bản nếu có
                if (!title.isNullOrEmpty()) {
                    putExtra(Intent.EXTRA_SUBJECT, title)
                }

                if (!text.isNullOrEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, text)
                }

                // Cấp quyền đọc tạm thời cho ứng dụng nhận
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Khởi chạy hộp thoại chọn ứng dụng
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"))
            Log.d(TAG, "Đã khởi chạy bảng chia sẻ cho URI: $imageUri")
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi chia sẻ ảnh: ${e.message}", e)
        }
    }

    /**
     * Chia sẻ một hình ảnh trực tiếp đến một ứng dụng cụ thể nếu có
     *
     * @param context Context để sử dụng cho việc chia sẻ
     * @param imageUri URI của hình ảnh cần chia sẻ
     * @param packageName Tên gói của ứng dụng đích (vd: "com.facebook.katana")
     * @param text Văn bản tùy chọn để kèm theo nội dung chia sẻ
     * @return true nếu chia sẻ thành công, false nếu không
     */
    fun shareImageToApp(
        context: Context,
        imageUri: Uri,
        packageName: String,
        text: String? = null
    ): Boolean {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)

                if (!text.isNullOrEmpty()) {
                    putExtra(Intent.EXTRA_TEXT, text)
                }

                setPackage(packageName)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Kiểm tra xem có ứng dụng nào có thể xử lý intent này không
            if (sendIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(sendIntent)
                Log.d(TAG, "Đã chia sẻ ảnh trực tiếp đến gói: $packageName")
                return true
            } else {
                Log.d(TAG, "Không tìm thấy ứng dụng để xử lý gói: $packageName")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi chia sẻ ảnh đến ứng dụng: ${e.message}", e)
            return false
        }
    }

    /**
     * Lấy URI có thể chia sẻ cho một tệp cục bộ
     *
     * @param context Context để sử dụng
     * @param filePath Đường dẫn đến tệp cục bộ
     * @return URI nội dung có thể chia sẻ, hoặc null nếu tạo không thành công
     */
    fun getShareableFileUri(context: Context, filePath: String): Uri? {
        return try {
            val file = File(filePath)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi tạo URI có thể chia sẻ: ${e.message}", e)
            null
        }
    }
}