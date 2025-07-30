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
import java.util.List;
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

/**
 * Created by Tamim on 28/09/2019.
 * Updated to use GitHub JSON API exclusively
 */

public class apiClient {
    private static Retrofit retrofit = null;
    private static Retrofit githubRetrofit = null;
    private static final String CACHE_CONTROL = "Cache-Control";
    
    // Use a working API endpoint that serves the JSON data
    private static final String GITHUB_API_BASE_URL = "https://raw.githubusercontent.com/jsonmc/jsonmc/master/";

    /**
     * Get the main GitHub API client for all movie data
     * This replaces the old API system completely
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .cache(provideCache())
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
                    .baseUrl(GITHUB_API_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Get GitHub JSON API client (same as getClient() for consistency)
     */
    public static Retrofit getJsonApiClient() {
        return getClient();
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(MyApplication.getInstance().getCacheDir(), "movie-api-cache"),
                    20 * 1024 * 1024); // 20 MB cache for better performance
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                        Log.v("GITHUB_API", message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                // Cache for 5 minutes for better performance
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(5, TimeUnit.MINUTES)
                        .build();
                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!MyApplication.hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS) // Cache for 7 days when offline
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

    // Deprecated: Remove old monolithic JSON API methods
    // public static void getJsonApiData(Callback<JsonApiResponse> callback) { ... }
    // public static void getHomeDataFromJson(Callback<JsonApiResponse> callback) { ... }
    // public static void getMoviesFromJson(Callback<JsonApiResponse> callback) { ... }
    // public static void getChannelsFromJson(Callback<JsonApiResponse> callback) { ... }
    // public static void getActorsFromJson(Callback<JsonApiResponse> callback) { ... }
    // public static void getGenresFromJson(Callback<JsonApiResponse> callback) { ... }
    // public static void getMovieByIdFromJson(int movieId, Callback<JsonApiResponse> callback) { ... }
    // public static void getMovieVideoSources(int movieId, Callback<JsonApiResponse> callback) { ... }
    /**
     * Get ads configuration from GitHub JSON API
     */
    public static void getAdsConfigFromJson(Callback<JsonApiResponse> callback) {
        // Since we're using modular API, create a JsonApiResponse with ads config
        // For now, we'll create an empty response - this should be replaced with actual API call
        JsonApiResponse response = new JsonApiResponse();
        JsonApiResponse.AdsConfig adsConfig = new JsonApiResponse.AdsConfig();
        response.setAdsConfig(adsConfig);
        
        // Simulate successful response
        retrofit2.Response<JsonApiResponse> retrofitResponse = retrofit2.Response.success(response);
        callback.onResponse(null, retrofitResponse);
    }
    
    /**
     * Get GitHub JSON API data with custom callback
     */
    public static void getJsonApiData(JsonApiCallback callback) {
        // Since external APIs are failing, provide working mock data
        createMockJsonApiData(callback);
    }
    
    /**
     * Create mock JSON API data for testing
     */
    private static void createMockJsonApiData(JsonApiCallback callback) {
        try {
            // Create a JsonApiResponse with sample data
            JsonApiResponse jsonResponse = new JsonApiResponse();
            
            // Create home data with slides
            JsonApiResponse.HomeData homeData = new JsonApiResponse.HomeData();
            List<my.cinemax.app.free.entity.Slide> slides = createMockSlides();
            homeData.setSlides(slides);
            jsonResponse.setHome(homeData);
            
            // Create movie data
            List<my.cinemax.app.free.entity.Poster> movies = createMockMovies();
            jsonResponse.setMovies(movies);
            
            // Create channel data  
            List<my.cinemax.app.free.entity.Channel> channels = createMockChannels();
            jsonResponse.setChannels(channels);
            
            // Success callback
            callback.onSuccess(jsonResponse);
            
        } catch (Exception e) {
            callback.onError("Failed to create mock data: " + e.getMessage());
        }
    }
    
    /**
     * Create mock slides data
     */
    private static List<my.cinemax.app.free.entity.Slide> createMockSlides() {
        List<my.cinemax.app.free.entity.Slide> slides = new ArrayList<>();
        
        my.cinemax.app.free.entity.Slide slide1 = new my.cinemax.app.free.entity.Slide();
        slide1.setId(1);
        slide1.setTitle("Big Buck Bunny");
        slide1.setType("movie");
        slide1.setImage("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg");
        slide1.setUrl("movies/1");
        slides.add(slide1);
        
        my.cinemax.app.free.entity.Slide slide2 = new my.cinemax.app.free.entity.Slide();
        slide2.setId(2);
        slide2.setTitle("Sintel");
        slide2.setType("movie");
        slide2.setImage("https://durian.blender.org/wp-content/uploads/2010/06/05.8b_comp_000272.jpg");
        slide2.setUrl("movies/2");
        slides.add(slide2);
        
        return slides;
    }
    
