package com.example.a207408_cikguizwan_lab02

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a207408_cikguizwan_lab02.WildLensDatabase

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MENU = "menu"
    const val IDENTIFY = "identify"
    const val ACTIVITY = "activity"
}

@Composable
fun WildLensNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 1. 初始化 Room 数据库实例
    val database = WildLensDatabase.getDatabase(context)
    val repository = ActivityLogRepository(database.activityLogDao())

    // 2. ViewModel Factory：这是连接 UI 和 Room 数据的关键桥梁
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WildLensViewModel::class.java)) {
                return WildLensViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // 初始化 ViewModel
    val viewModel: WildLensViewModel = viewModel(factory = factory)

    // 3. 导航配置
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            WildLensApp(
                viewModel = viewModel,
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onMenuClick = { navController.navigate(Routes.MENU) },
                onIdentifyClick = { navController.navigate(Routes.IDENTIFY) },
                onActivityClick = { navController.navigate(Routes.ACTIVITY) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.MENU) {
            MenuScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.IDENTIFY) {
            IdentifyScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.ACTIVITY) {
            ActivityScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}