package com.example.photomanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.photomanagement.data.db.StringListConverter

@Entity(tableName = "albums")
@TypeConverters(StringListConverter::class)
data class Album(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val coverPhotoId: String? = null,
    val dateCreated: Long = System.currentTimeMillis(),
    val photoIds: List<String> = emptyList()
)