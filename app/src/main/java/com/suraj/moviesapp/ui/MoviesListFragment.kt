package com.suraj.moviesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suraj.moviesapp.R
import com.suraj.moviesapp.databinding.FragmentMoviesBinding
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MovieSearchUiModel
import com.suraj.moviesapp.model.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesListFragment : Fragment() {

    private lateinit var binding: FragmentMoviesBinding
    private val viewModel: MoviesViewModel by activityViewModels()
    private lateinit var moviesAdapter: MoviesAdapter
    private var queryResult = ArrayList<MovieItemDTO>()

    private var currItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0
    private var isScrolling = false
    private var lastClicked = 0

    companion object {
        fun newInstance() = MoviesListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoviesBinding.inflate(layoutInflater)
        binding.list.apply {
            val itemClickListener: (MovieItemDTO) -> Unit = { goToDetails(it) }
            val favClickListener: (MovieItemDTO) -> Unit = { viewModel.updateFavorites(it) }
            val linearLayoutManager = LinearLayoutManager(requireContext())
            moviesAdapter = MoviesAdapter(favClickListener, itemClickListener)

            adapter = moviesAdapter
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        isScrolling = true
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    currItems = linearLayoutManager.childCount
                    totalItems = linearLayoutManager.itemCount
                    scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition()

                    if (dy > 0 && isScrolling && (currItems + scrollOutItems == totalItems)) {
                        isScrolling = false
                        viewModel.fetchMovies(viewModel.lastQuery, viewModel.page)
                    }
                }
            })
        }

        return binding.root
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.movieQueryResult.observe(this, Observer { onQueryResultUpdated(it!!) })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.movieQueryResult.removeObservers(this)
    }

    private fun onQueryResultUpdated(uiModel: MovieSearchUiModel) {
        when (uiModel) {
            is MovieSearchUiModel.Loading -> {

            }
            is MovieSearchUiModel.Error -> {

            }
            is MovieSearchUiModel.Success -> {
                if (uiModel.isRedelivered)
                    return
                if (viewModel.page == 1)
                        queryResult = ArrayList()

                queryResult.addAll(uiModel.result)
                moviesAdapter.submitList(queryResult)
                moviesAdapter.notifyDataSetChanged()
                viewModel.page += 1
                binding.text.visibility = if(queryResult.size == 0) View.VISIBLE else View.GONE
            }
        }
    }
}