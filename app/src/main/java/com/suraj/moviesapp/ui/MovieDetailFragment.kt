package com.suraj.moviesapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.suraj.moviesapp.R
import com.suraj.moviesapp.databinding.FragmentMovieDetailBinding
import com.suraj.moviesapp.model.MovieDetailUiModel
import com.suraj.moviesapp.model.MovieDetailsDTO
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_MOVIE = "ARG_MOVIE"

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private lateinit var movieItem: MovieItemDTO
    private var isFavorite: Boolean = false
    lateinit var binding: FragmentMovieDetailBinding

    private val moviesViewModel: MoviesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moviesViewModel.detailResult.observe(this, Observer { onMovieDetailsUpdated(it) })

        arguments?.let {
            movieItem = it.getParcelable(ARG_MOVIE)!!
            isFavorite = movieItem.isFavorite
        }
        moviesViewModel.fetchMovieDetails(movieItem.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onMovieDetailsUpdated(uiModel: MovieDetailUiModel) {
        when (uiModel) {
            is MovieDetailUiModel.Error -> {

            }
            is MovieDetailUiModel.Loading -> {

            }
            is MovieDetailUiModel.Success -> {
                updateView(uiModel.result)
            }
        }
    }

    private fun updateView(details: MovieDetailsDTO) {
        binding.apply {
            tvTitle.text = details.title
            tvYear.text = details.year
            tvRating.text = details.rated
            tvCast.text = "Cast: " + details.cast
            tvPlot.text = details.plot
            tvDirector.text = "Directed by: " + details.director
            ivPoster.loadFromUrl(details.posterUrl)
            cbFav.isChecked = isFavorite
            cbFav.setOnCheckedChangeListener { _, isChecked ->
                details.isFavorite = isChecked
                moviesViewModel.updateFavorites(details.convertToMovieItem())
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(movieItem: MovieItemDTO) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MOVIE, movieItem)
                }
            }
    }
}