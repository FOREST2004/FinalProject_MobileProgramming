package com.example.photomanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider  // Thêm import này
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photomanagement.data.repository.AlbumRepository
import com.example.photomanagement.data.repository.PhotoRepository
import com.example.photomanagement.ui.navigation.PhotoManagementApp
import com.example.photomanagement.ui.theme.PhotoManagementTheme
import com.example.photomanagement.ui.viewmodel.AlbumViewModel
import com.example.photomanagement.ui.viewmodel.AlbumViewModelFactory
import com.example.photomanagement.ui.viewmodel.PhotoViewModel
import com.example.photomanagement.ui.viewmodel.PhotoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Thêm code này ở đây, trước setContent
        val albumRepository = AlbumRepository()
        val albumViewModel = ViewModelProvider(
            this,
            AlbumViewModelFactory(albumRepository)
        ).get(AlbumViewModel::class.java)

        // In ra log số lượng album sau khi khởi tạo
        println("MainActivity: Initial albums count: ${albumRepository.albums.value.size}")

        setContent {
            PhotoManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Initialize repositories
                    val photoRepository = remember { PhotoRepository(applicationContext) }
                    // Sử dụng albumRepository đã khởi tạo ở trên thay vì tạo mới
                    // val albumRepository = remember { AlbumRepository() }

                    // Create ViewModels
                    val photoViewModel: PhotoViewModel = viewModel(
                        factory = PhotoViewModelFactory(photoRepository)
                    )
                    // Sử dụng albumViewModel đã khởi tạo ở trên thay vì tạo mới qua viewModel
                    // val albumViewModel: AlbumViewModel = viewModel(
                    //    factory = AlbumViewModelFactory(albumRepository)
                    // )

                    PhotoManagementApp(
                        photoViewModel = photoViewModel,
                        albumViewModel = albumViewModel
                    )
                }
            }
        }
    }
}