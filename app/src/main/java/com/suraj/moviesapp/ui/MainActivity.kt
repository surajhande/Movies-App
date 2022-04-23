package com.suraj.moviesapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.suraj.moviesapp.R
import com.suraj.moviesapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, HostFragment.newInstance())
            .commit()
    }
}