package com.siarheikrupenich.trailermobile.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.style.Size
import java.util.Locale

@Composable
fun MovieDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: TrailerDetailsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val movie = uiState.movie

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movie?.title ?: stringResource(id = R.string.trailers_title),
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_content_description),
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
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

                movie != null -> {
                    MovieDetailsContent(
                        movie = movie,
                        releaseYear = uiState.releaseYear
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: stringResource(id = R.string.common_error_message),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: TrailerUi,
    releaseYear: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Size.spacing16)
    ) {
        movie.backdropPath?.let { backdrop ->
            AsyncImage(
                model = backdrop,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.height(Size.spacing16))
        } ?: movie.posterPath?.let { poster ->
            AsyncImage(
                model = poster,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Spacer(modifier = Modifier.height(Size.spacing16))
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Size.spacing8))

        if (releaseYear.isNotBlank() || movie.voteAverage > 0.0) {
            Text(
                text = stringResource(
                    id = R.string.movie_rating_release_format,
                    String.format(Locale.US, "%.1f", movie.voteAverage),
                    releaseYear
                ),
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(Size.spacing16))
        }

        Text(
            text = stringResource(id = R.string.movie_description_title),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Size.spacing4))
        Text(
            text = movie.description,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f)
        )
    }
}
