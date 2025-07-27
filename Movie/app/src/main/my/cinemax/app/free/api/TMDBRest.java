package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.TMDBMovie;
import my.cinemax.app.free.entity.TMDBMovieResponse;
import my.cinemax.app.free.entity.TMDBGenreResponse;
import my.cinemax.app.free.entity.TMDBMovieDetail;
import my.cinemax.app.free.entity.TMDBVideoResponse;
import my.cinemax.app.free.entity.TMDBTvShow;
import my.cinemax.app.free.entity.TMDBTvShowResponse;
import my.cinemax.app.free.entity.TMDBTvShowDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TMDB API Interface
 */
public interface TMDBRest {

    // Movies
    @GET("movie/popular")
    Call<TMDBMovieResponse> getPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Call<TMDBMovieResponse> getTopRatedMovies(@Query("page") int page);

    @GET("movie/now_playing")
    Call<TMDBMovieResponse> getNowPlayingMovies(@Query("page") int page);

    @GET("movie/upcoming")
    Call<TMDBMovieResponse> getUpcomingMovies(@Query("page") int page);

    @GET("movie/{movie_id}")
    Call<TMDBMovieDetail> getMovieDetail(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/videos")
    Call<TMDBVideoResponse> getMovieVideos(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/similar")
    Call<TMDBMovieResponse> getSimilarMovies(@Path("movie_id") int movieId, @Query("page") int page);

    @GET("movie/{movie_id}/recommendations")
    Call<TMDBMovieResponse> getMovieRecommendations(@Path("movie_id") int movieId, @Query("page") int page);

    @GET("search/movie")
    Call<TMDBMovieResponse> searchMovies(@Query("query") String query, @Query("page") int page);

    @GET("discover/movie")
    Call<TMDBMovieResponse> discoverMovies(
            @Query("with_genres") String genres,
            @Query("sort_by") String sortBy,
            @Query("page") int page
    );

    // TV Shows
    @GET("tv/popular")
    Call<TMDBTvShowResponse> getPopularTvShows(@Query("page") int page);

    @GET("tv/top_rated")
    Call<TMDBTvShowResponse> getTopRatedTvShows(@Query("page") int page);

    @GET("tv/on_the_air")
    Call<TMDBTvShowResponse> getOnTheAirTvShows(@Query("page") int page);

    @GET("tv/airing_today")
    Call<TMDBTvShowResponse> getAiringTodayTvShows(@Query("page") int page);

    @GET("tv/{tv_id}")
    Call<TMDBTvShowDetail> getTvShowDetail(@Path("tv_id") int tvId);

    @GET("tv/{tv_id}/videos")
    Call<TMDBVideoResponse> getTvShowVideos(@Path("tv_id") int tvId);

    @GET("tv/{tv_id}/similar")
    Call<TMDBTvShowResponse> getSimilarTvShows(@Path("tv_id") int tvId, @Query("page") int page);

    @GET("tv/{tv_id}/recommendations")
    Call<TMDBTvShowResponse> getTvShowRecommendations(@Path("tv_id") int tvId, @Query("page") int page);

    @GET("search/tv")
    Call<TMDBTvShowResponse> searchTvShows(@Query("query") String query, @Query("page") int page);

    @GET("discover/tv")
    Call<TMDBTvShowResponse> discoverTvShows(
            @Query("with_genres") String genres,
            @Query("sort_by") String sortBy,
            @Query("page") int page
    );

    // Genres
    @GET("genre/movie/list")
    Call<TMDBGenreResponse> getMovieGenres();

    @GET("genre/tv/list")
    Call<TMDBGenreResponse> getTvGenres();

    // Multi Search
    @GET("search/multi")
    Call<TMDBMovieResponse> searchMulti(@Query("query") String query, @Query("page") int page);
}