package com.example.dlinkexam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dlinkexam.ui.detail.DetailScreen
import com.example.dlinkexam.ui.list.ListScreen

@Composable
fun DLinkExamNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            ListScreen(onStationClick = { sno -> navController.navigate(Routes.detail(sno)) })
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.STATION_SNO_ARG) { type = NavType.StringType }),
        ) {
            DetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
