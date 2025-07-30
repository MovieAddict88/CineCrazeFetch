package my.cinemax.app.free.api;

import android.app.Activity;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import my.cinemax.app.free.BuildConfig;
import my.cinemax.app.free.MyApplication;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;
import my.cinemax.app.free.entity.Actress;

/**
 * Created by Tamim on 28/09/2019.
 */


public class apiClient {
    private static Retrofit retrofit = null;
    private static final String CACHE_CONTROL = "Cache-Control";

    // Removed old API client - now using only GitHub API
    public static Retrofit initClient(){
        return getJsonApiClient();
    }

    // Removed old API methods - now using only GitHub API
    public static  void setClient(retrofit2.Response<ApiResponse> response, Activity activity, PrefManager prf){
        // This method is kept for compatibility but no longer used
    }
    private static void adapterDataWithNewVersion(retrofit2.Response<ApiResponse> response,PrefManager prf) {
        // This method is kept for compatibility but no longer used
    }
    public static String LoadClientData(Activity activity){
        return activity.getApplicationContext().getPackageName();
    }
    public static void FormatData(final Activity activity,Object o){
        // Skip old API calls - app now uses GitHub JSON API
        // Set formatted to true to skip old API calls
        final PrefManager prf = new PrefManager(activity.getApplication());
        prf.setString("formatted","true");
        // Old API calls removed - app now uses GitHub JSON API
    }
    public static boolean check(Activity activity){
        final PrefManager prf = new PrefManager(activity.getApplication());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        if (prf.getString("LAST_DATA_LOAD").equals("")) {
            prf.setString("LAST_DATA_LOAD", strDate);
        } else {
            String toyBornTime = prf.getString("LAST_DATA_LOAD");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date oldDate = dateFormat.parse(toyBornTime);
                System.out.println(oldDate);

                Date currentDate = new Date();

                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;

                if (seconds >15) {
                    prf.setString("LAST_DATA_LOAD", strDate);
                    return  true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }
    public static Retrofit getClient() {
        if (retrofit==null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor( provideHttpLoggingInterceptor() )
                    .addInterceptor( provideOfflineCacheInterceptor() )
                    .addNetworkInterceptor( provideCacheInterceptor() )
                    .cache( provideCache() )
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttpClient);
            Picasso picasso = new Picasso.Builder(MyApplication.getInstance())
                    .downloader(okHttp3Downloader)
                    .build();
            Picasso.setSingletonInstance(picasso);

            retrofit = new Retrofit.Builder()
				.baseUrl("https://raw.githubusercontent.com/MovieAddict88/movie-api/main/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    private static Cache provideCache ()
    {
        Cache cache = null;
        try
        {
            cache = new Cache( new File(MyApplication.getInstance().getCacheDir(), "wallpaper-cache" ),
                    10 * 1024 * 1024 ); // 10 MB
        }
        catch (Exception e)
        {
            Timber.e( e, "Could not create Cache!" );
        }
        return cache;
    }
    private static HttpLoggingInterceptor provideHttpLoggingInterceptor ()
    {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger()
                {
                    @Override
                    public void log (String message)
                    {
                        Timber.d( message );
                        Log.v("MYAPI",message);

                    }
                } );
        httpLoggingInterceptor.setLevel( BuildConfig.DEBUG ? HEADERS : NONE );
        return httpLoggingInterceptor;
    }
    public static Interceptor provideCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Response response = chain.proceed( chain.request() );
                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge( 2, TimeUnit.SECONDS )
                        .build();
                return response.newBuilder()
                        .header( CACHE_CONTROL, cacheControl.toString() )
                        .build();
            }
        };
    }
    // Old API ID removed - now using GitHub API
    public static String retrofit_id = "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/";
    
    // ===== JSON API CLIENT METHODS =====
    // These methods will fetch data from your GitHub JSON file
    
    /**
     * Get the JSON API client for fetching data from GitHub Raw
     */
    public static Retrofit getJsonApiClient() {
        return new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/MovieAddict88/movie-api/main/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Fetch all data from the JSON API
     */
    public static void getJsonApiData(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getJsonApiData();
        call.enqueue(callback);
    }
    
    /**
     * Fetch home data from the JSON API
     */
    public static void getHomeDataFromJson(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getHomeDataFromJson();
        call.enqueue(callback);
    }
    
    /**
     * Fetch movies from the JSON API
     */
    public static void getMoviesFromJson(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getMoviesFromJson();
        call.enqueue(callback);
    }
    
    /**
     * Fetch channels from the JSON API
     */
    public static void getChannelsFromJson(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getChannelsFromJson();
        call.enqueue(callback);
    }
    
    /**
     * Fetch actors from the JSON API
     */
    public static void getActorsFromJson(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getActorsFromJson();
        call.enqueue(callback);
    }
    
    /**
     * Fetch genres from the JSON API
     */
    public static void getGenresFromJson(Callback<JsonApiResponse> callback) {
        Retrofit retrofit = getJsonApiClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getGenresFromJson();
        call.enqueue(callback);
    }
    
    /**
     * Get a specific movie by ID from the JSON API
     */
    public static void getMovieByIdFromJson(int movieId, Callback<JsonApiResponse> callback) {
        getMoviesFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Find the movie by ID
                    for (Poster movie : response.body().getMovies()) {
                        if (movie.getId() == movieId) {
                            // Create a new response with just this movie
                            JsonApiResponse singleMovieResponse = new JsonApiResponse();
                            // You can customize this response as needed
                            callback.onResponse(call, retrofit2.Response.success(singleMovieResponse));
                            return;
                        }
                    }
                    callback.onFailure(call, new Exception("Movie not found"));
                } else {
                    callback.onFailure(call, new Exception("Failed to load movies"));
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    
    /**
     * Get video sources for a movie from the JSON API
     */
    public static void getMovieVideoSources(int movieId, Callback<JsonApiResponse> callback) {
        getMovieByIdFromJson(movieId, new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Return video sources from the JSON
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Exception("Failed to load video sources"));
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    public static Interceptor provideOfflineCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Request request = chain.request();
                if ( !MyApplication.hasNetwork() )
                {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale( 30, TimeUnit.DAYS )
                            .build();
                    request = request.newBuilder()
                            .cacheControl( cacheControl )
                            .build();
                }
                return chain.proceed( request );
            }
        };
        }
    
