package com.siarheikrupenich.trailermobile.presentation.trailers.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.presentation.R
import com.siarheikrupenich.trailermobile.style.Size

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryFilterChips(
    selectedCategory: MovieCategory,
    onCategorySelected: (MovieCategory) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<MovieCategory> = MovieCategory.entries
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Size.spacing16, vertical = Size.spacing8),
        horizontalArrangement = Arrangement.spacedBy(Size.spacing8)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            CategoryFilterChipItem(
                category = category,
                isSelected = isSelected,
                onClick = onCategorySelected
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryFilterChipItem(
    category: MovieCategory,
    isSelected: Boolean,
    onClick: (MovieCategory) -> Unit,
) {
    Surface(
        onClick = { onClick(category) },
        shape = RoundedCornerShape(20.dp),
        color = getBackgroundColor(isSelected),
        contentColor = getContentColor(isSelected),
        border = getBorder(isSelected),
        elevation = getElevation(isSelected)
    ) {
        Text(
            text = stringResource(id = category.stringResId()),
            style = MaterialTheme.typography.body2.copy(
                fontWeight = getFontWeight(isSelected)
            ),
            modifier = Modifier.padding(horizontal = Size.spacing16, vertical = Size.spacing8)
        )
    }
}

@Composable
private fun getBackgroundColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
    }
}

@Composable
private fun getContentColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colors.onPrimary
    } else {
        MaterialTheme.colors.onSurface
    }
}

@Composable
private fun getBorder(isSelected: Boolean): BorderStroke? {
    return if (!isSelected) {
        BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
    } else {
        null
    }
}

private fun getElevation(isSelected: Boolean): Dp {
    return if (isSelected) 2.dp else 0.dp
}

private fun getFontWeight(isSelected: Boolean): FontWeight {
    return if (isSelected) FontWeight.Bold else FontWeight.Normal
}

fun MovieCategory.stringResId(): Int {
    return when (this) {
        MovieCategory.NOW_PLAYING -> R.string.category_now_playing
        MovieCategory.UPCOMING -> R.string.category_upcoming
        MovieCategory.TOP_RATED -> R.string.category_top_rated
    }
}
