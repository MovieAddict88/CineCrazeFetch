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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Log.d(TAG, "Starting hybrid data fetch - JSON first, TMDB for enhancement...");
        
        // Use GitHub JSON as primary source (contains actual streaming URLs)
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    Log.d(TAG, "Successfully fetched JSON data, length: " + jsonData.length());
                    Data data = parseHomeDataFromJson(jsonData);
                    
                    Log.d(TAG, "Parsed data: " + (data.getPosters() != null ? data.getPosters().size() : 0) + " posters, " + (data.getChannels() != null ? data.getChannels().size() : 0) + " channels");
                    
                    // Enhance movies with TMDB metadata (for missing posters, descriptions, etc.)
                    if (data.getPosters() != null && data.getPosters().size() > 0) {
                        enhanceMoviesWithTMDBMetadata(data.getPosters(), new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Enhanced " + data.getPosters().size() + " movies with TMDB metadata");
                                callback.onSuccess(data);
                            }
                        });
                    } else {
                        // If no movies found, add some TMDB movies with placeholder sources
                        addTMDBMoviesAsFallback(data, callback);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch JSON data, falling back to TMDB only");
                    // Complete fallback to TMDB if JSON fails
                    TMDBService.getInstance().getHomeData(new TMDBService.HomeDataCallback() {
                        @Override
                        public void onSuccess(Data tmdbData) {
                            addPlaceholderSourcesToTMDBMovies(tmdbData);
                            callback.onSuccess(tmdbData);
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError("All data sources failed: " + error);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching hybrid data", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }
    
    private static void addTMDBMoviesAsFallback(Data data, HomeDataCallback callback) {
        TMDBService.getInstance().getPopularMovies(1, new TMDBService.MovieListCallback() {
            @Override
            public void onSuccess(List<Poster> tmdbMovies) {
                if (tmdbMovies != null && !tmdbMovies.isEmpty()) {
                    // Add placeholder sources to TMDB movies
                    for (Poster movie : tmdbMovies) {
                        addPlaceholderSources(movie);
                    }
                    data.setPosters(tmdbMovies.subList(0, Math.min(10, tmdbMovies.size())));
                    Log.d(TAG, "Added " + data.getPosters().size() + " TMDB movies as fallback");
                }
                callback.onSuccess(data);
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "TMDB fallback also failed: " + error);
                callback.onSuccess(data); // Return with whatever we have
            }
        });
    }
    
    private static void addPlaceholderSourcesToTMDBMovies(Data tmdbData) {
        if (tmdbData.getPosters() != null) {
            for (Poster movie : tmdbData.getPosters()) {
                addPlaceholderSources(movie);
            }
        }
    }
    
    private static void addPlaceholderSources(Poster movie) {
        List<Source> sources = new ArrayList<>();
        Source source = new Source();
        source.setTitle("HD Stream");
        source.setUrl("https://vidsrc.me/embed/movie?imdb=" + movie.getImdb());
        source.setType("stream");
        source.setQuality("720p");
        source.setSize("0");
        source.setKind("stream");
        source.setPremium("0");
        source.setExternal(false);
        sources.add(source);
        movie.setSources(sources);
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
        // Get genres from GitHub JSON data
        getGenresFromJson(callback);
    }

    public static void discoverMovies(String genres, String sortBy, int page, MovieListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<Poster> movies = parseMoviesFromJson(jsonData, genres, sortBy, page);
                    // Enhance with TMDB metadata only for missing information
                    enhanceMoviesWithTMDBMetadata(movies, () -> {
                        callback.onSuccess(movies);
                    });
                } else {
                    callback.onError("Failed to fetch GitHub JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching movies from GitHub JSON", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public static void discoverTVShows(String genres, String sortBy, int page, MovieListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<Poster> tvSeries = parseTVSeriesFromJson(jsonData, genres, sortBy, page);
                    // Enhance with TMDB metadata only for missing information
                    enhanceMoviesWithTMDBMetadata(tvSeries, () -> {
                        callback.onSuccess(tvSeries);
                    });
                } else {
                    callback.onError("Failed to fetch GitHub JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching TV series from GitHub JSON", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
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
        List<my.cinemax.app.free.entity.Channel> channels = new ArrayList<>();

        if (jsonObject.has("Categories")) {
            Log.d(TAG, "Found Categories in JSON, parsing streaming data...");
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if (category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        
                        if ("Movies".equals(mainCategory)) {
                            // Parse Movies as individual movies with streaming sources
                            Poster poster = parseMovieEntry(entry);
                            if (poster != null) {
                                posters.add(poster);
                                Log.d(TAG, "Added movie: " + poster.getTitle());
                            }
                        } else if ("TV Series".equals(mainCategory)) {
                            // Parse TV Series as shows with streaming sources
                            Poster poster = parseTVSeriesEntry(entry);
                            if (poster != null) {
                                posters.add(poster);
                                Log.d(TAG, "Added TV series: " + poster.getTitle());
                            }
                        } else if ("Live TV".equals(mainCategory)) {
                            // Parse Live TV as channels
                            my.cinemax.app.free.entity.Channel channel = parseLiveTVEntry(entry);
                            if (channel != null) {
                                channels.add(channel);
                                Log.d(TAG, "Added live channel: " + channel.getTitle());
                            }
                        }
                    }
                }
            }
            
            Log.d(TAG, "Parsed " + posters.size() + " movies/series and " + channels.size() + " live channels");
        }

        data.setPosters(posters);
        data.setChannels(channels);
        return data;
    }

    private static Poster parseMovieEntry(JSONObject entry) throws JSONException {
        Poster poster = new Poster();

        // Set basic movie information
        poster.setTitle(entry.optString("Title", "Unknown Movie"));
        poster.setDescription(entry.optString("Description", ""));
        poster.setImage(entry.optString("Poster", ""));
        poster.setCover(entry.optString("Thumbnail", ""));
        poster.setRating((float) entry.optDouble("Rating", 0.0f));
        poster.setYear(String.valueOf(entry.optInt("Year", 2024)));
        poster.setDuration(entry.optString("Duration", "120 min"));
        poster.setType("movie");
        poster.setPlayas("movie");
        poster.setDownloadas("movie");
        poster.setComment(true);

        // Parse streaming sources directly from movie entry
        List<Source> sources = new ArrayList<>();
        if (entry.has("Servers")) {
            JSONArray serversArray = entry.getJSONArray("Servers");
            for (int i = 0; i < serversArray.length(); i++) {
                JSONObject serverObj = serversArray.getJSONObject(i);
                Source source = parseSourceFromServer(serverObj);
                if (source != null) {
                    sources.add(source);
                }
            }
        }
        poster.setSources(sources);

        return poster;
    }

    private static Poster parseTVSeriesEntry(JSONObject entry) throws JSONException {
        Poster poster = new Poster();

        // Set basic series information
        poster.setTitle(entry.optString("Title", "Unknown Series"));
        poster.setDescription(entry.optString("Description", ""));
        poster.setImage(entry.optString("Poster", ""));
        poster.setCover(entry.optString("Thumbnail", ""));
        poster.setRating((float) entry.optDouble("Rating", 0.0f));
        poster.setYear(String.valueOf(entry.optInt("Year", 2024)));
        poster.setType("serie");
        poster.setPlayas("serie");
        poster.setDownloadas("serie");
        poster.setComment(true);

        // Parse streaming sources from first episode of first season
        List<Source> sources = new ArrayList<>();
        if (entry.has("Seasons")) {
            JSONArray seasonsArray = entry.getJSONArray("Seasons");
            if (seasonsArray.length() > 0) {
                JSONObject firstSeason = seasonsArray.getJSONObject(0);
                if (firstSeason.has("Episodes")) {
                    JSONArray episodesArray = firstSeason.getJSONArray("Episodes");
                    if (episodesArray.length() > 0) {
                        JSONObject firstEpisode = episodesArray.getJSONObject(0);
                        if (firstEpisode.has("Servers")) {
                            JSONArray serversArray = firstEpisode.getJSONArray("Servers");
                            for (int i = 0; i < serversArray.length(); i++) {
                                JSONObject serverObj = serversArray.getJSONObject(i);
                                Source source = parseSourceFromServer(serverObj);
                                if (source != null) {
                                    sources.add(source);
                                }
                            }
                        }
                    }
                }
            }
        }
        poster.setSources(sources);

        return poster;
    }

    private static my.cinemax.app.free.entity.Channel parseLiveTVEntry(JSONObject entry) throws JSONException {
        my.cinemax.app.free.entity.Channel channel = new my.cinemax.app.free.entity.Channel();

        // Set basic channel information
        channel.setTitle(entry.optString("Title", "Unknown Channel"));
        channel.setDescription(entry.optString("Description", ""));
        channel.setImage(entry.optString("Poster", ""));
        channel.setRating((float) entry.optDouble("Rating", 0.0f));
        
        // Parse streaming sources
        List<Source> sources = new ArrayList<>();
        if (entry.has("Servers")) {
            JSONArray serversArray = entry.getJSONArray("Servers");
            for (int i = 0; i < serversArray.length(); i++) {
                JSONObject serverObj = serversArray.getJSONObject(i);
                Source source = parseSourceFromServer(serverObj);
                if (source != null) {
                    sources.add(source);
                }
            }
        }
        channel.setSources(sources);

        return channel;
    }

    private static Source parseSourceFromServer(JSONObject serverObj) {
        Source source = new Source();

        String quality = serverObj.optString("name", "HD");
        source.setTitle(quality);
        source.setUrl(serverObj.optString("url", ""));
        source.setType("stream");
        source.setQuality(quality);
        source.setSize("0");
        source.setKind("stream");
        source.setPremium("0");
        source.setExternal(false);

        return source;
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

    private static Poster parsePosterFromJson(JSONObject movieObj) throws JSONException {
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
    }

    private static Source parseSourceFromJson(JSONObject sourceObj) throws JSONException {
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
    }

    private static List<Poster> parseMoviesFromJson(String jsonData, String genreFilter, String sortBy, int page) throws JSONException {
        List<Poster> movies = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (jsonObject.has("Categories")) {
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if ("Movies".equals(mainCategory) && category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        String subCategory = entry.optString("SubCategory", "");
                        
                        // Filter by genre if specified
                        if (genreFilter == null || genreFilter.isEmpty() || genreFilter.equals("0") ||
                            subCategory.toLowerCase().contains(genreFilter.toLowerCase())) {
                            
                            Poster poster = parseMovieEntry(entry);
                            if (poster != null && poster.getSources() != null && !poster.getSources().isEmpty()) {
                                movies.add(poster);
                            }
                        }
                    }
                }
            }
        }

        // Apply sorting
        sortMovies(movies, sortBy);
        
        // Apply pagination
        int startIndex = (page - 1) * 20;
        int endIndex = Math.min(startIndex + 20, movies.size());
        
        if (startIndex < movies.size()) {
            return movies.subList(startIndex, endIndex);
        }
        
        return new ArrayList<>();
    }

    private static List<Poster> parseTVSeriesFromJson(String jsonData, String genreFilter, String sortBy, int page) throws JSONException {
        List<Poster> tvSeries = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (jsonObject.has("Categories")) {
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if ("TV Series".equals(mainCategory) && category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        String subCategory = entry.optString("SubCategory", "");
                        
                        // Filter by genre if specified
                        if (genreFilter == null || genreFilter.isEmpty() || genreFilter.equals("0") ||
                            subCategory.toLowerCase().contains(genreFilter.toLowerCase())) {
                            
                            Poster poster = parseTVSeriesEntry(entry);
                            if (poster != null && poster.getSources() != null && !poster.getSources().isEmpty()) {
                                tvSeries.add(poster);
                            }
                        }
                    }
                }
            }
        }

        // Apply sorting
        sortMovies(tvSeries, sortBy);
        
        // Apply pagination
        int startIndex = (page - 1) * 20;
        int endIndex = Math.min(startIndex + 20, tvSeries.size());
        
        if (startIndex < tvSeries.size()) {
            return tvSeries.subList(startIndex, endIndex);
        }
        
        return new ArrayList<>();
    }

    private static void sortMovies(List<Poster> movies, String sortBy) {
        if (movies == null || movies.isEmpty()) return;
        
        switch (sortBy) {
            case "title":
            case "name.asc":
            case "title.asc":
                movies.sort((a, b) -> {
                    String titleA = a.getTitle() != null ? a.getTitle() : "";
                    String titleB = b.getTitle() != null ? b.getTitle() : "";
                    return titleA.compareToIgnoreCase(titleB);
                });
                break;
            case "year":
            case "release_date.desc":
            case "first_air_date.desc":
                movies.sort((a, b) -> {
                    String yearA = a.getYear() != null ? a.getYear() : "0";
                    String yearB = b.getYear() != null ? b.getYear() : "0";
                    return yearB.compareTo(yearA); // Descending
                });
                break;
            case "rating":
            case "vote_average.desc":
                movies.sort((a, b) -> {
                    Float ratingA = a.getRating() != null ? a.getRating() : 0f;
                    Float ratingB = b.getRating() != null ? b.getRating() : 0f;
                    return ratingB.compareTo(ratingA); // Descending
                });
                break;
            case "created":
            case "popularity.desc":
            default:
                // Keep original order (most recent first in JSON)
                break;
        }
    }

    public static void getChannelsByFilters(int categoryId, int countryId, int page, ChannelListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<my.cinemax.app.free.entity.Channel> channels = parseChannelsFromJson(jsonData, categoryId, countryId, page);
                    callback.onSuccess(channels);
                } else {
                    callback.onError("Failed to fetch GitHub JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching channels from GitHub JSON", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public interface ChannelListCallback {
        void onSuccess(List<my.cinemax.app.free.entity.Channel> channels);
        void onError(String error);
    }

    private static List<my.cinemax.app.free.entity.Channel> parseChannelsFromJson(String jsonData, int categoryFilter, int countryFilter, int page) throws JSONException {
        List<my.cinemax.app.free.entity.Channel> channels = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (jsonObject.has("Categories")) {
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if ("Live TV".equals(mainCategory) && category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        String subCategory = entry.optString("SubCategory", "");
                        
                        // Simple filtering - for now just include all channels
                        // TODO: Implement proper category and country filtering
                        my.cinemax.app.free.entity.Channel channel = parseLiveTVEntry(entry);
                        if (channel != null && channel.getSources() != null && !channel.getSources().isEmpty()) {
                            channels.add(channel);
                        }
                    }
                }
            }
        }

        // Apply pagination
        int startIndex = (page - 1) * 20;
        int endIndex = Math.min(startIndex + 20, channels.size());
        
        if (startIndex < channels.size()) {
            return channels.subList(startIndex, endIndex);
        }
        
        return new ArrayList<>();
    }

    public static void getGenresFromJson(GenreListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<Genre> genres = extractGenresFromJson(jsonData);
                    callback.onSuccess(genres);
                } else {
                    callback.onError("Failed to fetch GitHub JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting genres from GitHub JSON", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    private static List<Genre> extractGenresFromJson(String jsonData) throws JSONException {
        List<Genre> genres = new ArrayList<>();
        Set<String> genreNames = new HashSet<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (jsonObject.has("Categories")) {
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if (("Movies".equals(mainCategory) || "TV Series".equals(mainCategory)) && category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        String subCategory = entry.optString("SubCategory", "");
                        
                        if (!subCategory.isEmpty() && !genreNames.contains(subCategory)) {
                            genreNames.add(subCategory);
                            Genre genre = new Genre();
                            genre.setId(genreNames.size());
                            genre.setTitle(subCategory);
                            genres.add(genre);
                        }
                    }
                }
                
                // Also extract from SubCategories array if present
                if (category.has("SubCategories")) {
                    JSONArray subCategoriesArray = category.getJSONArray("SubCategories");
                    for (int k = 0; k < subCategoriesArray.length(); k++) {
                        String subCategory = subCategoriesArray.getString(k);
                        if (!subCategory.isEmpty() && !genreNames.contains(subCategory)) {
                            genreNames.add(subCategory);
                            Genre genre = new Genre();
                            genre.setId(genreNames.size());
                            genre.setTitle(subCategory);
                            genres.add(genre);
                        }
                    }
                }
            }
        }

        // Add a default "All" genre at the beginning
        Genre allGenre = new Genre();
        allGenre.setId(0);
        allGenre.setTitle("All");
        genres.add(0, allGenre);

        return genres;
    }

    public static void getLiveTVCategories(CategoryListCallback callback) {
        executor.execute(() -> {
            try {
                String jsonData = fetchJsonFromUrl(JSON_URL);
                if (jsonData != null) {
                    List<my.cinemax.app.free.entity.Category> categories = extractCategoriesFromJson(jsonData);
                    callback.onSuccess(categories);
                } else {
                    callback.onError("Failed to fetch GitHub JSON data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting categories from GitHub JSON", e);
                callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public interface CategoryListCallback {
        void onSuccess(List<my.cinemax.app.free.entity.Category> categories);
        void onError(String error);
    }

    private static List<my.cinemax.app.free.entity.Category> extractCategoriesFromJson(String jsonData) throws JSONException {
        List<my.cinemax.app.free.entity.Category> categories = new ArrayList<>();
        Set<String> categoryNames = new HashSet<>();
        JSONObject jsonObject = new JSONObject(jsonData);

        if (jsonObject.has("Categories")) {
            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                String mainCategory = category.optString("MainCategory", "");
                
                if ("Live TV".equals(mainCategory) && category.has("Entries")) {
                    JSONArray entries = category.getJSONArray("Entries");
                    
                    for (int j = 0; j < entries.length(); j++) {
                        JSONObject entry = entries.getJSONObject(j);
                        String subCategory = entry.optString("SubCategory", "");
                        
                        if (!subCategory.isEmpty() && !categoryNames.contains(subCategory)) {
                            categoryNames.add(subCategory);
                            my.cinemax.app.free.entity.Category cat = new my.cinemax.app.free.entity.Category();
                            cat.setId(categoryNames.size());
                            cat.setTitle(subCategory);
                            categories.add(cat);
                        }
                    }
                }
            }
        }

        // Add a default "All" category at the beginning
        my.cinemax.app.free.entity.Category allCategory = new my.cinemax.app.free.entity.Category();
        allCategory.setId(0);
        allCategory.setTitle("All");
        categories.add(0, allCategory);

        return categories;
    }
}