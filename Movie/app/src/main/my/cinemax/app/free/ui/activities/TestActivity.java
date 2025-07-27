package my.cinemax.app.free.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.TMDBService;

public class TestActivity extends AppCompatActivity {
    
    private TextView resultText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        resultText = findViewById(R.id.result_text);
        resultText.setText("Testing TMDB API...");
        
        // Test TMDB API
        TMDBService.getInstance().getPopularMovies(1, new TMDBService.MovieListCallback() {
            @Override
            public void onSuccess(java.util.List<my.cinemax.app.free.entity.Poster> movies) {
                Log.d("TestActivity", "Success! Got " + movies.size() + " movies");
                runOnUiThread(() -> {
                    resultText.setText("Success! Got " + movies.size() + " movies from TMDB");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("TestActivity", "Error: " + error);
                runOnUiThread(() -> {
                    resultText.setText("Error: " + error);
                });
            }
        });
    }
    
    public void testAgain(android.view.View view) {
        resultText.setText("Testing TMDB API again...");
        
        TMDBService.getInstance().getPopularMovies(1, new TMDBService.MovieListCallback() {
            @Override
            public void onSuccess(java.util.List<my.cinemax.app.free.entity.Poster> movies) {
                Log.d("TestActivity", "Success! Got " + movies.size() + " movies");
                runOnUiThread(() -> {
                    resultText.setText("Success! Got " + movies.size() + " movies from TMDB");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("TestActivity", "Error: " + error);
                runOnUiThread(() -> {
                    resultText.setText("Error: " + error);
                });
            }
        });
    }
}