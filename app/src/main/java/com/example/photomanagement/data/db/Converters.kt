package com.example.photomanagement.data.db

import androidx.room.TypeConverter
import java.util.Date

/**
 * Lớp Converters cung cấp các phương thức để chuyển đổi giữa các kiểu dữ liệu mà Room không
 * hỗ trợ trực tiếp (như Date, List<String>) và các kiểu dữ liệu mà SQLite hỗ trợ.
 */
class Converters {
    /**
     * Chuyển đổi từ timestamp (Long) sang Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Chuyển đổi từ Date sang timestamp (Long)
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Chuyển đổi từ List<String> sang chuỗi lưu trữ
     */
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    /**
     * Chuyển đổi từ chuỗi lưu trữ sang List<String>
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}