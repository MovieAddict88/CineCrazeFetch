package my.cinemax.app.free.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;
import my.cinemax.app.free.entity.Slide;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonDataService {
    
    private static final String JSON_URL = "https://raw.githubusercontent.com/MovieAddict88/Movie-Source/main/playlist.json";
    private static final String TAG = "JsonDataService";
    
    private OkHttpClient client;
    private Gson gson;
    private Context context;
    
    public JsonDataService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }
    
    public JsonDataService(Context context) {
        this();
        this.context = context;
    }
    
    public interface DataCallback {
        void onSuccess(Data data);
        void onError(String error);
    }
    
    public void loadHomeData(DataCallback callback) {
        Request request = new Request.Builder()
                .url(JSON_URL)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to load JSON data from URL, trying local assets", e);
                // Try to load from local assets as fallback
                loadFromAssets(callback);
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "HTTP " + response.code() + ", trying local assets");
                    loadFromAssets(callback);
                    return;
                }
                
                try {
                    String jsonString = response.body().string();
                    Data data = parseJsonToData(jsonString);
                    callback.onSuccess(data);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse JSON data from URL, trying local assets", e);
                    loadFromAssets(callback);
                }
            }
        });
    }
    
    private void loadFromAssets(DataCallback callback) {
        if (context == null) {
            callback.onError("No context provided for loading local assets");
            return;
        }
        
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("playlist.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            
            StringBuilder jsonBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                jsonBuilder.append(buffer, 0, bytesRead);
            }
            reader.close();
            
            String jsonString = jsonBuilder.toString();
            Data data = parseJsonToData(jsonString);
            callback.onSuccess(data);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to load JSON data from assets", e);
            callback.onError("Failed to load data: " + e.getMessage());
        }
    }
    
    private Data parseJsonToData(String jsonString) {
        try {
            JsonObject root = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonArray categories = root.getAsJsonArray("Categories");
            
            Data data = new Data();
            List<Channel> channels = new ArrayList<>();
            List<Poster> posters = new ArrayList<>();
            List<Slide> slides = new ArrayList<>();
            List<Genre> genres = new ArrayList<>();
        
        // Process categories
        for (JsonElement categoryElement : categories) {
            JsonObject category = categoryElement.getAsJsonObject();
            String mainCategory = category.get("MainCategory").getAsString();
            
            // Create a genre for this main category
            Genre genre = new Genre();
            genre.setId(genres.size() + 1);
            genre.setTitle(mainCategory);
            genres.add(genre);
            
            JsonArray entries = category.getAsJsonArray("Entries");
            
            for (JsonElement entryElement : entries) {
                try {
                    JsonObject entry = entryElement.getAsJsonObject();
                    
                    if (mainCategory.equals("Live TV")) {
                        // Map to Channel
                        Channel channel = mapToChannel(entry, mainCategory);
                        if (channel != null) {
                            channels.add(channel);
                            
                            // Add first few channels as slides for the carousel
                            if (slides.size() < 5) {
                                Slide slide = new Slide();
                                slide.setId(slides.size() + 1);
                                slide.setTitle(channel.getTitle());
                                slide.setImage(channel.getImage());
                                slide.setChannel(channel);
                                slides.add(slide);
                            }
                        }
                    } else {
                        // Map to Poster (Movies/TV Series)
                        Poster poster = mapToPoster(entry, mainCategory);
                        if (poster != null) {
                            posters.add(poster);
                            
                            // Add first few movies/series as slides for the carousel
                            if (slides.size() < 5) {
                                Slide slide = new Slide();
                                slide.setId(slides.size() + 1);
                                slide.setTitle(poster.getTitle());
                                slide.setImage(poster.getImage());
                                slide.setPoster(poster);
                                slides.add(slide);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing entry: " + entryElement.toString(), e);
                    // Continue with next entry
                }
            }
        }
        
            data.setChannels(channels);
            data.setPosters(posters);
            data.setSlides(slides);
            data.setGenres(genres);
            
            return data;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON data", e);
            // Return empty data object to prevent crashes
            Data data = new Data();
            data.setChannels(new ArrayList<>());
            data.setPosters(new ArrayList<>());
            data.setSlides(new ArrayList<>());
            data.setGenres(new ArrayList<>());
            return data;
        }
    }
    
    private Channel mapToChannel(JsonObject entry, String category) {
        try {
            Channel channel = new Channel();
        
        channel.setId(entry.has("id") ? entry.get("id").getAsInt() : 
                     entry.get("Title").getAsString().hashCode());
        channel.setTitle(entry.get("Title").getAsString());
        channel.setDescription(entry.has("Description") ? 
                              entry.get("Description").getAsString() : "");
        channel.setImage(entry.has("Poster") ? 
                        entry.get("Poster").getAsString() : "");
        channel.setRating(entry.has("Rating") ? 
                         entry.get("Rating").getAsFloat() : 0.0f);
        channel.setViews(0);
        channel.setShares(0);
        channel.setComment(true);
        channel.setClassification(category);
        
        // Map servers to sources
        List<Source> sources = new ArrayList<>();
        if (entry.has("Servers")) {
            JsonArray servers = entry.getAsJsonArray("Servers");
            for (JsonElement serverElement : servers) {
                JsonObject server = serverElement.getAsJsonObject();
                Source source = new Source();
                source.setId(sources.size() + 1);
                source.setTitle(server.get("name").getAsString());
                source.setQuality(server.get("name").getAsString());
                source.setUrl(server.get("url").getAsString());
                source.setType("hls");
                source.setExternal(false);
                sources.add(source);
            }
        }
            channel.setSources(sources);
            
            return channel;
        } catch (Exception e) {
            Log.e(TAG, "Error mapping channel: " + entry.toString(), e);
            return null;
        }
    }
    
    private Poster mapToPoster(JsonObject entry, String category) {
        try {
            Poster poster = new Poster();
        
        poster.setId(entry.has("id") ? entry.get("id").getAsInt() : 
                    entry.get("Title").getAsString().hashCode());
        poster.setTitle(entry.get("Title").getAsString());
        poster.setDescription(entry.has("Description") ? 
                             entry.get("Description").getAsString() : "");
        poster.setImage(entry.has("Poster") ? 
                       entry.get("Poster").getAsString() : "");
        poster.setCover(entry.has("Thumbnail") ? 
                       entry.get("Thumbnail").getAsString() : 
                       (entry.has("Poster") ? entry.get("Poster").getAsString() : ""));
        poster.setRating(entry.has("Rating") ? 
                        entry.get("Rating").getAsFloat() : 0.0f);
        poster.setYear(entry.has("Year") ? 
                      String.valueOf(entry.get("Year").getAsInt()) : "2024");
        poster.setDuration(entry.has("Duration") ? 
                          entry.get("Duration").getAsString() : "");
        poster.setType(category.contains("Movies") ? "movie" : 
                      (category.contains("TV Series") ? "serie" : "serie"));
        poster.setClassification(category);
        poster.setComment(true);
        
        // Map servers to sources (handle both Movies and TV Series)
        List<Source> sources = new ArrayList<>();
        
        if (entry.has("Servers")) {
            // Handle Movies with direct Servers
            JsonArray servers = entry.getAsJsonArray("Servers");
            for (JsonElement serverElement : servers) {
                JsonObject server = serverElement.getAsJsonObject();
                Source source = new Source();
                source.setId(sources.size() + 1);
                source.setTitle(server.get("name").getAsString());
                source.setQuality(server.get("name").getAsString());
                source.setUrl(server.get("url").getAsString());
                source.setType("mp4");
                source.setExternal(false);
                sources.add(source);
            }
        } else if (entry.has("Seasons")) {
            // Handle TV Series with Seasons/Episodes
            JsonArray seasons = entry.getAsJsonArray("Seasons");
            if (seasons.size() > 0) {
                // Get the first episode of the first season as the main source
                JsonObject firstSeason = seasons.get(0).getAsJsonObject();
                if (firstSeason.has("Episodes")) {
                    JsonArray episodes = firstSeason.getAsJsonArray("Episodes");
                    if (episodes.size() > 0) {
                        JsonObject firstEpisode = episodes.get(0).getAsJsonObject();
                        if (firstEpisode.has("Servers")) {
                            JsonArray servers = firstEpisode.getAsJsonArray("Servers");
                            for (JsonElement serverElement : servers) {
                                JsonObject server = serverElement.getAsJsonObject();
                                Source source = new Source();
                                source.setId(sources.size() + 1);
                                source.setTitle(server.get("name").getAsString());
                                source.setQuality(server.get("name").getAsString());
                                source.setUrl(server.get("url").getAsString());
                                source.setType("mp4");
                                source.setExternal(false);
                                sources.add(source);
                            }
                        }
                    }
                }
            }
        }
        poster.setSources(sources);
        
        // Set genres
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setTitle(category);
        genres.add(genre);
            poster.setGenres(genres);
            
            return poster;
        } catch (Exception e) {
            Log.e(TAG, "Error mapping poster: " + entry.toString(), e);
            return null;
        }
    }
}