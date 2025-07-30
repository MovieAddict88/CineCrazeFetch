// ===== JSON API USAGE EXAMPLE =====
// This shows how to use the JSON API integration in your existing Android app

package my.cinemax.app.free.ui.activities;

import android.util.Log;
import android.widget.Toast;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Channel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import my.cinemax.app.free.entity.Actor;

/**
 * Example of how to use the JSON API integration
 * Add this to your existing activities or fragments
 */
public class JSON_API_Usage_Example {

    // ===== STEP 1: LOAD HOME DATA =====
    public void loadHomeDataExample() {
        apiClient.getHomeDataFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse jsonResponse = response.body();
                    
                    // Get movies from JSON
                    List<Poster> movies = jsonResponse.getMovies();
                    Log.d("JSON_API", "Loaded " + movies.size() + " movies");
                    
                    // Get channels from JSON
                    List<Channel> channels = jsonResponse.getChannels();
                    Log.d("JSON_API", "Loaded " + channels.size() + " channels");
                    
                    // Get actors from JSON
                    List<Actor> actors = jsonResponse.getActors();
                    Log.d("JSON_API", "Loaded " + actors.size() + " actors");
                    
                    // Update your UI with this data
                    updateUIWithJsonData(movies, channels, actors);
                    
                } else {
                    Log.e("JSON_API", "Failed to load data: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error loading data", t);
            }
        });
    }
    
    // ===== STEP 2: LOAD SPECIFIC MOVIES =====
    public void loadMoviesExample() {
        apiClient.getMoviesFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> movies = response.body().getMovies();
                    
                    // Display movies in your RecyclerView
                    for (Poster movie : movies) {
                        Log.d("JSON_API", "Movie: " + movie.getTitle() + " (" + movie.getYear() + ")");
                        
                        // Get video sources for this movie
                        if (movie.getSources() != null && !movie.getSources().isEmpty()) {
                            String videoUrl = movie.getSources().get(0).getUrl();
                            Log.d("JSON_API", "Video URL: " + videoUrl);
                        }
                    }
                    
                    // Update your movies adapter
                    updateMoviesAdapter(movies);
                    
                } else {
                    Log.e("JSON_API", "Failed to load movies");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error loading movies", t);
            }
        });
    }
    
    // ===== STEP 3: LOAD CHANNELS (LIVE STREAMS) =====
    public void loadChannelsExample() {
        apiClient.getChannelsFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Channel> channels = response.body().getChannels();
                    
                    // Display channels in your RecyclerView
                    for (Channel channel : channels) {
                        Log.d("JSON_API", "Channel: " + channel.getTitle());
                        
                        // Get live stream sources for this channel
                        if (channel.getSources() != null && !channel.getSources().isEmpty()) {
                            String streamUrl = channel.getSources().get(0).getUrl();
                            Log.d("JSON_API", "Live Stream URL: " + streamUrl);
                        }
                    }
                    
                    // Update your channels adapter
                    updateChannelsAdapter(channels);
                    
                } else {
                    Log.e("JSON_API", "Failed to load channels");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error loading channels", t);
            }
        });
    }
    
    // ===== STEP 4: PLAY A MOVIE =====
    public void playMovieExample(int movieId) {
        // Get video sources for a specific movie
        apiClient.getMovieVideoSources(movieId, new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse.VideoSources videoSources = response.body().getVideoSources();
                    
                    if (videoSources != null) {
                        // Get Big Buck Bunny URL (1080p)
                        if (videoSources.getBigBuckBunny() != null) {
                            String videoUrl = videoSources.getBigBuckBunny().getUrls().getP1080();
                            Log.d("JSON_API", "Playing Big Buck Bunny: " + videoUrl);
                            
                            // Start video player with this URL
                            startVideoPlayer(videoUrl);
                        }
                        // Get Elephants Dream URL (1080p)
                        else if (videoSources.getElephantsDream() != null) {
                            String videoUrl = videoSources.getElephantsDream().getUrls().getP1080();
                            Log.d("JSON_API", "Playing Elephants Dream: " + videoUrl);
                            
                            // Start video player with this URL
                            startVideoPlayer(videoUrl);
                        }
                    }
                } else {
                    Log.e("JSON_API", "Failed to load video sources");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error loading video sources", t);
            }
        });
    }
    
    // ===== STEP 5: PLAY LIVE STREAM =====
    public void playLiveStreamExample() {
        // Get live stream URL from JSON
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse.VideoSources videoSources = response.body().getVideoSources();
                    
                    if (videoSources != null && videoSources.getLiveStreams() != null) {
                        JsonApiResponse.LiveStream liveStream = videoSources.getLiveStreams().getTestHls();
                        
                        if (liveStream != null) {
                            String streamUrl = liveStream.getUrl();
                            Log.d("JSON_API", "Playing Live Stream: " + streamUrl);
                            
                            // Start live stream player with this URL
                            startLiveStreamPlayer(streamUrl);
                        }
                    }
                } else {
                    Log.e("JSON_API", "Failed to load live stream");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error loading live stream", t);
            }
        });
    }
    
    // ===== STEP 6: SEARCH MOVIES =====
    public void searchMoviesExample(String query) {
        // Load all movies and filter locally
        apiClient.getMoviesFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Poster> allMovies = response.body().getMovies();
                    List<Poster> searchResults = new ArrayList<>();
                    
                    // Filter movies by query
                    for (Poster movie : allMovies) {
                        if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            searchResults.add(movie);
                        }
                    }
                    
                    Log.d("JSON_API", "Found " + searchResults.size() + " movies for query: " + query);
                    
                    // Update search results adapter
                    updateSearchResultsAdapter(searchResults);
                    
                } else {
                    Log.e("JSON_API", "Failed to search movies");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                Log.e("JSON_API", "Error searching movies", t);
            }
        });
    }
    
    // ===== HELPER METHODS =====
    
    private void updateUIWithJsonData(List<Poster> movies, List<Channel> channels, List<Actor> actors) {
        // Update your UI components with the JSON data
        Log.d("JSON_API", "Updating UI with JSON data");
    }
    
    private void updateMoviesAdapter(List<Poster> movies) {
        // Update your movies RecyclerView adapter
        Log.d("JSON_API", "Updating movies adapter with " + movies.size() + " movies");
    }
    
    private void updateChannelsAdapter(List<Channel> channels) {
        // Update your channels RecyclerView adapter
        Log.d("JSON_API", "Updating channels adapter with " + channels.size() + " channels");
    }
    
    private void updateSearchResultsAdapter(List<Poster> searchResults) {
        // Update your search results RecyclerView adapter
        Log.d("JSON_API", "Updating search results adapter with " + searchResults.size() + " results");
    }
    
    private void startVideoPlayer(String videoUrl) {
        // Start your video player activity with the URL
        Log.d("JSON_API", "Starting video player with URL: " + videoUrl);
        
        // Example: Start ExoPlayer or VideoView
        // Intent intent = new Intent(this, VideoPlayerActivity.class);
        // intent.putExtra("video_url", videoUrl);
        // startActivity(intent);
    }
    
    private void startLiveStreamPlayer(String streamUrl) {
        // Start your live stream player activity with the URL
        Log.d("JSON_API", "Starting live stream player with URL: " + streamUrl);
        
        // Example: Start ExoPlayer for HLS streams
        // Intent intent = new Intent(this, LiveStreamActivity.class);
        // intent.putExtra("stream_url", streamUrl);
        // startActivity(intent);
    }
    
    // ===== USAGE IN YOUR ACTIVITY =====
    
    /**
     * Example: How to use in your HomeActivity
     */
    public void exampleUsageInActivity() {
        // 1. Load home data when app starts
        loadHomeDataExample();
        
        // 2. Load movies when user clicks Movies tab
        loadMoviesExample();
        
        // 3. Load channels when user clicks TV tab
        loadChannelsExample();
        
        // 4. Play a movie when user clicks on it
        playMovieExample(1); // Movie ID 1 (Big Buck Bunny)
        
        // 5. Play live stream when user clicks on channel
        playLiveStreamExample();
        
        // 6. Search movies when user types in search
        searchMoviesExample("Big Buck");
    }
}

// ===== INTEGRATION WITH YOUR EXISTING CODE =====

/**
 * To integrate with your existing HomeActivity:
 * 
 * 1. Add this to your HomeActivity:
 *    private JSON_API_Usage_Example jsonApiExample = new JSON_API_Usage_Example();
 * 
 * 2. Call the methods in your onCreate or wherever you need:
 *    jsonApiExample.loadHomeDataExample();
 * 
 * 3. Or call directly from your existing methods:
 *    apiClient.getMoviesFromJson(new Callback<JsonApiResponse>() { ... });
 * 
 * 4. Update your Global.java URL:
 *    public static final String API_URL = "https://your-username.github.io/movie-api/free_movie_api.json";
 * 
 * 5. Upload free_movie_api.json to your GitHub repository
 * 
 * 6. Build and test your app!
 */