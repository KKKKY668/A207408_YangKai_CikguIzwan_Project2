package com.example.a207408_cikguizwan_lab02

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ActivityLog::class], version = 1, exportSchema = false)
abstract class WildLensDatabase : RoomDatabase() {
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var Instance: WildLensDatabase? = null

        fun getDatabase(context: Context): WildLensDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    WildLensDatabase::class.java,
                    "wildlens_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}