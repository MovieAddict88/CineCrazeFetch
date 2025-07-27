package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
// Removed unused retrofit imports

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


// Removed billing imports
import com.greenfrvr.rubberloader.RubberLoaderView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
// Removed old API imports
import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.ApiResponse;

// Removed unused imports
import java.util.Timer;
import java.util.TimerTask;
import my.cinemax.app.free.*;

public class SplashActivity extends AppCompatActivity {

    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // Removed check() - no longer needed
        prf= new PrefManager(getApplicationContext());
        ( (RubberLoaderView) findViewById(R.id.loader1)).startLoading();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Skip API check and go directly to main activity
                        redirect();
                    }
                });
            }
        }, 3000);

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
        prf.setString("SUBSCRIBED","TRUE"); // All features are now free
        prf.setString("NEW_SUBSCRIBE_ENABLED","TRUE"); // All features are now free

    }

    // Removed checkAccount() method - no longer needed with TMDB integration



    public void redirect(){
        if (!prf.getString("first").equals("true")) {
            Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
            prf.setString("first","true");
        }else{
            if (prf.getString("APP_LOGIN_REQUIRED").toString().equals("TRUE")){
                if (prf.getString("LOGGED").toString().equals("TRUE")){
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }else{
                    Intent intent= new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    finish();
                }
            }else{
                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }
    }



}