    /**
     * Get ads configuration from separate JSON API
     */
    public static void getAdsConfigFromJson(Callback<JsonApiResponse> callback) {
        // Create a separate Retrofit client for ads config
        Retrofit adsRetrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/MovieAddict88/movie-api/main/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiRest adsService = adsRetrofit.create(apiRest.class);
        Call<JsonApiResponse> call = adsService.getAdsConfig();
        
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse.AdsConfig adsConfig = response.body().getAdsConfig();
                    if (adsConfig != null) {
                        callback.onResponse(call, response);
                    } else {
                        callback.onFailure(call, new Exception("No ads configuration found"));
                    }
                } else {
                    callback.onFailure(call, new Exception("Failed to load ads configuration"));
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
    
    /**
     * Get JSON API data with custom callback
     */
    public static void getJsonApiData(JsonApiCallback callback) {
        getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load data from JSON API");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Load ads configuration and update PrefManager
     */
    public static void loadAdsConfigAndUpdatePrefs(Activity activity, AdsConfigCallback callback) {
        getAdsConfigFromJson(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse.AdsConfig adsConfig = response.body().getAdsConfig();
                    if (adsConfig != null) {
                        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
                        
                        // Update AdMob IDs
                        if (adsConfig.getAdmob() != null) {
                            prefManager.setString("ADMIN_BANNER_ADMOB_ID", adsConfig.getAdmob().getBannerId());
                            prefManager.setString("ADMIN_INTERSTITIAL_ADMOB_ID", adsConfig.getAdmob().getInterstitialId());
                            prefManager.setString("ADMIN_REWARDED_ADMOB_ID", adsConfig.getAdmob().getRewardedId());
                            prefManager.setString("ADMIN_NATIVE_ADMOB_ID", adsConfig.getAdmob().getNativeId());
                        }
                        
                        // Update Facebook IDs
                        if (adsConfig.getFacebook() != null) {
                            prefManager.setString("ADMIN_BANNER_FACEBOOK_ID", adsConfig.getFacebook().getBannerId());
                            prefManager.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID", adsConfig.getFacebook().getInterstitialId());
                            prefManager.setString("ADMIN_REWARDED_FACEBOOK_ID", adsConfig.getFacebook().getRewardedId());
                            prefManager.setString("ADMIN_NATIVE_FACEBOOK_ID", adsConfig.getFacebook().getNativeId());
                        }
                        
                        // Update settings
                        if (adsConfig.getSettings() != null) {
                            JsonApiResponse.AdsSettings settings = adsConfig.getSettings();
                            
                            prefManager.setString("ADMIN_BANNER_TYPE", settings.getBannerType());
                            prefManager.setString("ADMIN_INTERSTITIAL_TYPE", settings.getInterstitialType());
                            prefManager.setString("ADMIN_NATIVE_TYPE", settings.getNativeType());
                            prefManager.setInt("ADMIN_INTERSTITIAL_CLICKS", settings.getInterstitialClicks());
                            prefManager.setString("ADMIN_NATIVE_LINES", String.valueOf(settings.getNativeLines()));
                            
                            // Enable/disable ads
                            prefManager.setString("ADMIN_BANNER_TYPE", settings.isBannerEnabled() ? settings.getBannerType() : "FALSE");
                            prefManager.setString("ADMIN_INTERSTITIAL_TYPE", settings.isInterstitialEnabled() ? settings.getInterstitialType() : "FALSE");
                            prefManager.setString("ADMIN_NATIVE_TYPE", settings.isNativeEnabled() ? settings.getNativeType() : "FALSE");
                        }
                        
                        callback.onSuccess("Ads configuration updated successfully");
                    } else {
                        callback.onError("No ads configuration found");
                    }
                } else {
                    callback.onError("Failed to load ads configuration");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onError("Failed to load ads config: " + t.getMessage());
            }
        });
    }
    
    // Callback interface for ads configuration
    public interface AdsConfigCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    // Callback interface for JSON API data
    public interface JsonApiCallback {
        void onSuccess(JsonApiResponse jsonResponse);
        void onError(String error);
    }
    
}
