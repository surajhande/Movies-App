package com.suraj.moviesapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suraj.moviesapp.R
import com.suraj.moviesapp.databinding.FragmentHostBinding
import com.suraj.moviesapp.model.FavoritesUiModel
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MovieSearchUiModel
import com.suraj.moviesapp.model.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HostFragment : Fragment() {

    private lateinit var binding: FragmentHostBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var loadingAdapter: LoadingAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val moviesViewModel: MoviesViewModel by viewModels()
    private var lastQuery = ""
    private var queryResult = ArrayList<MovieItemDTO>()
    private var favoriteList: List<MovieItemDTO> = ArrayList<MovieItemDTO>()
    var isScrolling = false
    var lastClicked = 0

    private var currItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0
    private var isShowingFavorites = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesViewModel.favoriteResult.observe(this, Observer { onFavoritesUpdated(it!!) })
        moviesViewModel.movieQueryResult.observe(this, Observer { onMovieResultUpdated(it!!) })
        binding.movieList.apply {
            val favClickListener: (MovieItemDTO) -> Unit = {
                moviesViewModel.updateFavorites(it)
            }
            val itemClickListener: (MovieItemDTO) -> Unit = { goToDetails(it) }
            moviesAdapter = MoviesAdapter(favClickListener, itemClickListener)
            loadingAdapter = LoadingAdapter(false)
            concatAdapter = ConcatAdapter(moviesAdapter, loadingAdapter)
            adapter = moviesAdapter
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (isShowingFavorites) return
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        isScrolling = true
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (isShowingFavorites) return
                    currItems = linearLayoutManager.childCount
                    totalItems = linearLayoutManager.itemCount
                    scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition()

                    if (dy > 0 && isScrolling && (currItems + scrollOutItems == totalItems)) {
                        isScrolling = false
                        moviesViewModel.fetchMovies(lastQuery, moviesViewModel.page)
                    }
                }
            })
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lastQuery = query ?: ""
                queryResult = arrayListOf()
                moviesAdapter.submitList(queryResult)
                moviesViewModel.page = 1
                hideKeybaord(binding.searchView)
                if (query.isNullOrBlank()) return false
                // Do a fresh search
                moviesViewModel.fetchMovies(lastQuery, 1)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.tabLayout.apply {
            tabSelectionRadioGroup.setOnCheckedChangeListener { _, id ->
                when (id) {
                    this.tabMovies.id -> {
                        moviesAdapter.submitList(queryResult)
                        isShowingFavorites = false
                    }
                    this.tabFavorites.id -> {
                        moviesViewModel.loadFavorites()
                        isShowingFavorites = true
                    }
                }
                updateEmptyText()
            }
        }

        updateEmptyText()

    }


    override fun onResume() {
        super.onResume()
        //moviesViewModel.loadFavorites()
        queryResult.forEach { moviesViewModel.updateFavorite(it) }
    }
    private fun updateEmptyText() {
        binding.tvEmptyView.visibility =
            if (!isShowingFavorites && queryResult.size == 0) View.VISIBLE else View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onMovieResultUpdated(uiModel: MovieSearchUiModel) {
        // Leaving loading and error states as TODO.
        when (uiModel) {
            is MovieSearchUiModel.Loading -> {
            }
            is MovieSearchUiModel.Error -> {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong. Try again!",
                    Toast.LENGTH_LONG
                )
                    .show()
                updateEmptyText()
            }
            is MovieSearchUiModel.Success -> {
                queryResult.addAll(uiModel.result)
                updateEmptyText()
                if (!isShowingFavorites) {
                    moviesAdapter.submitList(queryResult)
                    val listState = binding.movieList.layoutManager?.onSaveInstanceState()
                    moviesAdapter.notifyDataSetChanged()
                    binding.movieList.layoutManager?.onRestoreInstanceState(listState)
                }
                if (moviesViewModel.page == 1)
                    binding.movieList.layoutManager?.scrollToPosition(0)
                moviesViewModel.page += 1
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onFavoritesUpdated(uiModel: FavoritesUiModel) {
        // Leaving loading and error states as TODO.
        when (uiModel) {
            is FavoritesUiModel.Loading -> {

            }
            is FavoritesUiModel.Success -> {
                if (isShowingFavorites) {
                    favoriteList = uiModel.result
                    val listState = binding.movieList.layoutManager?.onSaveInstanceState()
                    moviesAdapter.submitList(favoriteList)
                    binding.movieList.layoutManager?.onRestoreInstanceState(listState)
                }
            }
            FavoritesUiModel.Error -> {}
        }
    }

    private fun goToDetails(movie: MovieItemDTO) {

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container_view,
                MovieDetailFragment.newInstance(movie.id, movie.isFavorite))
            ?.addToBackStack("")
            ?.commit()
    }

    private fun hideKeybaord(view: View) {
        val imm =
            (activity)?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HostFragment()
    }
}