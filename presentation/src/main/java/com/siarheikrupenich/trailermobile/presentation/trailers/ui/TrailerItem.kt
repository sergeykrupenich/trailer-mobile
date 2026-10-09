package com.siarheikrupenich.trailermobile.presentation.trailers.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.style.Size
import java.util.Locale

@Composable
internal fun TrailerItem(
    trailerUi: TrailerUi,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit = {},
    onFavoriteClick: (TrailerUi) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Size.spacing16, vertical = Size.spacing8)
            .clickable { onClick(trailerUi.id) },
        elevation = 4.dp,
        shape = RoundedCornerShape(Size.spacing8)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Size.spacing12)
            ) {
                trailerUi.posterPath?.let { posterUrl ->
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = trailerUi.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(90.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(Size.spacing8))
                    )
                    Spacer(modifier = Modifier.width(Size.spacing12))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 36.dp)
                ) {
                    Text(
                        text = trailerUi.title,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (trailerUi.releaseDate.isNotBlank() || trailerUi.voteAverage > 0.0) {
                        Spacer(modifier = Modifier.height(Size.spacing4))
                        Text(
                            text = stringResource(
                                id = R.string.movie_rating_release_format,
                                String.format(Locale.US, "%.1f", trailerUi.voteAverage),
                                trailerUi.releaseDate
                            ),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(Size.spacing8))
                    Text(
                        text = trailerUi.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { onFavoriteClick(trailerUi) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Size.spacing4)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) {
                        stringResource(id = R.string.favorite_remove_content_description)
                    } else {
                        stringResource(id = R.string.favorite_add_content_description)
                    },
                    tint = if (isFavorite) Color.Red else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLazyColumnWithRoundedCards() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(10) { index ->
            TrailerItem(
                TrailerUi(
                    id = index.toLong(),
                    title = "Resident Evil $index",
                    description = "Medical courier Bryan unwittingly finds himself fighting for survival.",
                    voteAverage = 7.3,
                    releaseDate = "2026-09-16"
                ),
                isFavorite = index % 2 == 0
            )
        }
    }
}
