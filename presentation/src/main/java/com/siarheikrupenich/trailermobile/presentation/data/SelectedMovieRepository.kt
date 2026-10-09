package com.siarheikrupenich.trailermobile.presentation.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedMovieRepository @Inject constructor() {
    private var selectedMovie: TrailerUi? = null

    fun setSelectedMovie(movie: TrailerUi) {
        selectedMovie = movie
    }

    fun getSelectedMovie(): TrailerUi? = selectedMovie
}
