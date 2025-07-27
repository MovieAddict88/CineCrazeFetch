package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;



import com.greenfrvr.rubberloader.RubberLoaderView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.JsonDataService;
import my.cinemax.app.free.entity.Data;

import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prf = new PrefManager(getApplicationContext());
        
        // Initialize subscription state as false (since we removed billing functionality)
        check();
        
        // Start loading animation
        ((RubberLoaderView) findViewById(R.id.loader1)).startLoading();
        
        // Reduce timer to 2 seconds for faster startup
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkAccount();
                    }
                });
            }
        }, 2000);

        prf.setString("ADMIN_REWARDED_ADMOB_ID","");

        prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID","");
        prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID","");
        prf.setString("ADMIN_INTERSTITIAL_TYPE","FALSE");
        prf.setInt("ADMIN_INTERSTITIAL_CLICKS",3);

        prf.setString("ADMIN_BANNER_ADMOB_ID","");
        prf.setString("ADMIN_BANNER_FACEBOOK_ID","");
        prf.setString("ADMIN_BANNER_TYPE","FALSE");

        prf.setString("ADMIN_NATIVE_FACEBOOK_ID","");
        prf.setString("ADMIN_NATIVE_ADMOB_ID","");
        prf.setString("ADMIN_NATIVE_LINES","6");
        prf.setString("ADMIN_NATIVE_TYPE","FALSE");
        prf.setString("APP_STRIPE_ENABLED","FALSE");
        prf.setString("APP_PAYPAL_ENABLED","FALSE");
        prf.setString("APP_PAYPAL_CLIENT_ID","");
        prf.setString("APP_CASH_ENABLED","FALSE");
        prf.setString("APP_LOGIN_REQUIRED","FALSE");

    }

    public void check(){
        // In-app purchase check functionality removed due to missing dependencies
        PrefManager prefManager= new PrefManager(getApplicationContext());
        prefManager.setString("SUBSCRIBED","FALSE");
    }
    private void checkAccount() {
        Log.d("SplashActivity", "checkAccount() called");
        
        // Skip external API calls and use GitHub JSON data instead
        // Initialize basic app settings for offline operation
        prf.setString("APP_CURRENCY", "USD");
        prf.setString("NEW_SUBSCRIBE_ENABLED", "FALSE");
        
        try {
            // Use JsonDataService to load GitHub data
            JsonDataService jsonDataService = new JsonDataService(this);
            jsonDataService.loadHomeData(new JsonDataService.DataCallback() {
                @Override
                public void onSuccess(Data data) {
                    Log.d("SplashActivity", "Data loaded successfully from GitHub");
                    // Data loaded successfully from GitHub
                    // Store that data is available
                    prf.setString("DATA_LOADED", "TRUE");
                    redirect();
                }
                
                @Override
                public void onError(String error) {
                    Log.d("SplashActivity", "Data loading failed, using local assets: " + error);
                    // Even if GitHub data fails, continue to app
                    // The app can work with local assets
                    prf.setString("DATA_LOADED", "FALSE");
                    redirect();
                }
            });
        } catch (Exception e) {
            Log.e("SplashActivity", "Exception in JsonDataService: " + e.getMessage());
            // If anything fails, just continue to the app
            prf.setString("DATA_LOADED", "ERROR");
            redirect();
        }
        
        // Safety fallback timer - force redirect after 3 seconds max
        Timer fallbackTimer = new Timer();
        fallbackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SplashActivity", "Fallback timer triggered");
                        // Force redirect if still on splash screen
                        prf.setString("DATA_LOADED", "TIMEOUT");
                        redirect();
                    }
                });
            }
        }, 3000);
    }



    public void redirect(){
        Log.d("SplashActivity", "redirect() called");
        
        // Initialize first preference if not set
        if (prf.getString("first").isEmpty()) {
            prf.setString("first", "false");
        }
        
        Log.d("SplashActivity", "first preference: " + prf.getString("first"));
        
        if (!prf.getString("first").equals("true")) {
            Log.d("SplashActivity", "Going to IntroActivity");
            Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
            prf.setString("first","true");
        }else{
            // Initialize LOGGED preference if not set
            if (prf.getString("LOGGED").isEmpty()) {
                prf.setString("LOGGED", "FALSE");
            }
            
            Log.d("SplashActivity", "APP_LOGIN_REQUIRED: " + prf.getString("APP_LOGIN_REQUIRED"));
            Log.d("SplashActivity", "LOGGED: " + prf.getString("LOGGED"));
            
            if (prf.getString("APP_LOGIN_REQUIRED").toString().equals("TRUE")){
                if (prf.getString("LOGGED").toString().equals("TRUE")){
                    Log.d("SplashActivity", "Going to HomeActivity (logged in)");
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }else{
                    Log.d("SplashActivity", "Going to LoginActivity");
                    Intent intent= new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    finish();
                }
            }else{
                Log.d("SplashActivity", "Going to HomeActivity (no login required)");
                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }

    }



}
