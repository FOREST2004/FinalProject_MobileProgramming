package com.example.photomanagement.data.model

sealed class EditOperation {
    data class Rotate(val degrees: Float) : EditOperation()
    object FlipHorizontal : EditOperation()
    object FlipVertical : EditOperation()
    data class Crop(val x: Int, val y: Int, val width: Int, val height: Int) : EditOperation()
}