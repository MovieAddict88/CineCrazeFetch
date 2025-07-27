package my.cinemax.app.free.api;

import android.util.Log;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;
import my.cinemax.app.free.entity.TMDBMovie;
import my.cinemax.app.free.entity.TMDBMovieResponse;
import my.cinemax.app.free.entity.TMDBGenre;
import my.cinemax.app.free.entity.TMDBGenreResponse;
import my.cinemax.app.free.util.TMDBDataConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hybrid Data Service that combines GitHub JSON for streaming sources and TMDB for metadata
 */
public class HybridDataService {
    private static final String TAG = "HybridDataService";
    private static final String JSON_URL = "https://github.com/MovieAddict88/Movie-Source/raw/main/playlist.json";
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public interface HomeDataCallback {
        void onSuccess(Data data);
        void onError(String error);
    }

    public interface MovieListCallback {
        void onSuccess(List<Poster> movies);
        void onError(String error);
    }

    public interface GenreListCallback {
        void onSuccess(List<Genre> genres);
        void onError(String error);
    }

    public static void getHomeData(HomeDataCallback callback) {
        Log.d(TAG, "Starting hybrid data fetch...");
        executor.execute(() -> {
            try {
                // First, fetch data from GitHub JSON
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    Log.d(TAG, "Successfully fetched JSON data, length: " + jsonData.length());
                    Data data = parseHomeDataFromJson(jsonData);
                    
                    // If we have movies, enhance them with TMDB metadata
                    if (data.getPosters() != null && data.getPosters().size() > 0) {
                        enhanceMoviesWithTMDBMetadata(data.getPosters(), new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Enhanced " + data.getPosters().size() + " movies with TMDB metadata");
                                callback.onSuccess(data);
                            }
                        });
                    } else {
                        Log.w(TAG, "No movies found in JSON data");
                        callback.onSuccess(data);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch JSON data - null response");
                    callback.onError("Failed to fetch JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching hybrid data", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public static void getPopularMovies(int page, MovieListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<Poster> movies = parseMoviesFromJson(jsonData);
                    enhanceMoviesWithTMDBMetadata(movies, new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(movies);
                        }
                    });
                } else {
                    callback.onError("Failed to fetch JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching movies", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public static void getMovieGenres(GenreListCallback callback) {
        // Try TMDB first for genres
        TMDBService.getInstance().getMovieGenres(new TMDBService.GenreListCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                callback.onSuccess(genres);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "TMDB genres failed, using default genres");
                // Fallback to default genres
                List<Genre> defaultGenres = createDefaultGenres();
                callback.onSuccess(defaultGenres);
            }
        });
    }

    public static void discoverMovies(String genres, String sortBy, int page, MovieListCallback callback) {
        // For hybrid approach, we'll get all movies and filter client-side
        getPopularMovies(page, callback);
    }

    private static void enhanceMoviesWithTMDBMetadata(List<Poster> movies, Runnable onComplete) {
        if (movies == null || movies.isEmpty()) {
            onComplete.run();
            return;
        }

        final int[] completedCount = {0};
        final int totalMovies = movies.size();

        for (Poster movie : movies) {
            // Only enhance if missing critical metadata
            if (needsMetadataEnhancement(movie)) {
                searchTMDBForMovie(movie, new Runnable() {
                    @Override
                    public void run() {
                        completedCount[0]++;
                        if (completedCount[0] >= totalMovies) {
                            onComplete.run();
                        }
                    }
                });
            } else {
                completedCount[0]++;
                if (completedCount[0] >= totalMovies) {
                    onComplete.run();
                }
            }
        }
    }

    private static boolean needsMetadataEnhancement(Poster movie) {
        // Check if movie needs metadata enhancement
        return (movie.getDescription() == null || movie.getDescription().isEmpty()) ||
               (movie.getImage() == null || movie.getImage().isEmpty()) ||
               (movie.getRating() == null || movie.getRating() == 0) ||
               (movie.getYear() == null || movie.getYear().isEmpty());
    }

    private static void searchTMDBForMovie(Poster movie, Runnable onComplete) {
        // Search TMDB for movie by title
        TMDBService.getInstance().searchMovies(movie.getTitle(), 1, new TMDBService.SearchCallback() {
            @Override
            public void onSuccess(List<Poster> tmdbMovies) {
                if (tmdbMovies != null && !tmdbMovies.isEmpty()) {
                    Poster tmdbMovie = tmdbMovies.get(0);
                    enhanceMovieWithTMDBData(movie, tmdbMovie);
                }
                onComplete.run();
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "TMDB search failed for: " + movie.getTitle());
                onComplete.run();
            }
        });
    }

    private static void enhanceMovieWithTMDBData(Poster originalMovie, Poster tmdbMovie) {
        // Enhance original movie with TMDB data, but keep original sources
        if (originalMovie.getDescription() == null || originalMovie.getDescription().isEmpty()) {
            originalMovie.setDescription(tmdbMovie.getDescription());
        }
        
        if (originalMovie.getImage() == null || originalMovie.getImage().isEmpty()) {
            originalMovie.setImage(tmdbMovie.getImage());
        }
        
        if (originalMovie.getCover() == null || originalMovie.getCover().isEmpty()) {
            originalMovie.setCover(tmdbMovie.getCover());
        }
        
        if (originalMovie.getRating() == null || originalMovie.getRating() == 0) {
            originalMovie.setRating(tmdbMovie.getRating());
        }
        
        if (originalMovie.getYear() == null || originalMovie.getYear().isEmpty()) {
            originalMovie.setYear(tmdbMovie.getYear());
        }
        
        if (originalMovie.getDuration() == null || originalMovie.getDuration().isEmpty()) {
            originalMovie.setDuration(tmdbMovie.getDuration());
        }
        
        if (originalMovie.getGenres() == null || originalMovie.getGenres().isEmpty()) {
            originalMovie.setGenres(tmdbMovie.getGenres());
        }
    }

