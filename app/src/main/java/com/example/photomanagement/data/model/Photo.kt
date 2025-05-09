package com.example.photomanagement.data.model

import java.util.Date

data class Photo(
    val id: String,
    val uri: String,
    val title: String,
    val description: String? = null,
    val dateAdded: Date,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)