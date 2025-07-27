package my.cinemax.app.free.api;

import android.util.Log;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
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

/**
 * Fallback data service that fetches data from JSON URL when TMDB fails
 */
public class FallbackDataService {
    private static final String TAG = "FallbackDataService";
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
        Log.d(TAG, "Starting fallback data fetch from: " + JSON_URL);
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    Log.d(TAG, "Successfully fetched JSON data, length: " + jsonData.length());
                    Data data = parseHomeDataFromJson(jsonData);
                    Log.d(TAG, "Parsed data successfully, posters count: " + (data.getPosters() != null ? data.getPosters().size() : 0));
                    callback.onSuccess(data);
                } else {
                    Log.e(TAG, "Failed to fetch JSON data - null response");
                    callback.onError("Failed to fetch JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching fallback data", e);
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
                    callback.onSuccess(movies);
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
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<Genre> genres = parseGenresFromJson(jsonData);
                    callback.onSuccess(genres);
                } else {
                    callback.onError("Failed to fetch JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching genres", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
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

    private static List<Genre> parseGenresFromJson(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        List<Genre> genres = new ArrayList<>();

        // Create some default genres based on common movie categories
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
                poster.setRating((float) movieObj.getDouble("vote_average"));
            }
            
            // Set default values for required fields
            poster.setType("movie");
            poster.setDuration("120 min");
            poster.setYear("2024");
            
            return poster;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing movie object", e);
            return null;
        }
    }
}