    private static String fetchJsonFromUrl(String urlString) {
        HttpURLConnection connection = null;
        try {
            Log.d(TAG, "Connecting to: " + urlString);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "HTTP Response Code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "Successfully read response, length: " + response.length());
                return response.toString();
            } else {
                Log.e(TAG, "HTTP Error: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching JSON", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static Data parseHomeDataFromJson(String jsonData) throws JSONException {
        Log.d(TAG, "Parsing JSON data...");
        JSONObject jsonObject = new JSONObject(jsonData);
        Data data = new Data();
        List<Poster> posters = new ArrayList<>();

        // Parse movies from JSON
        if (jsonObject.has("movies")) {
            JSONArray moviesArray = jsonObject.getJSONArray("movies");
            Log.d(TAG, "Found " + moviesArray.length() + " movies in JSON");
            for (int i = 0; i < Math.min(moviesArray.length(), 20); i++) { // Limit to 20 movies
                JSONObject movieObj = moviesArray.getJSONObject(i);
                Poster poster = parsePosterFromJson(movieObj);
                if (poster != null) {
                    posters.add(poster);
                }
            }
            Log.d(TAG, "Successfully parsed " + posters.size() + " posters");
        } else {
            Log.w(TAG, "No 'movies' key found in JSON");
        }

        data.setPosters(posters);
        return data;
    }

    private static List<Poster> parseMoviesFromJson(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        List<Poster> movies = new ArrayList<>();

        if (jsonObject.has("movies")) {
            JSONArray moviesArray = jsonObject.getJSONArray("movies");
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObj = moviesArray.getJSONObject(i);
                Poster poster = parsePosterFromJson(movieObj);
                if (poster != null) {
                    movies.add(poster);
                }
            }
        }

        return movies;
    }

    private static List<Genre> createDefaultGenres() {
        List<Genre> genres = new ArrayList<>();
        String[] defaultGenres = {"Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller", "Adventure"};
        for (int i = 0; i < defaultGenres.length; i++) {
            Genre genre = new Genre();
            genre.setId(i + 1);
            genre.setTitle(defaultGenres[i]);
            genres.add(genre);
        }
        return genres;
    }

    private static Poster parsePosterFromJson(JSONObject movieObj) {
        try {
            Poster poster = new Poster();
            
            if (movieObj.has("id")) {
                poster.setId(movieObj.getInt("id"));
            }
            
            if (movieObj.has("title")) {
                String title = movieObj.getString("title");
                poster.setTitle(title);
                Log.d(TAG, "Parsed movie: " + title);
            }
            
            if (movieObj.has("overview")) {
                poster.setDescription(movieObj.getString("overview"));
            }
            
            if (movieObj.has("poster_path")) {
                String posterPath = movieObj.getString("poster_path");
                if (posterPath.startsWith("http")) {
                    poster.setImage(posterPath);
                } else {
                    poster.setImage("https://image.tmdb.org/t/p/w500" + posterPath);
                }
            }
            
            if (movieObj.has("backdrop_path")) {
                String backdropPath = movieObj.getString("backdrop_path");
                if (backdropPath.startsWith("http")) {
                    poster.setCover(backdropPath);
                } else {
                    poster.setCover("https://image.tmdb.org/t/p/w500" + backdropPath);
                }
            }
            
            if (movieObj.has("release_date")) {
                poster.setYear(movieObj.getString("release_date"));
            }
            
            if (movieObj.has("vote_average")) {
                poster.setRating(movieObj.getDouble("vote_average").floatValue());
            }
            
            // Parse streaming sources from JSON
            if (movieObj.has("sources")) {
                List<Source> sources = new ArrayList<>();
                JSONArray sourcesArray = movieObj.getJSONArray("sources");
                for (int i = 0; i < sourcesArray.length(); i++) {
                    JSONObject sourceObj = sourcesArray.getJSONObject(i);
                    Source source = parseSourceFromJson(sourceObj);
                    if (source != null) {
                        sources.add(source);
                    }
                }
                poster.setSources(sources);
            }
            
            // Set default values for required fields
            poster.setType("movie");
            poster.setDuration("120 min");
            poster.setYear("2024");
            poster.setPlayas("1");
            poster.setDownloadas("1");
            poster.setComment(true);
            
            return poster;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing movie object", e);
            return null;
        }
    }

    private static Source parseSourceFromJson(JSONObject sourceObj) {
        try {
            Source source = new Source();
            
            if (sourceObj.has("id")) {
                source.setId(sourceObj.getInt("id"));
            }
            
            if (sourceObj.has("title")) {
                source.setTitle(sourceObj.getString("title"));
            }
            
            if (sourceObj.has("url")) {
                source.setUrl(sourceObj.getString("url"));
            }
            
            if (sourceObj.has("type")) {
                source.setType(sourceObj.getString("type"));
            }
            
            if (sourceObj.has("quality")) {
                source.setQuality(sourceObj.getString("quality"));
            }
            
            if (sourceObj.has("size")) {
                source.setSize(sourceObj.getString("size"));
            }
            
            if (sourceObj.has("kind")) {
                source.setKind(sourceObj.getString("kind"));
            }
            
            if (sourceObj.has("premium")) {
                source.setPremium(sourceObj.getString("premium"));
            }
            
            if (sourceObj.has("external")) {
                source.setExternal(sourceObj.getBoolean("external"));
            }
            
            return source;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing source object", e);
            return null;
        }
    }
}