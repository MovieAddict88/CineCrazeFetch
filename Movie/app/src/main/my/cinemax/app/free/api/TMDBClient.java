package my.cinemax.app.free.api;

import android.app.Activity;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import my.cinemax.app.free.BuildConfig;
import my.cinemax.app.free.MyApplication;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.entity.ApiResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * TMDB API Client for movie data
 */
public class TMDBClient {
    private static Retrofit retrofit = null;
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String TMDB_API_KEY = "871c8ec045dba340e55b032a0546948c";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public static String getImageUrl(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        if (path.startsWith("http")) {
            return path;
        }
        return TMDB_IMAGE_BASE_URL + path;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .addInterceptor(provideHttpLoggingInterceptor())
                        .addInterceptor(provideOfflineCacheInterceptor())
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .addInterceptor(provideApiKeyInterceptor())
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
                        .baseUrl(TMDB_BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to basic configuration
                retrofit = new Retrofit.Builder()
                        .baseUrl(TMDB_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }

    private static Interceptor provideApiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String originalHttpUrl = original.url().toString();

                String url = originalHttpUrl;
                if (url.contains("?")) {
                    url += "&api_key=" + TMDB_API_KEY;
                } else {
                    url += "?api_key=" + TMDB_API_KEY;
                }

                Request request = original.newBuilder()
                        .url(url)
                        .build();

                return chain.proceed(request);
            }
        };
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(MyApplication.getInstance().getCacheDir(), "tmdb-cache"),
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
                        Log.v("TMDB_API", message);
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
                        .maxAge(2, TimeUnit.SECONDS)
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
                            .maxStale(30, TimeUnit.DAYS)
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