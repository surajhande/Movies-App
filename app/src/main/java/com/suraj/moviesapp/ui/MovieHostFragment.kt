package com.suraj.moviesapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.suraj.moviesapp.databinding.FragmentMovieHostBinding
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieHostFragment : Fragment() {

    private lateinit var binding: FragmentMovieHostBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private val moviesViewModel: MoviesViewModel by activityViewModels()
    private lateinit var pagerAdapter: MoviesPageAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieHostBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initView() {
        pagerAdapter = MoviesPageAdapter(this.requireActivity())
        with(binding) {
           listPager.adapter = pagerAdapter
            listPager.isUserInputEnabled = false
            tabLayout.apply {
                tabSelectionRadioGroup.setOnCheckedChangeListener { _, id ->
                    when (id) {
                        this.tabMovies.id -> {
                            listPager.currentItem = PAGE_MOVIES
                        }
                        this.tabFavorites.id -> {
                            listPager.currentItem = PAGE_FAVORITES
                        }
                    }
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    moviesViewModel.lastQuery = query ?: ""
                    moviesViewModel.page = 1
                    hideKeybaord(binding.searchView)
                    if (query.isNullOrBlank()) return false
                    // Do a fresh search
                    moviesViewModel.fetchMovies(moviesViewModel.lastQuery, 1)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun hideKeybaord(view: View) {
        val imm =
            (activity)?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {

        private const val PAGE_MOVIES = 0
        private const val PAGE_FAVORITES = 1

        @JvmStatic
        fun newInstance() = MovieHostFragment()
    }

    inner class MoviesPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            if (position == 0) {
                return MoviesListFragment.newInstance()
            } else
                return FavoritesListFragment.newInstance()
        }
    }

}