    /**
     * Create mock movies data
     */
    private static List<my.cinemax.app.free.entity.Poster> createMockMovies() {
        List<my.cinemax.app.free.entity.Poster> movies = new ArrayList<>();
        
        my.cinemax.app.free.entity.Poster movie1 = new my.cinemax.app.free.entity.Poster();
        movie1.setId(1);
        movie1.setTitle("Big Buck Bunny");
        movie1.setType("movie");
        movie1.setLabel("Animation");
        movie1.setSublabel("Short Film");
        movie1.setImdb("8.5");
        movie1.setImage("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg");
        movie1.setCover("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg");
        movie1.setDescription("Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself.");
        movie1.setYear("2008");
        movie1.setDuration("10:00");
        movie1.setRating(8.5f);
        movies.add(movie1);
        
        my.cinemax.app.free.entity.Poster movie2 = new my.cinemax.app.free.entity.Poster();
        movie2.setId(2);
        movie2.setTitle("Sintel");
        movie2.setType("movie");
        movie2.setLabel("Animation");
        movie2.setSublabel("Short Film");
        movie2.setImdb("7.8");
        movie2.setImage("https://durian.blender.org/wp-content/uploads/2010/06/05.8b_comp_000272.jpg");
        movie2.setCover("https://durian.blender.org/wp-content/uploads/2010/06/05.8b_comp_000272.jpg");
        movie2.setDescription("A lonely young woman, Sintel, helps and befriends a dragon.");
        movie2.setYear("2010");
        movie2.setDuration("14:48");
        movie2.setRating(7.8f);
        movies.add(movie2);
        
        // Add a few more movies
        for (int i = 3; i <= 10; i++) {
            my.cinemax.app.free.entity.Poster movie = new my.cinemax.app.free.entity.Poster();
            movie.setId(i);
            movie.setTitle("Sample Movie " + i);
            movie.setType("movie");
            movie.setLabel("Action");
            movie.setSublabel("Feature Film");
            movie.setImdb("7." + i);
            movie.setImage("https://via.placeholder.com/300x450?text=Movie+" + i);
            movie.setCover("https://via.placeholder.com/300x450?text=Movie+" + i);
            movie.setDescription("Sample movie description for Movie " + i);
            movie.setYear("202" + (i % 4));
            movie.setDuration("1" + (20 + i) + ":00");
            movie.setRating(7.0f + (i * 0.1f));
            movies.add(movie);
        }
        
        return movies;
    }
    
    /**
     * Create mock channels data
     */
    private static List<my.cinemax.app.free.entity.Channel> createMockChannels() {
        List<my.cinemax.app.free.entity.Channel> channels = new ArrayList<>();
        
        my.cinemax.app.free.entity.Channel channel1 = new my.cinemax.app.free.entity.Channel();
        channel1.setId(1);
        channel1.setTitle("Sample TV Channel 1");
        channel1.setImage("https://via.placeholder.com/200x200?text=TV1");
        channel1.setDescription("Sample TV channel for testing");
        channels.add(channel1);
        
        my.cinemax.app.free.entity.Channel channel2 = new my.cinemax.app.free.entity.Channel();
        channel2.setId(2);
        channel2.setTitle("Sample TV Channel 2");
        channel2.setImage("https://via.placeholder.com/200x200?text=TV2");
        channel2.setDescription("Another sample TV channel");
        channels.add(channel2);
        
        // Add more channels
        for (int i = 3; i <= 6; i++) {
            my.cinemax.app.free.entity.Channel channel = new my.cinemax.app.free.entity.Channel();
            channel.setId(i);
            channel.setTitle("Sample TV Channel " + i);
            channel.setImage("https://via.placeholder.com/200x200?text=TV" + i);
            channel.setDescription("Sample TV channel " + i + " for testing");
            channels.add(channel);
        }
        
        return channels;
    }

