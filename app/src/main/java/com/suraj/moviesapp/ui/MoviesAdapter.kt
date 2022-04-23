package com.suraj.moviesapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suraj.moviesapp.databinding.ItemMovieBinding
import com.suraj.moviesapp.model.MovieItemDTO

class MoviesAdapter(private val favClickListener: (MovieItemDTO) -> Unit,
                    private val itemClickListener: (MovieItemDTO) -> Unit) : ListAdapter<MovieItemDTO, MovieViewHolder>(MovieDiffCallback()) {

    private lateinit var binding: ItemMovieBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.binding.cbFav.setOnCheckedChangeListener {_, isChecked ->
            if (position < itemCount) {
                getItem(position).isFavorite = isChecked
                favClickListener(getItem(position))
            }
        }
        holder.binding.root.setOnClickListener {
            if (position < itemCount) {
                itemClickListener(getItem(position))
            }
        }
        holder.bind(getItem(position))
    }
}

class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MovieItemDTO) {
        binding.tvTitle.text = item.title
        binding.tvYear.text = item.year
        binding.ivThumbnail.loadFromUrl(item.posterUrl)
        binding.cbFav.isChecked = item.isFavorite
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieItemDTO>() {

    override fun areItemsTheSame(oldItem: MovieItemDTO, newItem: MovieItemDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieItemDTO, newItem: MovieItemDTO): Boolean {
        return oldItem.id == newItem.id && oldItem.isFavorite == newItem.isFavorite
    }
}