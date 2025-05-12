package com.example.photomanagement.utils

import com.example.photomanagement.data.model.Photo
import java.util.Date

/**
 * Extensions cho Photo để làm việc với Date
 */

/**
 * Chuyển đổi Long từ dateAdded thành đối tượng Date
 */
fun Photo.getDateAdded(): Date {
    return Date(this.dateAdded)
}

/**
 * Tạo một bản sao của Photo với Date được chuyển thành Long
 */
fun Photo.withDate(date: Date): Photo {
    return this.copy(dateAdded = date.time)
}