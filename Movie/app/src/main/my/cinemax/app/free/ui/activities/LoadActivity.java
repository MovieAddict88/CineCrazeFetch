package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.greenfrvr.rubberloader.RubberLoaderView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;

import java.util.Timer;
import java.util.TimerTask;
import my.cinemax.app.free.MyApi;

public class LoadActivity extends AppCompatActivity {

    private PrefManager prf;



    private  Integer id;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int time = 3000;
        Uri data = this.getIntent().getData();
        if (data==null){
            Bundle bundle = getIntent().getExtras() ;
            if (bundle!=null) {
                this.id = bundle.getInt("id");
                this.type = bundle.getString("type");
                time = 2000;
            }
        }else{
            if (data.getPath().contains("/c/share/")){
                this.id=Integer.parseInt(data.getPath().replace("/c/share/","").replace(".html",""));
                this.type = "channel";
                time = 2000;
            }else{
                this.id=Integer.parseInt(data.getPath().replace("/share/","").replace(".html",""));
                this.type = "poster";
                time = 2000;
            }
        }



        prf= new PrefManager(getApplicationContext());
        ( (RubberLoaderView) findViewById(R.id.loader1)).startLoading();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                LoadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        checkAccount();

                    }
                });
            }
        }, time);

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
        prf.setString("APP_CASH_ENABLED","FALSE");
        prf.setString("APP_LOGIN_REQUIRED","FALSE");
    }


    private void checkAccount() {

        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version!=-1){
            // Skip old API call - use JSON configuration instead
            // Integer id_user = 0;
            // if (prf.getString("LOGGED").toString().equals("TRUE")) {
            //     id_user = Integer.parseInt(prf.getString("ID_USER"));
            // }
            // Retrofit retrofit = apiClient.getClient();
            // apiRest service = retrofit.create(apiRest.class);
            // Call<ApiResponse> call = service.check(version,id_user);
            
            // Set default values for ads configuration
            prf.setString("ADMIN_REWARDED_ADMOB_ID", "ca-app-pub-3940256099942544/5224354917");
            prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID", "ca-app-pub-3940256099942544/1033173712");
            prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
            prf.setString("ADMIN_INTERSTITIAL_TYPE", "ADMOB");
            prf.setInt("ADMIN_INTERSTITIAL_CLICKS", 3);
            prf.setString("ADMIN_BANNER_ADMOB_ID", "ca-app-pub-3940256099942544/6300978111");
            prf.setString("ADMIN_BANNER_FACEBOOK_ID", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
            prf.setString("ADMIN_BANNER_TYPE", "ADMOB");
            prf.setString("ADMIN_NATIVE_FACEBOOK_ID", "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
            prf.setString("ADMIN_NATIVE_ADMOB_ID", "ca-app-pub-3940256099942544/2247696110");
            prf.setString("ADMIN_NATIVE_LINES", "6");
            prf.setString("ADMIN_NATIVE_TYPE", "ADMOB");
            prf.setString("APP_CURRENCY", "USD");
            prf.setString("APP_CASH_ACCOUNT", "test@example.com");
            prf.setString("APP_STRIPE_PUBLIC_KEY", "pk_test_51H1234567890");
            prf.setString("APP_CASH_ENABLED", "FALSE");
            prf.setString("APP_PAYPAL_ENABLED", "FALSE");
            prf.setString("APP_STRIPE_ENABLED", "FALSE");
            prf.setString("APP_LOGIN_REQUIRED", "FALSE");
            prf.setString("NEW_SUBSCRIBE_ENABLED", "FALSE");
            
            // Skip the API call and go directly to success
            // Call<ApiResponse> call = service.check(version,id_user);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()){
                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_REWARDED_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_REWARDED_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_CLICKS") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setInt("ADMIN_INTERSTITIAL_CLICKS",Integer.parseInt(response.body().getValues().get(i).getValue()));
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_LINES") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_LINES",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("APP_CURRENCY") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_CURRENCY",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("APP_CASH_ACCOUNT") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_CASH_ACCOUNT",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("APP_STRIPE_PUBLIC_KEY") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_STRIPE_PUBLIC_KEY",response.body().getValues().get(i).getValue());
                            }

                            if ( response.body().getValues().get(i).getName().equals("APP_CASH_ENABLED") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_CASH_ENABLED",response.body().getValues().get(i).getValue());
                            }

                            if ( response.body().getValues().get(i).getName().equals("APP_PAYPAL_ENABLED") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_PAYPAL_ENABLED",response.body().getValues().get(i).getValue());
                            }


                            if ( response.body().getValues().get(i).getName().equals("APP_STRIPE_ENABLED") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_STRIPE_ENABLED",response.body().getValues().get(i).getValue());
                            }

                            if ( response.body().getValues().get(i).getName().equals("APP_LOGIN_REQUIRED") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("APP_LOGIN_REQUIRED",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("subscription") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("NEW_SUBSCRIBE_ENABLED",response.body().getValues().get(i).getValue());
                            }
                        }
                        if (response.body().getValues().get(1).getValue().equals("403")){
                            prf.remove("ID_USER");
                            prf.remove("SALT_USER");
                            prf.remove("TOKEN_USER");
                            prf.remove("NAME_USER");
                            prf.remove("TYPE_USER");
                            prf.remove("USERN_USER");
                            prf.remove("IMAGE_USER");
                            prf.remove("LOGGED");
                            prf.remove("NEW_SUBSCRIBE_ENABLED");
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                        if (id!=null && type !=null){
                            if (type.equals("poster"))
                                getPoster();
                            if (type.equals("channel"))
                                getChannel();
                        }else{
                            if (response.body().getCode().equals(200)) {
                                if (!prf.getString("first").equals("true")){
                                    Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                    prf.setString("first","true");
                                }else{
                                    Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            }else if (response.body().getCode().equals(202)) {
                                String title_update=response.body().getValues().get(0).getValue();
                                String featurs_update=response.body().getMessage();
                                View v = (View)  getLayoutInflater().inflate(R.layout.update_message,null);
                                TextView update_text_view_title=(TextView) v.findViewById(R.id.update_text_view_title);
                                TextView update_text_view_updates=(TextView) v.findViewById(R.id.update_text_view_updates);
                                update_text_view_title.setText(title_update);
                                update_text_view_updates.setText(featurs_update);
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(LoadActivity.this);
                                builder.setTitle("New Update")
                                        //.setMessage(response.body().getValue())
                                        .setView(v)
                                        .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                final String appPackageName=getApplication().getPackageName();
											    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyApi.API_URL)));
                                                finish();
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.skip), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (!prf.getString("first").equals("true")){
                                                    Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                                    finish();
                                                    prf.setString("first","true");
                                                }else{
                                                    Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                                    finish();
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .setIcon(R.drawable.ic_update)
                                        .show();
                            } else {
                                if (!prf.getString("first").equals("true")){
                                    Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                    prf.setString("first","true");
                                }else{
                                    Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            }
                        }
                    }else {

                        if (id!=null && type !=null){
                            if (type.equals("poster"))
                                getPoster();
                            if (type.equals("channel"))
                                getChannel();
                        }else{
                            if (!prf.getString("first").equals("true")){
                                Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                                prf.setString("first","true");
                            }else{
                                Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                    if (id!=null && type !=null){
                        if (type.equals("poster"))
                            getPoster();
                        if (type.equals("channel"))
                            getChannel();
                    }else{
                        if (!prf.getString("first").equals("true")){
                            Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                            prf.setString("first","true");
                        }else{
                            Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                    }
                }
            });

        }else{

            if (id!=null && type !=null){
                if (type.equals("poster"))
                    getPoster();
                if (type.equals("channel"))
                    getChannel();
            }else{
                if (!prf.getString("first").equals("true")){
                    Intent intent = new Intent(LoadActivity.this,IntroActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                    prf.setString("first","true");
                }else{
                    Intent intent = new Intent(LoadActivity.this,HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        }

    }




    public void getPoster(){
        // Use GitHub JSON API to get movie/series by ID
        apiClient.getJsonApiData(new retrofit2.Callback<my.cinemax.app.free.entity.JsonApiResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Response<my.cinemax.app.free.entity.JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    my.cinemax.app.free.entity.JsonApiResponse apiResponse = response.body();
                    
                    // Search for the poster/movie by ID
                    if (apiResponse.getMovies() != null) {
                        for (Poster poster : apiResponse.getMovies()) {
                            if (poster.getId() == id) {
                                if (poster.getType().equals("serie") || poster.getType().equals("series")) {
                                    Intent in = new Intent(LoadActivity.this, SerieActivity.class);
                                    in.putExtra("poster", poster);
                                    in.putExtra("from", "true");
                                    startActivity(in);
                                    finish();
                                    return;
                                } else if (poster.getType().equals("movie")) {
                                    Intent in = new Intent(LoadActivity.this, MovieActivity.class);
                                    in.putExtra("poster", poster);
                                    in.putExtra("from", "true");
                                    startActivity(in);
                                    finish();
                                    return;
                                }
                            }
                        }
                    }
                    
                    // If not found in movies, try featured movies in home section
                    if (apiResponse.getHome() != null && apiResponse.getHome().getFeaturedMovies() != null) {
                        for (Poster poster : apiResponse.getHome().getFeaturedMovies()) {
                            if (poster.getId() == id) {
                                if (poster.getType().equals("serie") || poster.getType().equals("series")) {
                                    Intent in = new Intent(LoadActivity.this, SerieActivity.class);
                                    in.putExtra("poster", poster);
                                    in.putExtra("from", "true");
                                    startActivity(in);
                                    finish();
                                    return;
                                } else if (poster.getType().equals("movie")) {
                                    Intent in = new Intent(LoadActivity.this, MovieActivity.class);
                                    in.putExtra("poster", poster);
                                    in.putExtra("from", "true");
                                    startActivity(in);
                                    finish();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
    public void getChannel(){
        // Use GitHub JSON API to get channel by ID
        apiClient.getJsonApiData(new retrofit2.Callback<my.cinemax.app.free.entity.JsonApiResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Response<my.cinemax.app.free.entity.JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    my.cinemax.app.free.entity.JsonApiResponse apiResponse = response.body();
                    
                    // Search for the channel by ID
                    if (apiResponse.getChannels() != null) {
                        for (Channel channel : apiResponse.getChannels()) {
                            if (channel.getId() == id) {
                                Intent in = new Intent(LoadActivity.this, ChannelActivity.class);
                                in.putExtra("channel", channel);
                                in.putExtra("from", "true");
                                startActivity(in);
                                finish();
                                return;
                            }
                        }
                    }
                    
                    // If not found in main channels, try channels in home section
                    if (apiResponse.getHome() != null && apiResponse.getHome().getChannels() != null) {
                        for (Channel channel : apiResponse.getHome().getChannels()) {
                            if (channel.getId() == id) {
                                Intent in = new Intent(LoadActivity.this, ChannelActivity.class);
                                in.putExtra("channel", channel);
                                in.putExtra("from", "true");
                                startActivity(in);
                                finish();
                                return;
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Throwable t) {
                // Handle error
            }
        });
    }

}
