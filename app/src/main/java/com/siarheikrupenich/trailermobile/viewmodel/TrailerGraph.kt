package com.siarheikrupenich.trailermobile.viewmodel

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.details.MovieDetailsScreen
import com.siarheikrupenich.trailermobile.presentation.favorites.FavoritesScreen
import com.siarheikrupenich.trailermobile.presentation.trailers.TrailerScreen
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.DETAILS
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.FAVORITES
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraphDestinations.TRAILERS

@Composable
fun TrailerGraph(
    navController: NavHostController = rememberNavController(),
    graphViewModel: TrailerGraphViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(TRAILERS, FAVORITES)

    // TODO: add later a subscription if Internet is on
    val isOffline by graphViewModel.isOffline.collectAsState(initial = false)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    TrailerBottomNavigation(
                        currentRoute = currentRoute,
                        onTabSelected = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    if (isOffline) {
                        OfflineNotificationBanner()
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TRAILERS,
            modifier = Modifier.padding(innerPadding),
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
            composable(FAVORITES) {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("$DETAILS/$movieId")
                    }
                )
            }
            composable(
                route = "$DETAILS/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId") ?: 0L
                MovieDetailsScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun TrailerBottomNavigation(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            selected = currentRoute == TRAILERS,
            onClick = { onTabSelected(TRAILERS) },
            icon = { Icon(imageVector = Icons.Filled.Movie, contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.trailers_title)) },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            selected = currentRoute == FAVORITES,
            onClick = { onTabSelected(FAVORITES) },
            icon = { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.favorites_title)) },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun OfflineNotificationBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.error,
        elevation = 4.dp
    ) {
        Text(
            text = "No internet connection - Showing cached data",
            color = MaterialTheme.colors.onError,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}

internal object TrailerGraphDestinations {
    internal const val TRAILERS = "trailers"
    internal const val FAVORITES = "favorites"
    internal const val DETAILS = "details"
}
