package my.cinemax.app.free.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.FallbackDataService;

public class FallbackTestActivity extends AppCompatActivity {
    
    private TextView resultText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        resultText = findViewById(R.id.result_text);
        resultText.setText("Testing Fallback Data Service...");
        
        // Test fallback data service
        FallbackDataService.getHomeData(new FallbackDataService.HomeDataCallback() {
            @Override
            public void onSuccess(my.cinemax.app.free.entity.Data data) {
                Log.d("FallbackTestActivity", "Success! Got " + (data.getPosters() != null ? data.getPosters().size() : 0) + " posters");
                runOnUiThread(() -> {
                    resultText.setText("Success! Got " + (data.getPosters() != null ? data.getPosters().size() : 0) + " posters from fallback service");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("FallbackTestActivity", "Error: " + error);
                runOnUiThread(() -> {
                    resultText.setText("Error: " + error);
                });
            }
        });
    }
    
    public void testAgain(android.view.View view) {
        resultText.setText("Testing Fallback Data Service again...");
        
        FallbackDataService.getHomeData(new FallbackDataService.HomeDataCallback() {
            @Override
            public void onSuccess(my.cinemax.app.free.entity.Data data) {
                Log.d("FallbackTestActivity", "Success! Got " + (data.getPosters() != null ? data.getPosters().size() : 0) + " posters");
                runOnUiThread(() -> {
                    resultText.setText("Success! Got " + (data.getPosters() != null ? data.getPosters().size() : 0) + " posters from fallback service");
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("FallbackTestActivity", "Error: " + error);
                runOnUiThread(() -> {
                    resultText.setText("Error: " + error);
                });
            }
        });
    }
}