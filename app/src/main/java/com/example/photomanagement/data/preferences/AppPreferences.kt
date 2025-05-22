package com.example.photomanagement.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("photo_management_prefs", Context.MODE_PRIVATE)

    // StateFlow để phát hiện thay đổi trong theme
    private val _darkModeFlow = MutableStateFlow(isDarkMode)
    val darkModeFlow: StateFlow<Boolean> = _darkModeFlow.asStateFlow()

    var isDarkMode: Boolean
        get() = sharedPrefs.getBoolean(KEY_DARK_MODE, false)
        set(value) {
            sharedPrefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
            _darkModeFlow.value = value // Cập nhật StateFlow khi giá trị thay đổi
        }

    var autoBackup: Boolean
        get() = sharedPrefs.getBoolean(KEY_AUTO_BACKUP, false)
        set(value) = sharedPrefs.edit().putBoolean(KEY_AUTO_BACKUP, value).apply()

    var highQualitySave: Boolean
        get() = sharedPrefs.getBoolean(KEY_HIGH_QUALITY, true)
        set(value) = sharedPrefs.edit().putBoolean(KEY_HIGH_QUALITY, value).apply()

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_AUTO_BACKUP = "auto_backup"
        private const val KEY_HIGH_QUALITY = "high_quality"
    }
}