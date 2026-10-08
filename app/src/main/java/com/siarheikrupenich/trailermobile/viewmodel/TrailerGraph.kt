package com.siarheikrupenich.trailermobile.viewmodel

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.siarheikrupenich.trailermobile.presentation.trailers.TrailerScreen
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.HOME_MVI

@Composable
fun TrailerGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = HOME_MVI) {
        composable(HOME_MVI) { TrailerScreen() }
    }
}

internal object TrailerGraphDestinations {
    internal const val HOME_MVI = "home_mvi"
}
