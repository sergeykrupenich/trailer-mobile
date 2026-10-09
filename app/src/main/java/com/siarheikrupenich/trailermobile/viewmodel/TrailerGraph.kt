package com.siarheikrupenich.trailermobile.viewmodel

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.siarheikrupenich.trailermobile.presentation.details.MovieDetailsScreen
import com.siarheikrupenich.trailermobile.presentation.trailers.TrailerScreen
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.DETAILS
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.TRAILERS

@Composable
fun TrailerGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = TRAILERS,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
    ) {
        composable(TRAILERS) {
            TrailerScreen(
                onMovieClick = { movieId ->
                    navController.navigate("$DETAILS/$movieId")
                }
            )
        }
        composable(
            route = "$DETAILS/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.LongType })
        ) {
            MovieDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

internal object TrailerGraphDestinations {
    internal const val TRAILERS = "trailers"
    internal const val DETAILS = "details"
}
