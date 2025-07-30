package com.example.movieapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    
    // GitHub Pages URL where you upload the JSON file
    private static final String JSON_API_URL = "https://your-username.github.io/your-repo/free_movie_api.json";
    
    private OkHttpClient client;
    private Gson gson;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize
        client = new OkHttpClient();
        gson = new Gson();
        
        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);
        
        // Load data from GitHub JSON
        loadMovieData();
    }
    
    private void loadMovieData() {
        Request request = new Request.Builder()
                .url(JSON_API_URL)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MovieAPI", "Error loading data", e);
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "Error loading data", Toast.LENGTH_SHORT).show()
                );
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    parseMovieData(jsonData);
                }
            }
        });
    }
    
    private void parseMovieData(String jsonData) {
        try {
            // Parse the JSON data
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            
            // Get movies array
            Type movieListType = new TypeToken<List<Movie>>(){}.getType();
            List<Movie> movies = gson.fromJson(jsonObject.get("movies"), movieListType);
            
            // Get home data
            JsonObject homeData = jsonObject.getAsJsonObject("home");
            List<Movie> featuredMovies = gson.fromJson(homeData.get("featured_movies"), movieListType);
            
            // Update UI on main thread
            runOnUiThread(() -> {
                movieAdapter.setMovies(movies);
                Log.d("MovieAPI", "Loaded " + movies.size() + " movies");
            });
            
        } catch (Exception e) {
            Log.e("MovieAPI", "Error parsing JSON", e);
        }
    }
}

// Movie data class
class Movie {
    private int id;
    private String title;
    private String type;
    private String description;
    private String year;
    private float rating;
    private String image;
    private String cover;
    private List<VideoSource> sources;
    private List<Actor> actors;
    private List<Genre> genres;
    private List<Comment> comments;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    
    public List<VideoSource> getSources() { return sources; }
    public void setSources(List<VideoSource> sources) { this.sources = sources; }
    
    public List<Actor> getActors() { return actors; }
    public void setActors(List<Actor> actors) { this.actors = actors; }
    
    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}

// Video source class
class VideoSource {
    private int id;
    private String type;
    private String title;
    private String quality;
    private String size;
    private String kind;
    private String premium;
    private boolean external;
    private String url;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    
    public String getPremium() { return premium; }
    public void setPremium(String premium) { this.premium = premium; }
    
    public boolean isExternal() { return external; }
    public void setExternal(boolean external) { this.external = external; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

// Actor class
class Actor {
    private int id;
    private String name;
    private String type;
    private String role;
    private String image;
    private String born;
    private String height;
    private String bio;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getBorn() { return born; }
    public void setBorn(String born) { this.born = born; }
    
    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}

// Genre class
class Genre {
    private int id;
    private String title;
    private List<Movie> posters;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<Movie> getPosters() { return posters; }
    public void setPosters(List<Movie> posters) { this.posters = posters; }
}

// Comment class
class Comment {
    private int id;
    private String user;
    private String comment;
    private String created_at;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}

// API Service class for better organization
class MovieAPIService {
    private static final String BASE_URL = "https://your-username.github.io/your-repo/free_movie_api.json";
    private OkHttpClient client;
    private Gson gson;
    
    public MovieAPIService() {
        client = new OkHttpClient();
        gson = new Gson();
    }
    
    // Get all movies
    public void getMovies(MovieCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    
                    Type movieListType = new TypeToken<List<Movie>>(){}.getType();
                    List<Movie> movies = gson.fromJson(jsonObject.get("movies"), movieListType);
                    
                    callback.onSuccess(movies);
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }
        });
    }
    
    // Get movie by ID
    public void getMovieById(int movieId, MovieDetailCallback callback) {
        getMovies(new MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                for (Movie movie : movies) {
                    if (movie.getId() == movieId) {
                        callback.onSuccess(movie);
                        return;
                    }
                }
                callback.onError("Movie not found");
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    // Get home data
    public void getHomeData(HomeDataCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    
                    JsonObject homeData = jsonObject.getAsJsonObject("home");
                    callback.onSuccess(homeData);
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }
        });
    }
    
    // Get video sources for a movie
    public void getMovieSources(int movieId, VideoSourceCallback callback) {
        getMovieById(movieId, new MovieDetailCallback() {
            @Override
            public void onSuccess(Movie movie) {
                callback.onSuccess(movie.getSources());
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    // Get live stream sources for a channel
    public void getChannelSources(int channelId, VideoSourceCallback callback) {
        // Similar to getMovieSources but for channels
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    
                    Type channelListType = new TypeToken<List<Channel>>(){}.getType();
                    List<Channel> channels = gson.fromJson(jsonObject.get("channels"), channelListType);
                    
                    for (Channel channel : channels) {
                        if (channel.getId() == channelId) {
                            callback.onSuccess(channel.getSources());
                            return;
                        }
                    }
                    callback.onError("Channel not found");
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }
        });
    }
}

// Channel class
class Channel {
    private int id;
    private String title;
    private String description;
    private String image;
    private List<VideoSource> sources;
    private List<Comment> comments;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public List<VideoSource> getSources() { return sources; }
    public void setSources(List<VideoSource> sources) { this.sources = sources; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}

// Callback interfaces
interface MovieCallback {
    void onSuccess(List<Movie> movies);
    void onError(String error);
}

interface MovieDetailCallback {
    void onSuccess(Movie movie);
    void onError(String error);
}

interface HomeDataCallback {
    void onSuccess(JsonObject homeData);
    void onError(String error);
}

interface VideoSourceCallback {
    void onSuccess(List<VideoSource> sources);
    void onError(String error);
}

// Example usage in Activity
class MovieListActivity extends AppCompatActivity {
    private MovieAPIService apiService;
    private MovieAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        
        apiService = new MovieAPIService();
        adapter = new MovieAdapter();
        
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Load movies
        loadMovies();
    }
    
    private void loadMovies() {
        apiService.getMovies(new MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    adapter.setMovies(movies);
                    Log.d("MovieAPI", "Loaded " + movies.size() + " movies");
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MovieListActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("MovieAPI", "Error: " + error);
                });
            }
        });
    }
    
    // Example: Play video when movie is clicked
    private void playMovie(Movie movie) {
        apiService.getMovieSources(movie.getId(), new VideoSourceCallback() {
            @Override
            public void onSuccess(List<VideoSource> sources) {
                if (!sources.isEmpty()) {
                    VideoSource source = sources.get(0); // Get first source (1080p)
                    String videoUrl = source.getUrl();
                    
                    // Use ExoPlayer or VideoView to play the video
                    playVideoWithExoPlayer(videoUrl);
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(MovieListActivity.this, "Error loading video: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void playVideoWithExoPlayer(String videoUrl) {
        // Implementation for ExoPlayer
        Log.d("VideoPlayer", "Playing video: " + videoUrl);
        // Add your ExoPlayer implementation here
    }
}