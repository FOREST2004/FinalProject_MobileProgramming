package com.example.photomanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity siêu đơn giản đại diện cho một ảnh
 */
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    val id: String,

    val uri: String,
    val title: String,
    val description: String? = null,
    val dateAdded: Long,
    val isFavorite: Boolean = false
    // Không cần trường tags nữa, nhưng tạm thời giữ lại để tránh lỗi
    // khi các file khác vẫn đang sử dụng
    , val tags: String = ""
)