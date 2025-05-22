package com.example.photomanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photomanagement.data.db.AppDatabase
import com.example.photomanagement.data.preferences.AppPreferences
import com.example.photomanagement.data.repository.AlbumRepository
import com.example.photomanagement.data.repository.PhotoRepository
import com.example.photomanagement.ui.navigation.PhotoManagementApp
import com.example.photomanagement.ui.theme.PhotoManagementTheme
import com.example.photomanagement.ui.viewmodel.AlbumViewModel
import com.example.photomanagement.ui.viewmodel.AlbumViewModelFactory
import com.example.photomanagement.ui.viewmodel.PhotoViewModel
import com.example.photomanagement.ui.viewmodel.PhotoViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo AppPreferences
        appPreferences = AppPreferences(applicationContext)

        // Khởi tạo repositories
        val albumRepository = AlbumRepository(applicationContext)
        val albumViewModel = ViewModelProvider(
            this,
            AlbumViewModelFactory(albumRepository)
        ).get(AlbumViewModel::class.java)

        setContent {
            // Theo dõi thay đổi dark mode từ StateFlow
            val isDarkTheme by appPreferences.darkModeFlow.collectAsState(initial = appPreferences.isDarkMode)

            // Tạo tham chiếu đến appPreferences để pass xuống các components
            val preferencesRef = remember { appPreferences }

            PhotoManagementTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Khởi tạo repository và viewmodel cho photos
                    val photoRepository = remember { PhotoRepository(applicationContext) }
                    val photoViewModel: PhotoViewModel = viewModel(
                        // Cập nhật factory để truyền thêm context
                        factory = PhotoViewModelFactory(photoRepository, applicationContext)
                    )

                    PhotoManagementApp(
                        photoViewModel = photoViewModel,
                        albumViewModel = albumViewModel,
                        appPreferences = preferencesRef // Truyền appPreferences xuống
                    )
                }
            }
        }
    }
}