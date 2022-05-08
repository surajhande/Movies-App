package com.suraj.moviesapp.model



import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorites_table")
@JsonClass(generateAdapter = true)
data class MovieItemDTO(
    @Json(name = "Title") val title: String,
    @Json(name = "Year") val year: String,
    @PrimaryKey
    @Json(name = "imdbID") val id: String,
    @Json(name = "Poster") val posterUrl: String,
    var isFavorite: Boolean = false
) : Parcelable

@JsonClass(generateAdapter = true)
data class MovieItemResponse(
    @Json(name = "Search") val search: List<MovieItemDTO>,
    @Json(name = "totalResults") val totalResults: Int,
    @Json(name = "Response") val response: String
)


@JsonClass(generateAdapter = true)
data class MovieDetailsDTO(
    @Json(name = "imdbID") val id: String,
    @Json(name = "Title") val title: String,
    @Json(name = "Poster") val posterUrl: String,
    @Json(name = "Year") val year: String,
    @Json(name = "Plot") val plot: String,
    @Json(name = "Rated") val rated: String,
    @Json(name = "Director") val director: String,
    @Json(name = "Actors") val cast: String,
    var isFavorite: Boolean = false
) {
    fun convertToMovieItem(): MovieItemDTO = MovieItemDTO(title, year, id, posterUrl, isFavorite)
}