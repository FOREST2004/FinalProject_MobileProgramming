package com.example.photomanagement.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("photo_management_prefs", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = sharedPrefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = sharedPrefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

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