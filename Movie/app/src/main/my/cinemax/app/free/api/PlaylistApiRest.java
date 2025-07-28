package my.cinemax.app.free.api;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistApiRest {
    
    public static void getHomeData(Callback<Data> callback) {
        PlaylistApiClient.getPlaylistData(callback);
    }
    
    public static void getPosterById(Integer id, Callback<Poster> callback) {
        // Get the playlist entry and convert to Poster
        PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
        
        try {
            if (dataStore.getPlaylistData() != null) {
                my.cinemax.app.free.entity.PlaylistEntry entry = dataStore.getEntryById(id);
                if (entry != null) {
                    // Find the category for this entry
                    my.cinemax.app.free.entity.PlaylistCategory category = findCategoryForEntry(entry);
                    if (category != null) {
                        Poster poster = PlaylistDataAdapter.convertToPoster(entry, category);
                        Response<Poster> response = Response.success(poster);
                        // Create a dummy Call for compatibility
                        Call<Poster> dummyCall = createDummyCall();
                        callback.onResponse(dummyCall, response);
                        return;
                    }
                }
            }
            
            Call<Poster> dummyCall = createDummyCall();
            callback.onFailure(dummyCall, new Exception("Poster not found"));
        } catch (Exception e) {
            Call<Poster> dummyCall = createDummyCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    public static void getSeasonsBySerie(Integer serieId, Callback<List<Season>> callback) {
        try {
            PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
            List<Season> seasons = dataStore.getSeasonsForEntry(serieId);
            
            Call<List<Season>> dummyCall = createDummySeasonCall();
            if (!seasons.isEmpty()) {
                Response<List<Season>> response = Response.success(seasons);
                callback.onResponse(dummyCall, response);
            } else {
                callback.onFailure(dummyCall, new Exception("No seasons found"));
            }
        } catch (Exception e) {
            Call<List<Season>> dummyCall = createDummySeasonCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    public static void searchData(String query, Callback<Data> callback) {
        try {
            PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
            
            Call<Data> dummyCall = createDummyDataCall();
            if (dataStore.getPlaylistData() != null) {
                Data searchResults = new Data();
                List<Poster> matchingPosters = new ArrayList<>();
                
                // Search through all entries
                for (my.cinemax.app.free.entity.PlaylistCategory category : dataStore.getPlaylistData().getCategories()) {
                    if (category.getEntries() != null) {
                        for (my.cinemax.app.free.entity.PlaylistEntry entry : category.getEntries()) {
                            if (entry.getTitle() != null && 
                                entry.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                
                                if (!"Live TV".equals(category.getMainCategory())) {
                                    Poster poster = PlaylistDataAdapter.convertToPoster(entry, category);
                                    matchingPosters.add(poster);
                                }
                            }
                        }
                    }
                }
                
                searchResults.setPosters(matchingPosters);
                Response<Data> response = Response.success(searchResults);
                callback.onResponse(dummyCall, response);
            } else {
                callback.onFailure(dummyCall, new Exception("No data available for search"));
            }
        } catch (Exception e) {
            Call<Data> dummyCall = createDummyDataCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    private static my.cinemax.app.free.entity.PlaylistCategory findCategoryForEntry(my.cinemax.app.free.entity.PlaylistEntry targetEntry) {
        PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
        
        if (dataStore.getPlaylistData() != null) {
            for (my.cinemax.app.free.entity.PlaylistCategory category : dataStore.getPlaylistData().getCategories()) {
                if (category.getEntries() != null) {
                    for (my.cinemax.app.free.entity.PlaylistEntry entry : category.getEntries()) {
                        if (entry.getTitle().equals(targetEntry.getTitle())) {
                            return category;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public static void getChannelsByFiltres(Integer category, Integer country, Integer page, Callback<List<my.cinemax.app.free.entity.Channel>> callback) {
        try {
            PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
            List<my.cinemax.app.free.entity.Channel> channels = new ArrayList<>();
            
            Call<List<my.cinemax.app.free.entity.Channel>> dummyCall = createDummyCall();
            
            if (dataStore.getPlaylistData() != null) {
                // Get all channels from Live TV category
                for (my.cinemax.app.free.entity.PlaylistCategory playlistCategory : dataStore.getPlaylistData().getCategories()) {
                    if ("Live TV".equals(playlistCategory.getMainCategory()) && playlistCategory.getEntries() != null) {
                        for (my.cinemax.app.free.entity.PlaylistEntry entry : playlistCategory.getEntries()) {
                            my.cinemax.app.free.entity.Channel channel = PlaylistDataAdapter.convertToChannel(entry, playlistCategory);
                            channels.add(channel);
                        }
                    }
                }
                
                Response<List<my.cinemax.app.free.entity.Channel>> response = Response.success(channels);
                callback.onResponse(dummyCall, response);
            } else {
                callback.onFailure(dummyCall, new Exception("No channel data available"));
            }
        } catch (Exception e) {
            Call<List<my.cinemax.app.free.entity.Channel>> dummyCall = createDummyCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    public static void getMoviesByFiltres(Integer genre, String order, Integer page, Callback<List<Poster>> callback) {
        try {
            PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
            List<Poster> movies = new ArrayList<>();
            
            Call<List<Poster>> dummyCall = createDummyCall();
            
            if (dataStore.getPlaylistData() != null) {
                // Get all movies from Movies category
                for (my.cinemax.app.free.entity.PlaylistCategory playlistCategory : dataStore.getPlaylistData().getCategories()) {
                    if ("Movies".equals(playlistCategory.getMainCategory()) && playlistCategory.getEntries() != null) {
                        for (my.cinemax.app.free.entity.PlaylistEntry entry : playlistCategory.getEntries()) {
                            Poster poster = PlaylistDataAdapter.convertToPoster(entry, playlistCategory);
                            movies.add(poster);
                        }
                    }
                }
                
                Response<List<Poster>> response = Response.success(movies);
                callback.onResponse(dummyCall, response);
            } else {
                callback.onFailure(dummyCall, new Exception("No movie data available"));
            }
        } catch (Exception e) {
            Call<List<Poster>> dummyCall = createDummyCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    public static void getSeriesByFiltres(Integer genre, String order, Integer page, Callback<List<Poster>> callback) {
        try {
            PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
            List<Poster> series = new ArrayList<>();
            
            Call<List<Poster>> dummyCall = createDummyCall();
            
            if (dataStore.getPlaylistData() != null) {
                // Get all series from TV Series category
                for (my.cinemax.app.free.entity.PlaylistCategory playlistCategory : dataStore.getPlaylistData().getCategories()) {
                    if ("TV Series".equals(playlistCategory.getMainCategory()) && playlistCategory.getEntries() != null) {
                        for (my.cinemax.app.free.entity.PlaylistEntry entry : playlistCategory.getEntries()) {
                            Poster poster = PlaylistDataAdapter.convertToPoster(entry, playlistCategory);
                            series.add(poster);
                        }
                    }
                }
                
                Response<List<Poster>> response = Response.success(series);
                callback.onResponse(dummyCall, response);
            } else {
                callback.onFailure(dummyCall, new Exception("No series data available"));
            }
        } catch (Exception e) {
            Call<List<Poster>> dummyCall = createDummyCall();
            callback.onFailure(dummyCall, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Call<T> createDummyCall() {
        return (Call<T>) new Call<Object>() {
            @Override
            public Response<Object> execute() { return null; }
            @Override
            public void enqueue(Callback<Object> callback) { }
            @Override
            public boolean isExecuted() { return false; }
            @Override
            public void cancel() { }
            @Override
            public boolean isCanceled() { return false; }
            @Override
            public Call<Object> clone() { return this; }
            @Override
            public okhttp3.Request request() { return null; }
        };
    }
    
    private static Call<List<Season>> createDummySeasonCall() {
        return createDummyCall();
    }
    
    private static Call<Data> createDummyDataCall() {
        return createDummyCall();
    }
}