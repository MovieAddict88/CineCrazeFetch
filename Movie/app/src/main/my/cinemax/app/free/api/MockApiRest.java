package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.ApiValue;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Category;
import my.cinemax.app.free.entity.Country;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MockApiRest {
    
    public static void handleVersionCheck(Integer code, Integer user, Callback<ApiResponse> callback) {
        // Create a mock successful response for version check
        ApiResponse response = new ApiResponse();
        response.setCode(200);
        response.setMessage("success");
        
        // Add mock configuration values
        List<ApiValue> values = new ArrayList<>();
        ApiValue admobId = new ApiValue();
        admobId.setName("ADMIN_REWARDED_ADMOB_ID");
        admobId.setValue("ca-app-pub-3940256099942544/5224354917"); // Test AdMob ID
        values.add(admobId);
        
        ApiValue bannerAdmobId = new ApiValue();
        bannerAdmobId.setName("ADMIN_BANNER_ADMOB_ID");
        bannerAdmobId.setValue("ca-app-pub-3940256099942544/6300978111"); // Test AdMob ID
        values.add(bannerAdmobId);
        
        ApiValue interstitialAdmobId = new ApiValue();
        interstitialAdmobId.setName("ADMIN_INTERSTITIAL_ADMOB_ID");
        interstitialAdmobId.setValue("ca-app-pub-3940256099942544/1033173712"); // Test AdMob ID
        values.add(interstitialAdmobId);
        
        ApiValue nativeAdmobId = new ApiValue();
        nativeAdmobId.setName("ADMIN_NATIVE_ADMOB_ID");
        nativeAdmobId.setValue("ca-app-pub-3940256099942544/2247696110"); // Test AdMob ID
        values.add(nativeAdmobId);
        
        response.setValues(values);
        
        // Simulate successful response
        Response<ApiResponse> retrofitResponse = Response.success(response);
        
        // Create a dummy call for the callback
        Call<ApiResponse> dummyCall = createDummyCall();
        
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    public static void handleGenreList(Callback<List<Genre>> callback) {
        // Create mock genres based on the playlist categories
        List<Genre> genres = new ArrayList<>();
        
        Genre actionGenre = new Genre();
        actionGenre.setId(1);
        actionGenre.setTitle("Action");
        genres.add(actionGenre);
        
        Genre animeGenre = new Genre();
        animeGenre.setId(2);
        animeGenre.setTitle("Anime");
        genres.add(animeGenre);
        
        Genre entertainmentGenre = new Genre();
        entertainmentGenre.setId(3);
        entertainmentGenre.setTitle("Entertainment");
        genres.add(entertainmentGenre);
        
        Response<List<Genre>> retrofitResponse = Response.success(genres);
        Call<List<Genre>> dummyCall = createDummyCall();
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    public static void handleCategoriesList(Callback<List<Category>> callback) {
        // Create mock categories
        List<Category> categories = new ArrayList<>();
        
        Category liveTV = new Category();
        liveTV.setId(1);
        liveTV.setTitle("Live TV");
        categories.add(liveTV);
        
        Category movies = new Category();
        movies.setId(2);
        movies.setTitle("Movies");
        categories.add(movies);
        
        Category series = new Category();
        series.setId(3);
        series.setTitle("TV Series");
        categories.add(series);
        
        Response<List<Category>> retrofitResponse = Response.success(categories);
        Call<List<Category>> dummyCall = createDummyCall();
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    public static void handleCountriesList(Callback<List<Country>> callback) {
        // Create mock countries
        List<Country> countries = new ArrayList<>();
        
        Country philippines = new Country();
        philippines.setId(1);
        philippines.setTitle("Philippines");
        countries.add(philippines);
        
        Country usa = new Country();
        usa.setId(2);
        usa.setTitle("USA");
        countries.add(usa);
        
        Country japan = new Country();
        japan.setId(3);
        japan.setTitle("Japan");
        countries.add(japan);
        
        Response<List<Country>> retrofitResponse = Response.success(countries);
        Call<List<Country>> dummyCall = createDummyCall();
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    public static void handleDeviceRegistration(String token, Callback<ApiResponse> callback) {
        // Create a mock successful response for device registration
        ApiResponse response = new ApiResponse();
        response.setCode(200);
        response.setMessage("Device registered successfully");
        response.setValues(new ArrayList<>());
        
        Response<ApiResponse> retrofitResponse = Response.success(response);
        Call<ApiResponse> dummyCall = createDummyCall();
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    public static void handleSupportRequest(String email, String name, String message, Callback<ApiResponse> callback) {
        // Create a mock successful response for support request
        ApiResponse response = new ApiResponse();
        response.setCode(200);
        response.setMessage("Support request submitted successfully");
        response.setValues(new ArrayList<>());
        
        Response<ApiResponse> retrofitResponse = Response.success(response);
        Call<ApiResponse> dummyCall = createDummyCall();
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Call<T> createDummyCall() {
        return (Call<T>) new Call<Object>() {
            @Override public Response<Object> execute() { return null; }
            @Override public void enqueue(Callback<Object> callback) { }
            @Override public boolean isExecuted() { return false; }
            @Override public void cancel() { }
            @Override public boolean isCanceled() { return false; }
            @Override public Call<Object> clone() { return this; }
            @Override public okhttp3.Request request() { return null; }
        };
    }
}