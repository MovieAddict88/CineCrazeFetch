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
        
        if (dataStore.getPlaylistData() != null) {
            my.cinemax.app.free.entity.PlaylistEntry entry = dataStore.getEntryById(id);
            if (entry != null) {
                // Find the category for this entry
                my.cinemax.app.free.entity.PlaylistCategory category = findCategoryForEntry(entry);
                if (category != null) {
                    Poster poster = PlaylistDataAdapter.convertToPoster(entry, category);
                    Response<Poster> response = Response.success(poster);
                    callback.onResponse(null, response);
                    return;
                }
            }
        }
        
        callback.onFailure(null, new Exception("Poster not found"));
    }
    
    public static void getSeasonsBySerie(Integer serieId, Callback<List<Season>> callback) {
        PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
        List<Season> seasons = dataStore.getSeasonsForEntry(serieId);
        
        if (!seasons.isEmpty()) {
            Response<List<Season>> response = Response.success(seasons);
            callback.onResponse(null, response);
        } else {
            callback.onFailure(null, new Exception("No seasons found"));
        }
    }
    
    public static void searchData(String query, Callback<Data> callback) {
        PlaylistDataStore dataStore = PlaylistDataStore.getInstance();
        
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
            callback.onResponse(null, response);
        } else {
            callback.onFailure(null, new Exception("No data available for search"));
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
}