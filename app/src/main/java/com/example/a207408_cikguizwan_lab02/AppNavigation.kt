package com.example.a207408_cikguizwan_lab02

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MENU = "menu"
    const val IDENTIFY = "identify"
    const val ACTIVITY = "activity"
    const val DISCOVER = "discover"
    const val COMMUNITY = "community"
}

@Composable
fun WildLensNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = WildLensDatabase.getDatabase(context)
    val repository = ActivityLogRepository(database.activityLogDao())

    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WildLensViewModel::class.java)) {
                return WildLensViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val viewModel: WildLensViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            WildLensApp(
                viewModel = viewModel,
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onMenuClick = { navController.navigate(Routes.MENU) },
                onIdentifyClick = { navController.navigate(Routes.IDENTIFY) },
                onActivityClick = { navController.navigate(Routes.ACTIVITY) },
                onDiscoverClick = { navController.navigate(Routes.DISCOVER) }
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
            ActivityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCommunityClick = { navController.navigate(Routes.COMMUNITY) }
            )
        }

        composable(Routes.DISCOVER) {
            DiscoverScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.COMMUNITY) {
            CommunityScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}