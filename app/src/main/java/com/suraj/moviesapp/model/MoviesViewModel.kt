package com.suraj.moviesapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suraj.moviesapp.repository.MoviesRepository
import com.suraj.moviesapp.ui.TransientAwareLiveData
import com.suraj.moviesapp.ui.TransientAwareUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class MovieSearchUiModel() : TransientAwareUiModel() {
    object Loading : MovieSearchUiModel()
    object Error : MovieSearchUiModel()
    data class Success(val result: List<MovieItemDTO>) : MovieSearchUiModel()
}

sealed class FavoritesUiModel() : TransientAwareUiModel() {
    object Loading : FavoritesUiModel()
    object Error : FavoritesUiModel()
    data class Success(val result: List<MovieItemDTO>) : FavoritesUiModel()
}

sealed class MovieDetailUiModel() : TransientAwareUiModel() {
    object Loading : MovieDetailUiModel()
    object Error : MovieDetailUiModel()
    data class Success(val result: MovieDetailsDTO) : MovieDetailUiModel()
}

@HiltViewModel
class MoviesViewModel @Inject constructor(private val moviesRepository: MoviesRepository) :
    ViewModel() {

    private val _movieQueryResult = TransientAwareLiveData<MovieSearchUiModel>()
    val movieQueryResult: LiveData<MovieSearchUiModel> = _movieQueryResult

    private val _favoriteResult = TransientAwareLiveData<FavoritesUiModel>()
    val favoriteResult: LiveData<FavoritesUiModel> = _favoriteResult

    private val _detailResult = TransientAwareLiveData<MovieDetailUiModel>()
    val detailResult: LiveData<MovieDetailUiModel> = _detailResult

    var page = 1
    var lastQuery = ""

    fun fetchMovies(query: String, page: Int) = viewModelScope.launch {
        _movieQueryResult.postValue(MovieSearchUiModel.Loading)
        moviesRepository.fetchMovies(query, page).collect {
            if (it.response == "false")
                _movieQueryResult.postValue(MovieSearchUiModel.Error)
            else {
                _movieQueryResult.postValue(MovieSearchUiModel.Success(it.search))
            }
        }
    }

    fun fetchFavorites() = viewModelScope.launch {
        _favoriteResult.postValue(FavoritesUiModel.Loading)
        moviesRepository.fetchFavorites().collect {
            _favoriteResult.postValue(FavoritesUiModel.Success(it))
        }
    }

    fun updateFavorites(movieItemDTO: MovieItemDTO) = viewModelScope.launch {
        if (movieItemDTO.isFavorite)
            moviesRepository.insertFavorite(movieItemDTO)
        else
            moviesRepository.removeFavorite(movieItemDTO)
    }

    fun updateFavorite(movieItemDTO: MovieItemDTO) = viewModelScope.launch {
        val fromDb = moviesRepository.fetchMovie(movieItemDTO.id).await()
        movieItemDTO.isFavorite = fromDb?.isFavorite ?: false
    }

    fun fetchMovieDetails(id: String) = viewModelScope.launch {
        _detailResult.postValue(MovieDetailUiModel.Loading)
        moviesRepository.fetchMovieDetails(id).collect {
            if (it.id == "")
                _detailResult.postValue(MovieDetailUiModel.Error)
            else
                _detailResult.postValue(MovieDetailUiModel.Success(it))
        }
    }
}