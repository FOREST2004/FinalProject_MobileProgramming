package com.example.photomanagement.data.model

data class Album(
    val id: String,
    val name: String,
    val description: String? = null,
    val photoIds: List<String> = emptyList(),
    val coverPhotoId: String? = null
)