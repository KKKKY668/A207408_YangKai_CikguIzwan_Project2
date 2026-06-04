package com.example.a207408_cikguizwan_lab02

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. 数据类：用户配置文件
data class UserProfile(
    val name: String = "",
    val location: String = "Nearby",
)

// 2. 状态类：管理 iNaturalist API 的网络状态
sealed class ApiState {
    object Loading : ApiState()
    data class Success(val data: List<Observation>) : ApiState()
    data class Error(val message: String) : ApiState()
}

// 3. 状态类：管理 Firebase 社区数据的状态 (新加的)
sealed class CommunityState {
    object Loading : CommunityState()
    data class Success(val data: List<CommunityLog>) : CommunityState()
    data class Error(val message: String) : CommunityState()
}

class WildLensViewModel(private val repository: ActivityLogRepository) : ViewModel() {

    // ================= 个人配置 & 本地 Room 数据库 =================
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val activityLogs: StateFlow<List<ActivityLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ================= iNaturalist API 数据 =================
    private val _observationsState = MutableStateFlow<ApiState>(ApiState.Loading)
    val observationsState: StateFlow<ApiState> = _observationsState.asStateFlow()

    // ================= Firebase 社区云端数据 (新加的) =================
    private val _communityState = MutableStateFlow<CommunityState>(CommunityState.Loading)
    val communityState: StateFlow<CommunityState> = _communityState.asStateFlow()

    // 分享状态（用于显示成功/失败的短暂提示）
    private val _shareResult = MutableStateFlow<String?>(null)
    val shareResult: StateFlow<String?> = _shareResult.asStateFlow()

    // ================= 初始化 =================
    init {
        fetchObservations()      // 启动时拉取 iNaturalist API
        listenCommunityLogs()    // 启动时监听 Firebase 社区数据
    }

    // ================= 方法：个人配置 & Room =================
    fun updateProfile(name: String, location: String = "Nearby") {
        _userProfile.value = UserProfile(name = name, location = location)
    }

    fun addActivityLog(log: ActivityLog) {
        viewModelScope.launch {
            repository.insert(log)
        }
    }

    // ================= 方法：iNaturalist API =================
    fun fetchObservations() {
        viewModelScope.launch {
            _observationsState.value = ApiState.Loading
            try {
                val response = RetrofitInstance.api.getObservations()
                _observationsState.value = ApiState.Success(response.results)
            } catch (e: Exception) {
                _observationsState.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // ================= 方法：Firebase Firestore (新加的) =================

    // 监听云端社区数据
    private fun listenCommunityLogs() {
        viewModelScope.launch {
            FirestoreService.getCommunityLogs().collect { logs ->
                _communityState.value = CommunityState.Success(logs)
            }
        }
    }

    // 分享本地记录到云端
    fun shareToFirestore(log: ActivityLog) {
        viewModelScope.launch {
            try {
                FirestoreService.shareLog(log, _userProfile.value.name)
                _shareResult.value = "✔ Shared to community!"
            } catch (e: Exception) {
                _shareResult.value = "⚠ Share failed: ${e.message}"
            }
        }
    }

    // 清除分享状态提示
    fun clearShareResult() {
        _shareResult.value = null
    }
}