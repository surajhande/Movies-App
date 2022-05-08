package com.suraj.moviesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suraj.moviesapp.R
import com.suraj.moviesapp.databinding.FragmentFavoritesBinding
import com.suraj.moviesapp.databinding.FragmentMoviesBinding
import com.suraj.moviesapp.model.FavoritesUiModel
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesListFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: MoviesViewModel by activityViewModels()
    private lateinit var moviesAdapter: MoviesAdapter

    companion object {
        fun newInstance() = FavoritesListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        binding.list.apply {
            val itemClickListener: (MovieItemDTO) -> Unit = { goToDetails(it) }
            val favClickListener: (MovieItemDTO) -> Unit = { viewModel.updateFavorites(it) }
            val linearLayoutManager = LinearLayoutManager(requireContext())
            moviesAdapter = MoviesAdapter(favClickListener, itemClickListener)

            adapter = moviesAdapter
            layoutManager = linearLayoutManager
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFavorites()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.favoriteResult.observe(this, Observer { onFavoritesListUpdated(it!!) })
    }

    private fun onFavoritesListUpdated(uiModel: FavoritesUiModel) {
        when (uiModel) {
            is FavoritesUiModel.Loading -> {

            }
            is FavoritesUiModel.Error -> {

            }
            is FavoritesUiModel.Success -> {
                if (uiModel.isRedelivered)
                    return
                moviesAdapter.submitList(uiModel.result)
                binding.text.visibility = if(uiModel.result.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun goToDetails(movie: MovieItemDTO) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.add(
                R.id.fragment_container_view,
                MovieDetailFragment.newInstance(movie)
            )
            ?.addToBackStack("")
            ?.commit()
    }
}