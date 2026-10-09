package com.siarheikrupenich.trailermobile.presentation.trailers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.CategoryFilterChips
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.LoadingState
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.TrailerEmptyState
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.TrailerItem
import com.siarheikrupenich.trailermobile.style.Size

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrailerScreen(
    viewModel: TrailerScreenViewModel = hiltViewModel(),
    onMovieClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagingItems = viewModel.pagedTrailers.collectAsLazyPagingItems()
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount > 0
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.trailers_title),
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                    )
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 4.dp
            )
        }
    ) { innerPadding ->
        TrailerContent(
            uiState = uiState,
            pagingItems = pagingItems,
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState,
            onCategorySelected = { category ->
                viewModel.nextEvent(TrailerUiEvent.SelectCategory(category))
            },
            onMovieClick = onMovieClick,
            onFavoriteClick = { movie ->
                viewModel.nextEvent(TrailerUiEvent.ToggleFavorite(movie))
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrailerContent(
    uiState: MviTrailerState,
    pagingItems: LazyPagingItems<TrailerUi>,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState,
    onCategorySelected: (com.siarheikrupenich.trailermobile.domain.model.MovieCategory) -> Unit,
    onMovieClick: (Long) -> Unit,
    onFavoriteClick: (TrailerUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CategoryFilterChips(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth()
        )

        TrailerListContainer(
            uiState = uiState,
            pagingItems = pagingItems,
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState,
            onMovieClick = onMovieClick,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrailerListContainer(
    uiState: MviTrailerState,
    pagingItems: LazyPagingItems<TrailerUi>,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState,
    onMovieClick: (Long) -> Unit,
    onFavoriteClick: (TrailerUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        when {
            pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                LoadingState()
            }

            pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0 -> {
                TrailerErrorState(pagingItems)
            }

            pagingItems.itemCount == 0 -> {
                TrailerEmptyState(
                    stateMessage = stringResource(id = R.string.empty_state_title),
                    contentDescription = stringResource(id = R.string.empty_content_description),
                    stateImageDrawableRes = R.drawable.ic_error,
                    buttonTitle = stringResource(id = R.string.retry),
                    emptyButtonAction = { pagingItems.refresh() }
                )
            }

            else -> {
                TrailerLazyColumn(
                    uiState = uiState,
                    pagingItems = pagingItems,
                    onMovieClick = onMovieClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun TrailerErrorState(pagingItems: LazyPagingItems<TrailerUi>) {
    val errorState = pagingItems.loadState.refresh as LoadState.Error
    TrailerEmptyState(
        stateMessage = errorState.error.localizedMessage
            ?: stringResource(id = R.string.common_error_message),
        contentDescription = stringResource(id = R.string.error_content_description),
        stateImageDrawableRes = R.drawable.ic_error,
        buttonTitle = stringResource(id = R.string.retry),
        emptyButtonAction = { pagingItems.refresh() }
    )
}

@Composable
private fun TrailerLazyColumn(
    uiState: MviTrailerState,
    pagingItems: LazyPagingItems<TrailerUi>,
    onMovieClick: (Long) -> Unit,
    onFavoriteClick: (TrailerUi) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = pagingItems.itemCount) { index ->
            pagingItems[index]?.let { trailerUi ->
                TrailerItem(
                    trailerUi = trailerUi,
                    isFavorite = uiState.favoriteIds.contains(trailerUi.id),
                    onClick = onMovieClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        if (pagingItems.loadState.append is LoadState.Loading) {
            item {
                TrailerAppendLoading()
            }
        }

        if (pagingItems.loadState.append is LoadState.Error) {
            item {
                TrailerAppendError(onRetry = { pagingItems.retry() })
            }
        }
    }
}

@Composable
private fun TrailerAppendLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Size.spacing16),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TrailerAppendError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Size.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.common_error_message),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(Size.spacing8))
        Button(onClick = onRetry) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrailerScreenPopulatedPreview() {
    MaterialTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(2) { index ->
                TrailerItem(
                    trailerUi = TrailerUi(
                        id = index.toLong(),
                        title = if (index == 0) "Inception" else "The Dark Knight",
                        description = "A thief who steals corporate secrets through the use of dream-sharing technology...",
                        voteAverage = if (index == 0) 8.8 else 9.0,
                        releaseDate = "2010-07-16"
                    ),
                    isFavorite = index == 0
                )
            }
        }
    }
}
