package my.cinemax.app.free.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import my.cinemax.app.free.R;
import my.cinemax.app.free.api.PlaylistApiClient;
import my.cinemax.app.free.api.PlaylistApiRest;
import my.cinemax.app.free.entity.PlaylistData;
import my.cinemax.app.free.Utils.PlaylistDataConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestPlaylistActivity extends AppCompatActivity {
    private static final String TAG = "TestPlaylistActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_playlist);
        
        textView = findViewById(R.id.text_view_test);
        
        // Test the new playlist API
        testPlaylistApi();
    }
    
    private void testPlaylistApi() {
        textView.setText("Testing playlist API...");
        
        Retrofit retrofit = PlaylistApiClient.getClient();
        PlaylistApiRest service = retrofit.create(PlaylistApiRest.class);
        Call<PlaylistData> call = service.getPlaylistData();
        
        call.enqueue(new Callback<PlaylistData>() {
            @Override
            public void onResponse(Call<PlaylistData> call, Response<PlaylistData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaylistData playlistData = response.body();
                    Log.d(TAG, "Playlist data loaded successfully");
                    Log.d(TAG, "Categories count: " + playlistData.getCategories().size());
                    
                    // Convert to app data format
                    my.cinemax.app.free.entity.Data appData = PlaylistDataConverter.convertPlaylistDataToAppData(playlistData);
                    
                    StringBuilder result = new StringBuilder();
                    result.append("✅ Playlist API Test Successful!\n\n");
                    result.append("Categories: ").append(playlistData.getCategories().size()).append("\n");
                    
                    if (appData.getChannels() != null) {
                        result.append("Live TV Channels: ").append(appData.getChannels().size()).append("\n");
                    }
                    
                    if (appData.getPosters() != null) {
                        result.append("Movies & Series: ").append(appData.getPosters().size()).append("\n");
                    }
                    
                    if (appData.getGenres() != null) {
                        result.append("Genres: ").append(appData.getGenres().size()).append("\n");
                    }
                    
                    textView.setText(result.toString());
                    
                } else {
                    Log.e(TAG, "Failed to load playlist data: " + response.code());
                    textView.setText("❌ Failed to load playlist data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PlaylistData> call, Throwable t) {
                Log.e(TAG, "Error loading playlist data", t);
                textView.setText("❌ Error: " + t.getMessage());
            }
        });
    }
}