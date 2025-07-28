package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Category;
import my.cinemax.app.free.entity.Country;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;
import retrofit2.Call;
import retrofit2.Callback;

import java.util.List;

/**
 * Smart API client that routes different types of API calls appropriately:
 * - Content data (movies, series, etc.) -> PlaylistApiClient/PlaylistApiRest
 * - Administrative data (genres, categories, etc.) -> MockApiRest
 * - User/device data -> MockApiRest for now
 */
public class SmartApiClient {
    
    // Content-related methods - use playlist data
    public static void getHomeData(Callback<Data> callback) {
        PlaylistApiClient.getPlaylistData(callback);
    }
    
    public static void getPosterById(Integer id, Callback<Poster> callback) {
        PlaylistApiRest.getPosterById(id, callback);
    }
    
    public static void getSeasonsBySerie(Integer serieId, Callback<List<Season>> callback) {
        PlaylistApiRest.getSeasonsBySerie(serieId, callback);
    }
    
    public static void searchData(String query, Callback<Data> callback) {
        PlaylistApiRest.searchData(query, callback);
    }
    
    // Administrative methods - use mock data
    public static void getGenreList(Callback<List<Genre>> callback) {
        MockApiRest.handleGenreList(callback);
    }
    
    public static void getCategoriesList(Callback<List<Category>> callback) {
        MockApiRest.handleCategoriesList(callback);
    }
    
    public static void getCountriesList(Callback<List<Country>> callback) {
        MockApiRest.handleCountriesList(callback);
    }
    
    public static void handleVersionCheck(Integer version, Integer userId, Callback<ApiResponse> callback) {
        MockApiRest.handleVersionCheck(version, userId, callback);
    }
    
    public static void handleDeviceRegistration(String token, Callback<ApiResponse> callback) {
        MockApiRest.handleDeviceRegistration(token, callback);
    }
    
    public static void handleSupportRequest(String email, String name, String message, Callback<ApiResponse> callback) {
        MockApiRest.handleSupportRequest(email, name, message, callback);
    }
}