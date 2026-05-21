package com.example.a207408_cikguizwan_lab02

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    // 获取所有数据，并按 ID 降序排列（让最新添加的显示在最上面）
    @Query("SELECT * FROM activity_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog)
}