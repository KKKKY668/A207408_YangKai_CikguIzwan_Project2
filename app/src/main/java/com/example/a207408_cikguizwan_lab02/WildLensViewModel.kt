package com.example.a207408_cikguizwan_lab02

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserProfile(
    val name: String = "",
    val location: String = "Nearby",
)

// 修改构造函数，传入 Repository
class WildLensViewModel(private val repository: ActivityLogRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // 从 Repository 动态获取数据流，转换成 StateFlow 供 Compose 收集
    val activityLogs: StateFlow<List<ActivityLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateProfile(name: String, location: String = "Nearby") {
        _userProfile.value = UserProfile(name = name, location = location )
    }

    // 存入数据库
    fun addActivityLog(log: ActivityLog) {
        viewModelScope.launch {
            repository.insert(log)
        }
    }
}