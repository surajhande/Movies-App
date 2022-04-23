package com.suraj.moviesapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suraj.moviesapp.databinding.ItemLoadingBinding

class LoadingAdapter(private var hasMore: Boolean) : RecyclerView.Adapter<LoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) = holder.bind(hasMore)

    override fun getItemCount(): Int = 1

    @SuppressLint("NotifyDataSetChanged")
    fun setLoadState(more: Boolean) {
        hasMore = more
        notifyDataSetChanged()
    }
}

class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(hasMore: Boolean) {
        if (hasMore) {
            binding.root.visibility = View.VISIBLE
            binding.progressBar.show()
        } else
            binding.root.visibility = View.GONE
    }
}