package com.example.a207408_cikguizwan_lab02

import kotlinx.coroutines.flow.Flow

class ActivityLogRepository(private val activityLogDao: ActivityLogDao) {
    val allLogs: Flow<List<ActivityLog>> = activityLogDao.getAllLogs()

    suspend fun insert(log: ActivityLog) {
        activityLogDao.insertLog(log)
    }
}