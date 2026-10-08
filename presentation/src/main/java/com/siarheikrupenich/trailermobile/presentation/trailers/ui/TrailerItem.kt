package com.siarheikrupenich.trailermobile.presentation.trailers.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.style.Size
import com.siarheikrupenich.trailermobile.style.UiConstants.Companion.COLOR_ALPHA_20

@Composable
internal fun TrailerItem(trailerUi: TrailerUi) {

}

@Preview(showBackground = true)
@Composable
fun PreviewLazyColumnWithRoundedCards() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(10) { _ ->
            TrailerItem(
                TrailerUi(0, "Test title", "test description")
            )
        }
    }
}
