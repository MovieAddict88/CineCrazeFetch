package my.cinemax.app.free.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.ApiResponse;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StripeActivity extends AppCompatActivity {

    private String paymentIntentClientSecret;
    private int plan_id;
    private ProgressDialog dialog_progress;
    private String plan_name;
    private Double price;
    private TextView text_view_activity_stripe_plan;
    private TextView text_view_activity_stripe_price;
    private RelativeLayout payButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        Bundle bundle = getIntent().getExtras() ;
        this.plan_id =  bundle.getInt("plan");
        this.plan_name =  bundle.getString("name");
        this.price =  bundle.getDouble("price");
        initView();
        initAction();
    }

    private void initAction() {
        payButton.setOnClickListener((View view) -> {
            Toasty.error(StripeActivity.this, "Stripe payment is currently disabled", Toast.LENGTH_SHORT).show();
        });
    }

    private void initView() {
        this.text_view_activity_stripe_price = (TextView) findViewById(R.id.text_view_activity_stripe_price);
        this.text_view_activity_stripe_plan = (TextView) findViewById(R.id.text_view_activity_stripe_plan);
        this.payButton = findViewById(R.id.payButton);

        text_view_activity_stripe_plan.setText(plan_name);
        text_view_activity_stripe_price.setText(price+" "+ new PrefManager(getApplicationContext()).getString("APP_CURRENCY"));
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Stripe payment result handling removed due to missing dependencies
    }


    private void checkPayment(String trans_id){
        PrefManager prf= new PrefManager(StripeActivity.this.getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            dialog_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

            Integer id_user=  Integer.parseInt(prf.getString("ID_USER"));
            String   key_user=  prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.SubscriptionStripe(id_user,key_user,trans_id,plan_id);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getCode()==200){
                            Intent intent = new Intent(StripeActivity.this, FinishActivity.class);
                            intent.putExtra("title", response.body().getMessage());
                            startActivity(intent);
                            finish();
                            prf.setString("NEW_SUBSCRIBE_ENABLED","TRUE");
                        }else{
                            Intent intent = new Intent(StripeActivity.this, FinishActivity.class);
                            intent.putExtra("title", response.body().getMessage());
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        Toasty.error(StripeActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    dialog_progress.dismiss();
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    dialog_progress.dismiss();
                    Toasty.error(StripeActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }else{
            Intent intent = new Intent(StripeActivity.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        }
    }

}


