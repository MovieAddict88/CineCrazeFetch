package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.TMDBGenre;
import my.cinemax.app.free.entity.TMDBMovie;
import my.cinemax.app.free.entity.TMDBMovieDetail;
import my.cinemax.app.free.entity.TMDBMovieResponse;
import my.cinemax.app.free.entity.TMDBTvShow;
import my.cinemax.app.free.entity.TMDBTvShowDetail;
import my.cinemax.app.free.entity.TMDBTvShowResponse;
import my.cinemax.app.free.entity.TMDBVideo;
import my.cinemax.app.free.entity.TMDBVideoResponse;
import my.cinemax.app.free.util.TMDBDataConverter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service class to handle TMDB API calls and data conversion
 */
public class TMDBService {
    private static TMDBService instance;
    private TMDBRest tmdbRest;

    private TMDBService() {
        try {
            tmdbRest = TMDBClient.getClient().create(TMDBRest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TMDBService getInstance() {
        if (instance == null) {
            instance = new TMDBService();
        }
        return instance;
    }

    public interface HomeDataCallback {
        void onSuccess(Data data);
        void onError(String error);
    }

    public interface MovieListCallback {
        void onSuccess(List<Poster> movies);
        void onError(String error);
    }

    public interface MovieDetailCallback {
        void onSuccess(Poster movie);
        void onError(String error);
    }

    public interface GenreListCallback {
        void onSuccess(List<Genre> genres);
        void onError(String error);
    }

    public interface SearchCallback {
        void onSuccess(List<Poster> results);
        void onError(String error);
    }

    /**
     * Get home data with popular movies and TV shows
     */
    public void getHomeData(HomeDataCallback callback) {
        android.util.Log.d("TMDBService", "Starting to fetch home data...");
        // Get popular movies
        tmdbRest.getPopularMovies(1).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                android.util.Log.d("TMDBService", "Popular movies response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<TMDBMovie> popularMovies = response.body().getResults();
                    android.util.Log.d("TMDBService", "Got " + popularMovies.size() + " popular movies");
                    
                    // Get popular TV shows
                    tmdbRest.getPopularTvShows(1).enqueue(new Callback<TMDBTvShowResponse>() {
                        @Override
                        public void onResponse(Call<TMDBTvShowResponse> call, Response<TMDBTvShowResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<TMDBTvShow> popularTvShows = response.body().getResults();
                                
                                // Convert and combine data
                                Data data = new Data();
                                List<Poster> allPosters = new ArrayList<>();
                                
                                // Add movies
                                for (TMDBMovie movie : popularMovies) {
                                    allPosters.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                                }
                                
                                // Add TV shows
                                for (TMDBTvShow tvShow : popularTvShows) {
                                    allPosters.add(TMDBDataConverter.convertTMDBTvShowToPoster(tvShow));
                                }
                                
                                data.setPosters(allPosters);
                                callback.onSuccess(data);
                            } else {
                                callback.onError("Failed to get TV shows");
                            }
                        }

                        @Override
                        public void onFailure(Call<TMDBTvShowResponse> call, Throwable t) {
                            callback.onError("Network error: " + t.getMessage());
                        }
                    });
                } else {
                    callback.onError("Failed to get movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                android.util.Log.e("TMDBService", "Popular movies error: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get popular movies
     */
    public void getPopularMovies(int page, MovieListCallback callback) {
        tmdbRest.getPopularMovies(page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        movies.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Failed to get popular movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get top rated movies
     */
    public void getTopRatedMovies(int page, MovieListCallback callback) {
        tmdbRest.getTopRatedMovies(page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        movies.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Failed to get top rated movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get now playing movies
     */
    public void getNowPlayingMovies(int page, MovieListCallback callback) {
        tmdbRest.getNowPlayingMovies(page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        movies.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Failed to get now playing movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get upcoming movies
     */
    public void getUpcomingMovies(int page, MovieListCallback callback) {
        tmdbRest.getUpcomingMovies(page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        movies.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Failed to get upcoming movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Discover movies with filters
     */
    public void discoverMovies(String genres, String sortBy, int page, MovieListCallback callback) {
        tmdbRest.discoverMovies(genres, sortBy, page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        movies.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Failed to discover movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get popular TV shows
     */
    public void getPopularTvShows(int page, MovieListCallback callback) {
        tmdbRest.getPopularTvShows(page).enqueue(new Callback<TMDBTvShowResponse>() {
            @Override
            public void onResponse(Call<TMDBTvShowResponse> call, Response<TMDBTvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> tvShows = new ArrayList<>();
                    for (TMDBTvShow tvShow : response.body().getResults()) {
                        tvShows.add(TMDBDataConverter.convertTMDBTvShowToPoster(tvShow));
                    }
                    callback.onSuccess(tvShows);
                } else {
                    callback.onError("Failed to get popular TV shows");
                }
            }

            @Override
            public void onFailure(Call<TMDBTvShowResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get top rated TV shows
     */
    public void getTopRatedTvShows(int page, MovieListCallback callback) {
        tmdbRest.getTopRatedTvShows(page).enqueue(new Callback<TMDBTvShowResponse>() {
            @Override
            public void onResponse(Call<TMDBTvShowResponse> call, Response<TMDBTvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> tvShows = new ArrayList<>();
                    for (TMDBTvShow tvShow : response.body().getResults()) {
                        tvShows.add(TMDBDataConverter.convertTMDBTvShowToPoster(tvShow));
                    }
                    callback.onSuccess(tvShows);
                } else {
                    callback.onError("Failed to get top rated TV shows");
                }
            }

            @Override
            public void onFailure(Call<TMDBTvShowResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get movie detail
     */
    public void getMovieDetail(int movieId, MovieDetailCallback callback) {
        tmdbRest.getMovieDetail(movieId).enqueue(new Callback<TMDBMovieDetail>() {
            @Override
            public void onResponse(Call<TMDBMovieDetail> call, Response<TMDBMovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Poster movie = TMDBDataConverter.convertTMDBMovieDetailToPoster(response.body());
                    callback.onSuccess(movie);
                } else {
                    callback.onError("Failed to get movie detail");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieDetail> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get TV show detail
     */
    public void getTvShowDetail(int tvShowId, MovieDetailCallback callback) {
        tmdbRest.getTvShowDetail(tvShowId).enqueue(new Callback<TMDBTvShowDetail>() {
            @Override
            public void onResponse(Call<TMDBTvShowDetail> call, Response<TMDBTvShowDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Poster tvShow = TMDBDataConverter.convertTMDBTvShowDetailToPoster(response.body());
                    callback.onSuccess(tvShow);
                } else {
                    callback.onError("Failed to get TV show detail");
                }
            }

            @Override
            public void onFailure(Call<TMDBTvShowDetail> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get movie genres
     */
    public void getMovieGenres(GenreListCallback callback) {
        tmdbRest.getMovieGenres().enqueue(new Callback<my.cinemax.app.free.entity.TMDBGenreResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.TMDBGenreResponse> call, Response<my.cinemax.app.free.entity.TMDBGenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = new ArrayList<>();
                    for (TMDBGenre tmdbGenre : response.body().getGenres()) {
                        genres.add(TMDBDataConverter.convertTMDBGenreToGenre(tmdbGenre));
                    }
                    callback.onSuccess(genres);
                } else {
                    callback.onError("Failed to get genres");
                }
            }

            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.TMDBGenreResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Search movies
     */
    public void searchMovies(String query, int page, SearchCallback callback) {
        tmdbRest.searchMovies(query, page).enqueue(new Callback<TMDBMovieResponse>() {
            @Override
            public void onResponse(Call<TMDBMovieResponse> call, Response<TMDBMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> results = new ArrayList<>();
                    for (TMDBMovie movie : response.body().getResults()) {
                        results.add(TMDBDataConverter.convertTMDBMovieToPoster(movie));
                    }
                    callback.onSuccess(results);
                } else {
                    callback.onError("Failed to search movies");
                }
            }

            @Override
            public void onFailure(Call<TMDBMovieResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Search TV shows
     */
    public void searchTvShows(String query, int page, SearchCallback callback) {
        tmdbRest.searchTvShows(query, page).enqueue(new Callback<TMDBTvShowResponse>() {
            @Override
            public void onResponse(Call<TMDBTvShowResponse> call, Response<TMDBTvShowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> results = new ArrayList<>();
                    for (TMDBTvShow tvShow : response.body().getResults()) {
                        results.add(TMDBDataConverter.convertTMDBTvShowToPoster(tvShow));
                    }
                    callback.onSuccess(results);
                } else {
                    callback.onError("Failed to search TV shows");
                }
            }

            @Override
            public void onFailure(Call<TMDBTvShowResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Get movie videos (trailers)
     */
    public void getMovieVideos(int movieId, Callback<TMDBVideoResponse> callback) {
        tmdbRest.getMovieVideos(movieId).enqueue(callback);
    }

    /**
     * Get TV show videos (trailers)
     */
    public void getTvShowVideos(int tvShowId, Callback<TMDBVideoResponse> callback) {
        tmdbRest.getTvShowVideos(tvShowId).enqueue(callback);
    }
}