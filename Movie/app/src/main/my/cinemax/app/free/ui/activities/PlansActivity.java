package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.Plan;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlansActivity extends AppCompatActivity {

    private PlanAdapter planAdapter;
    private LinearLayout relative_layout_plans;
    private RelativeLayout relative_layout_loading;
    private RecyclerView recycler_view_plans;
    private RelativeLayout relative_layout_select_plan;

    private GridLayoutManager gridLayoutManager;
    private final List<Plan> planList = new ArrayList<>();
    private Integer selected_id = -1;
    private Integer selected_pos = -1;




    private String method = "null";
    private ProgressDialog dialog_progress;
    private TextView text_view_activity_plans_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        Bundle bundle = getIntent().getExtras() ;
        PrefManager prf= new PrefManager(PlansActivity.this.getApplicationContext());
        this.method =  bundle.getString("method");
        initView();
        initAction();
        loadPlans();
    }

    private void initView() {
            this.text_view_activity_plans_method = findViewById(R.id.text_view_activity_plans_method);
            this.relative_layout_select_plan = findViewById(R.id.relative_layout_select_plan);
            this.relative_layout_plans = findViewById(R.id.relative_layout_plans);
            this.relative_layout_loading = findViewById(R.id.relative_layout_loading);
            this.recycler_view_plans = findViewById(R.id.recycler_view_plans);
            this.gridLayoutManager = new GridLayoutManager(this, 1);
            this.planAdapter = new PlanAdapter();
            switch (method){
                case "pp":
                    text_view_activity_plans_method.setText("PayPal (Disabled)");
                    break;
                case "cc":
                    text_view_activity_plans_method.setText("Credit card (Disabled)");
                    break;
                case "cash":
                    text_view_activity_plans_method.setText("Cash");
                    break;

            }

    }

    private void initAction() {
        this.relative_layout_select_plan.setOnClickListener(v->{
            if (selected_id == -1){
                Toasty.error(getApplicationContext(),getResources().getString(R.string.select_plan),Toast.LENGTH_LONG).show();
            }else{
                switch (method){
                    case "pp":
                        Toasty.error(PlansActivity.this, "PayPal payment is currently disabled", Toast.LENGTH_SHORT).show();
                        break;
                    case "cc":
                        Toasty.error(PlansActivity.this, "Credit card payment is currently disabled", Toast.LENGTH_SHORT).show();
                        break;
                    case "cash":
                        Intent intent1 = new Intent(PlansActivity.this,CashActivity.class);
                        intent1.putExtra("plan",selected_id);
                        intent1.putExtra("name",planList.get(selected_pos).getTitle());
                        intent1.putExtra("price",planList.get(selected_pos).getPrice());
                        startActivity(intent1);
                        finish();
                        break;

                }
            }
        });
    }

    private void loadPlans() {
        relative_layout_plans.setVisibility(View.GONE);
        relative_layout_loading.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Plan>> call = service.getPlans();
        call.enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, final Response<List<Plan>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i <response.body().size() ; i++) {
                        planList.add(response.body().get(i));
                    }
                    relative_layout_plans.setVisibility(View.VISIBLE);
                    relative_layout_loading.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        planList.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            planList.add(response.body().get(i));
                        }
                    }
                    recycler_view_plans.setHasFixedSize(true);
                    recycler_view_plans.setLayoutManager(gridLayoutManager);
                    recycler_view_plans.setAdapter(planAdapter);
                    planAdapter.notifyDataSetChanged();
                }else{
                    relative_layout_plans.setVisibility(View.GONE);
                    relative_layout_loading.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                relative_layout_plans.setVisibility(View.GONE);
                relative_layout_loading.setVisibility(View.GONE);
            }
        });
    }

    public class PlanAdapter extends  RecyclerView.Adapter<PlanAdapter.PlanHolder>{


        @Override
        public PlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan,parent, false);
            PlanHolder mh = new PlanHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(PlanHolder holder, final int position) {
            holder.text_view_plan_discount.setVisibility(View.GONE);
            holder.text_view_plan_description.setVisibility(View.GONE);

            if (planList.get(position).getDiscount() !=  null){
                if (planList.get(position).getDiscount().length()>0){
                    holder.text_view_plan_discount.setVisibility(View.VISIBLE);
                    holder.text_view_plan_discount.setText(planList.get(position).getDiscount());
                }
            }
            if (planList.get(position).getDescription() !=  null){
                if (planList.get(position).getDescription().length()>0){
                    holder.text_view_plan_description.setVisibility(View.VISIBLE);
                    holder.text_view_plan_description.setText(planList.get(position).getDescription());
                }
            }
            holder.text_view_plan_title.setText(planList.get(position).getTitle());
            holder.text_view_plan_price.setText(planList.get(position).getPrice()+ " "+new PrefManager(getApplicationContext()).getString("APP_CURRENCY"));
            if(planList.get(position).getId()   == selected_id){
                holder.card_view_plan.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else{
                holder.card_view_plan.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            }
            holder.card_view_plan.setOnClickListener(v -> {
                selected_id = planList.get(position).getId();
                selected_pos = position;
                planAdapter.notifyDataSetChanged();
            });
        }
        @Override
        public int getItemCount() {
            return planList.size();
        }
        public class PlanHolder extends RecyclerView.ViewHolder {
            private final TextView text_view_plan_discount;
            private final CardView card_view_plan;
            private final TextView text_view_plan_title;
            private final TextView text_view_plan_description;
            private final TextView text_view_plan_price;

            public PlanHolder(View itemView) {
                super(itemView);
                this.text_view_plan_discount =  (TextView) itemView.findViewById(R.id.text_view_plan_discount);
                this.card_view_plan =  (CardView) itemView.findViewById(R.id.card_view_plan);
                this.text_view_plan_title =  (TextView) itemView.findViewById(R.id.text_view_plan_title);
                this.text_view_plan_description =  (TextView) itemView.findViewById(R.id.text_view_plan_description);
                this.text_view_plan_price =  (TextView) itemView.findViewById(R.id.text_view_plan_price);
            }
        }
    }




}
