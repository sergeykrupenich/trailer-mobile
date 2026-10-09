package com.siarheikrupenich.trailermobile.presentation.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.TrailerItem
import com.siarheikrupenich.trailermobile.style.FontSize
import com.siarheikrupenich.trailermobile.style.Size

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onMovieClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.favorites_title),
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                    )
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 4.dp
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.favorites.isEmpty() -> {
                    FavoritesEmptyState()
                }

                else -> {
                    FavoritesList(
                        favorites = uiState.favorites,
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { movie ->
                            viewModel.nextEvent(FavoritesUiEvent.ToggleFavorite(movie))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Size.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_state),
            contentDescription = stringResource(id = R.string.empty_content_description),
            modifier = Modifier.size(Size.emptyStateIconSize)
        )
        Spacer(modifier = Modifier.height(Size.spacing24))
        Text(
            text = stringResource(id = R.string.favorites_empty_title),
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.emptyStateTitleSize
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FavoritesList(
    favorites: List<TrailerUi>,
    onMovieClick: (Long) -> Unit,
    onFavoriteClick: (TrailerUi) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = favorites,
            key = { it.id }
        ) { trailerUi ->
            TrailerItem(
                trailerUi = trailerUi,
                isFavorite = true,
                onClick = onMovieClick,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPopulatedPreview() {
    MaterialTheme {
        FavoritesList(
            favorites = listOf(
                TrailerUi(
                    id = 1L,
                    title = "Fight Club",
                    description = "A ticking-time-bomb insomniac and a slippery soap salesman channel maximalist male aggression.",
                    voteAverage = 8.4,
                    releaseDate = "1999-10-15"
                ),
                TrailerUi(
                    id = 2L,
                    title = "Interstellar",
                    description = "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot is tasked to pilot a spacecraft.",
                    voteAverage = 8.7,
                    releaseDate = "2014-11-07"
                )
            ),
            onMovieClick = {},
            onFavoriteClick = {}
        )
    }
}
