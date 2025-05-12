package com.example.photomanagement.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.photomanagement.data.model.Photo

/**
 * Database siêu đơn giản
 */
@Database(entities = [Photo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  // Thêm dòng này
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "photo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}