package my.cinemax.app.free.api;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import my.cinemax.app.free.BuildConfig;
import my.cinemax.app.free.MyApplication;
import my.cinemax.app.free.entity.Actress;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.PlaylistData;
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

public class PlaylistApiClient {
    
    private static Retrofit playlistRetrofit = null;
    private static final String CACHE_CONTROL = "Cache-Control";

    public static Retrofit getPlaylistClient() {
        if (playlistRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .cache(provideCache())
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            playlistRetrofit = new Retrofit.Builder()
                    .baseUrl(Actress.actress)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return playlistRetrofit;
    }

    public static void getPlaylistData(Callback<Data> callback) {
        Retrofit retrofit = getPlaylistClient();
        PlaylistApiService service = retrofit.create(PlaylistApiService.class);
        Call<PlaylistData> call = service.getPlaylistData();
        
        call.enqueue(new Callback<PlaylistData>() {
            @Override
            public void onResponse(Call<PlaylistData> call, retrofit2.Response<PlaylistData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Store playlist data in data store for other parts of the app
                    PlaylistDataStore.getInstance().setPlaylistData(response.body());
                    
                    // Convert playlist data to app data format
                    Data appData = PlaylistDataAdapter.convertToAppData(response.body());
                    
                    // Create a successful response for the app data
                    retrofit2.Response<Data> dataResponse = retrofit2.Response.success(appData);
                    callback.onResponse(null, dataResponse);
                } else {
                    callback.onFailure(call, new Exception("Failed to load playlist data"));
                }
            }

            @Override
            public void onFailure(Call<PlaylistData> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(MyApplication.getInstance().getCacheDir(), "playlist-cache"),
                    10 * 1024 * 1024); // 10 MB
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
                        Log.v("PLAYLIST_API", message);
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
                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(30, TimeUnit.MINUTES)
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
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }
}