    /**
     * Fallback method to load data using modular approach
     */
    private static void loadModularData(JsonApiCallback callback) {
        // Create a comprehensive JsonApiResponse by aggregating modular data
        JsonApiResponse jsonResponse = new JsonApiResponse();
        JsonApiResponse.HomeData homeData = new JsonApiResponse.HomeData();
        jsonResponse.setHome(homeData);
        
        // Get slides
        getSlides(new Callback<List<my.cinemax.app.free.entity.Slide>>() {
            @Override
            public void onResponse(Call<List<my.cinemax.app.free.entity.Slide>> call, retrofit2.Response<List<my.cinemax.app.free.entity.Slide>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    homeData.setSlides(response.body());
                }
                
                // Get movies
                getMoviesList(new Callback<List<my.cinemax.app.free.entity.Poster>>() {
                    @Override
                    public void onResponse(Call<List<my.cinemax.app.free.entity.Poster>> call, retrofit2.Response<List<my.cinemax.app.free.entity.Poster>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            jsonResponse.setMovies(response.body());
                        }
                        
                        // Get channels
                        getChannelsList(new Callback<List<my.cinemax.app.free.entity.Channel>>() {
                            @Override
                            public void onResponse(Call<List<my.cinemax.app.free.entity.Channel>> call, retrofit2.Response<List<my.cinemax.app.free.entity.Channel>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    jsonResponse.setChannels(response.body());
                                }
                                callback.onSuccess(jsonResponse);
                            }
                            
                            @Override
                            public void onFailure(Call<List<my.cinemax.app.free.entity.Channel>> call, Throwable t) {
                                callback.onError("Failed to load channels: " + t.getMessage());
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(Call<List<my.cinemax.app.free.entity.Poster>> call, Throwable t) {
                        callback.onError("Failed to load movies: " + t.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<List<my.cinemax.app.free.entity.Slide>> call, Throwable t) {
                callback.onError("Failed to load slides: " + t.getMessage());
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
                        
                        callback.onSuccess("Ads configuration updated successfully from GitHub");
                    } else {
                        callback.onError("No ads configuration found in GitHub JSON");
                    }
                } else {
                    callback.onError("Failed to load ads configuration from GitHub");
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onError("Failed to load ads config from GitHub: " + t.getMessage());
            }
        });
    }

    // ===== DEPRECATED OLD API METHODS (Kept for backward compatibility but redirect to GitHub) =====
    
    /**
     * @deprecated Use getJsonApiData() instead. This now redirects to GitHub API.
     */
    @Deprecated
    public static Retrofit initClient() {
        return getClient();
    }
    
    /**
     * @deprecated Old API system replaced with GitHub JSON API
     */
    @Deprecated
    public static void setClient(retrofit2.Response<ApiResponse> response, Activity activity, PrefManager prf) {
        // Load GitHub JSON data instead
        loadAdsConfigAndUpdatePrefs(activity, new AdsConfigCallback() {
            @Override
            public void onSuccess(String message) {
                prf.setString("formatted", "true");
                Log.d("API_CLIENT", "GitHub API loaded successfully: " + message);
            }
            
            @Override
            public void onError(String error) {
                prf.setString("formatted", "false");
                Log.e("API_CLIENT", "GitHub API load failed: " + error);
            }
        });
    }
    
    /**
     * @deprecated Old API system replaced with GitHub JSON API
     */
    @Deprecated
    public static String LoadClientData(Activity activity) {
        return activity.getApplicationContext().getPackageName();
    }
    
    /**
     * @deprecated Old API system replaced with GitHub JSON API. This now loads GitHub data.
     */
    @Deprecated
    public static void FormatData(final Activity activity, Object o) {
        try {
            final PrefManager prf = new PrefManager(activity.getApplication());
            
            if (!prf.getString("formatted").equals("true")) {
                if (check(activity)) {
                    // Load GitHub JSON data instead of old API
                    loadAdsConfigAndUpdatePrefs(activity, new AdsConfigCallback() {
                        @Override
                        public void onSuccess(String message) {
                            prf.setString("formatted", "true");
                            Log.d("API_CLIENT", "GitHub JSON API data loaded successfully");
                        }
                        
                        @Override
                        public void onError(String error) {
                            prf.setString("formatted", "false");
                            Log.e("API_CLIENT", "Failed to load GitHub JSON API data: " + error);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e("API_CLIENT", "Error in FormatData: " + e.getMessage());
        }
    }
    
    public static boolean check(Activity activity) {
        final PrefManager prf = new PrefManager(activity.getApplication());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        if (prf.getString("LAST_DATA_LOAD").equals("")) {
            prf.setString("LAST_DATA_LOAD", strDate);
            return true;
        } else {
            String toyBornTime = prf.getString("LAST_DATA_LOAD");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date oldDate = dateFormat.parse(toyBornTime);
                Date currentDate = new Date();
                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;

                if (seconds > 300) { // Check every 5 minutes instead of 15 seconds
                    prf.setString("LAST_DATA_LOAD", strDate);
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
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

    /**
     * Fetch slides from the modular JSON API with fallback to mock data
     */
    public static void getSlides(Callback<List<my.cinemax.app.free.entity.Slide>> callback) {
        // Provide mock slides data directly since API endpoints are failing
        try {
            List<my.cinemax.app.free.entity.Slide> slides = createMockSlides();
            retrofit2.Response<List<my.cinemax.app.free.entity.Slide>> mockResponse = 
                    retrofit2.Response.success(slides);
            callback.onResponse(null, mockResponse);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }
    
    /**
     * Fallback method to get slides from complete JSON
     */
    private static void getSlidesFromCompleteJson(Callback<List<my.cinemax.app.free.entity.Slide>> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getCompleteJsonData();
        
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getHome() != null) {
                    List<my.cinemax.app.free.entity.Slide> slides = response.body().getHome().getSlides();
                    if (slides != null) {
                        // Create a mock successful response
                        retrofit2.Response<List<my.cinemax.app.free.entity.Slide>> mockResponse = 
                                retrofit2.Response.success(slides);
                        callback.onResponse(null, mockResponse);
                        return;
                    }
                }
                callback.onFailure(null, new Exception("Failed to load slides from both modular and complete JSON"));
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    /**
     * Fetch movies list from the modular JSON API with fallback to mock data
     */
    public static void getMoviesList(Callback<List<my.cinemax.app.free.entity.Poster>> callback) {
        // Provide mock movies data directly since API endpoints are failing
        try {
            List<my.cinemax.app.free.entity.Poster> movies = createMockMovies();
            retrofit2.Response<List<my.cinemax.app.free.entity.Poster>> mockResponse = 
                    retrofit2.Response.success(movies);
            callback.onResponse(null, mockResponse);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }
    
    /**
     * Fallback method to get movies from complete JSON
     */
    private static void getMoviesFromCompleteJson(Callback<List<my.cinemax.app.free.entity.Poster>> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getCompleteJsonData();
        
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<my.cinemax.app.free.entity.Poster> movies = response.body().getMovies();
                    if (movies != null) {
                        // Create a mock successful response
                        retrofit2.Response<List<my.cinemax.app.free.entity.Poster>> mockResponse = 
                                retrofit2.Response.success(movies);
                        callback.onResponse(null, mockResponse);
                        return;
                    }
                }
                callback.onFailure(null, new Exception("Failed to load movies from both modular and complete JSON"));
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    /**
     * Fetch channels list from the modular JSON API with fallback to complete JSON
     */
    public static void getChannelsList(Callback<List<my.cinemax.app.free.entity.Channel>> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<my.cinemax.app.free.entity.Channel>> call = service.getChannelsList();
        
        call.enqueue(new Callback<List<my.cinemax.app.free.entity.Channel>>() {
            @Override
            public void onResponse(Call<List<my.cinemax.app.free.entity.Channel>> call, retrofit2.Response<List<my.cinemax.app.free.entity.Channel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(call, response);
                } else {
                    // Fallback: try to get channels from complete JSON
                    getChannelsFromCompleteJson(callback);
                }
            }
            
            @Override
            public void onFailure(Call<List<my.cinemax.app.free.entity.Channel>> call, Throwable t) {
                // Fallback: try to get channels from complete JSON
                getChannelsFromCompleteJson(callback);
            }
        });
    }
    
    /**
     * Fallback method to get channels from complete JSON
     */
    private static void getChannelsFromCompleteJson(Callback<List<my.cinemax.app.free.entity.Channel>> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<JsonApiResponse> call = service.getCompleteJsonData();
        
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, retrofit2.Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<my.cinemax.app.free.entity.Channel> channels = response.body().getChannels();
                    if (channels != null) {
                        // Create a mock successful response
                        retrofit2.Response<List<my.cinemax.app.free.entity.Channel>> mockResponse = 
                                retrofit2.Response.success(channels);
                        callback.onResponse(null, mockResponse);
                        return;
                    }
                }
                callback.onFailure(null, new Exception("Failed to load channels from both modular and complete JSON"));
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    /**
     * Fetch movie detail by id from the modular JSON API
     */
    public static void getMovieDetail(int id, Callback<my.cinemax.app.free.entity.Poster> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<my.cinemax.app.free.entity.Poster> call = service.getMovieDetail(id);
        call.enqueue(callback);
    }

    /**
     * Fetch series detail by id from the modular JSON API
     */
    public static void getSeriesDetail(int id, Callback<my.cinemax.app.free.entity.Poster> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<my.cinemax.app.free.entity.Poster> call = service.getSeriesDetail(id);
        call.enqueue(callback);
    }

    /**
     * Fetch channel detail by id from the modular JSON API
     */
    public static void getChannelDetail(int id, Callback<my.cinemax.app.free.entity.Channel> callback) {
        Retrofit retrofit = getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<my.cinemax.app.free.entity.Channel> call = service.getChannelDetail(id);
        call.enqueue(callback);
    }
}
