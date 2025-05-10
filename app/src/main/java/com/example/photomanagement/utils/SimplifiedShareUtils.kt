package com.example.photomanagement.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Lớp tiện ích đơn giản để chia sẻ ảnh
 */
object SimplifiedShareUtils {
    private const val TAG = "SimplifiedShareUtils"

    /**
     * Chia sẻ một hình ảnh thông qua hộp thoại chia sẻ hệ thống
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

            // Hiển thị hộp thoại chọn ứng dụng chia sẻ
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"))
            Log.d(TAG, "Đã mở hộp thoại chia sẻ hệ thống cho URI: $imageUri")
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi chia sẻ ảnh: ${e.message}", e)
        }
    }
}