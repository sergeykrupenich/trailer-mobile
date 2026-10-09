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
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.CategoryFilterChips
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.LoadingState
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.TrailerEmptyState
import com.siarheikrupenich.trailermobile.presentation.trailers.ui.TrailerItem
import com.siarheikrupenich.trailermobile.style.Size

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrailerScreen(
    viewModel: TrailerScreenViewModel = hiltViewModel()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategoryFilterChips(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.nextEvent(TrailerUiEvent.SelectCategory(category))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                when {
                    pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                        LoadingState()
                    }

                    pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0 -> {
                        val errorState = pagingItems.loadState.refresh as LoadState.Error
                        TrailerEmptyState(
                            stateMessage = errorState.error.localizedMessage
                                ?: stringResource(id = R.string.common_error_message),
                            contentDescription = stringResource(id = R.string.error_content_description),
                            stateImageDrawableRes = R.drawable.ic_error,
                            buttonTitle = stringResource(id = R.string.retry),
                            emptyButtonAction = { pagingItems.retry() }
                        )
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
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(count = pagingItems.itemCount) { index ->
                                pagingItems[index]?.let { trailerUi ->
                                    TrailerItem(trailerUi = trailerUi)
                                }
                            }

                            if (pagingItems.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(Size.spacing16),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            if (pagingItems.loadState.append is LoadState.Error) {
                                item {
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
                                        Button(onClick = { pagingItems.retry() }) {
                                            Text(text = stringResource(id = R.string.retry))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
