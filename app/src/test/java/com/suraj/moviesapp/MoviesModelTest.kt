package com.suraj.moviesapp

import com.squareup.moshi.Moshi
import com.suraj.moviesapp.model.MovieItemDTO
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.intellij.lang.annotations.Language
import org.junit.Test

class MoviesModelTest {

    companion object {
        internal val MOVIE_ITEM_DTO = MovieItemDTO(
            "Batman Begins",
            "2005",
            "tt0372784",
            "https://m.media-amazon.com/images/M/MV5BOTY4YjI2N2MtYmFlMC00ZjcyLTg3YjEtMDQyM2ZjYzQ5YWFkXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg",
            false
        )
    }

    @Language("JSON")
    private val movieItemResponse = """
    {
        "Title": "Batman Begins",
        "Year": "2005",
        "imdbID": "tt0372784",
        "Type": "movie",
        "Poster": "https://m.media-amazon.com/images/M/MV5BOTY4YjI2N2MtYmFlMC00ZjcyLTg3YjEtMDQyM2ZjYzQ5YWFkXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg"  
    }""".trimIndent().trimMargin()


    private val moshi = Moshi.Builder().build()
    private val movieItemAdapter = moshi.adapter(MovieItemDTO::class.java)


    @Test
    fun `Generate MovieItemDTO object from JSON response`() {
        val movieItemDTO = movieItemAdapter.fromJson(movieItemResponse)
        assertThat(movieItemDTO?.id, equalTo(MOVIE_ITEM_DTO.id))
        assertThat(movieItemDTO?.title, equalTo(MOVIE_ITEM_DTO.title))
        assertThat(movieItemDTO?.year, equalTo(MOVIE_ITEM_DTO.year))
        assertThat(movieItemDTO?.posterUrl, equalTo(MOVIE_ITEM_DTO.posterUrl))
    }
}