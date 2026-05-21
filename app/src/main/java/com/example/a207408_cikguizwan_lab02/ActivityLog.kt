package com.example.a207408_cikguizwan_lab02

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自动生成 ID
    val species: String,
    val location: String,
    val time: String,
    val imageRes: Int
)