package my.cinemax.app.free.ui.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
// Removed unused retrofit imports

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.HybridDataService;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.ui.Adapters.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_home_fragment;
    private LinearLayout linear_layout_load_home_fragment;
    private LinearLayout linear_layout_page_error_home_fragment;
    private RecyclerView recycler_view_home_fragment;
    private RelativeLayout relative_layout_load_more_home_fragment;
    private HomeAdapter homeAdapter;



    private Genre my_genre_list;
    private List<Data> dataList=new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private Button button_try_again;


    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0 ;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view=  inflater.inflate(R.layout.fragment_home, container, false);
        prefManager= new PrefManager(getApplicationContext());

        initViews();
        initActions();
        loadData();
        return view;
    }

    private void loadData() {
        showLoadingView();
        
        // Add logging to debug
        android.util.Log.d("HomeFragment", "Starting to load hybrid data...");
        
        HybridDataService.getHomeData(new HybridDataService.HomeDataCallback() {
            @Override
            public void onSuccess(Data data) {
                android.util.Log.d("HomeFragment", "Hybrid data loaded successfully");
                dataList.clear();
                dataList.add(new Data().setViewType(0)); // Header view
                
                // Add popular movies section
                if (data.getPosters() != null && data.getPosters().size() > 0) {
                    Data moviesData = new Data();
                    moviesData.setPosters(data.getPosters());
                    dataList.add(moviesData);
                }
                
                // Get genres and add genre sections
                HybridDataService.getMovieGenres(new HybridDataService.GenreListCallback() {
                    @Override
                    public void onSuccess(List<Genre> genres) {
                        for (Genre genre : genres) {
                            Data genreData = new Data();
                            genreData.setGenre(genre);
                            dataList.add(genreData);
                            
                            // Add ads if enabled
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        dataList.add(new Data().setViewType(5));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        dataList.add(new Data().setViewType(6));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                        if (type_ads == 0) {
                                            dataList.add(new Data().setViewType(5));
                                            type_ads = 1;
                                        } else if (type_ads == 1) {
                                            dataList.add(new Data().setViewType(6));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }
                        }
                        
                        showListView();
                        homeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.w("HomeFragment", "Genre loading failed, showing basic data");
                        showListView();
                        homeAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("HomeFragment", "Hybrid error: " + error + ", falling back to TMDB");
                // Fallback to TMDB data when hybrid fails
                loadTMDBData();
            }
        });
    }
    
    private void loadTMDBData() {
        android.util.Log.d("HomeFragment", "Loading TMDB fallback data...");
        my.cinemax.app.free.api.TMDBService.getInstance().getHomeData(new my.cinemax.app.free.api.TMDBService.HomeDataCallback() {
            @Override
            public void onSuccess(Data data) {
                android.util.Log.d("HomeFragment", "TMDB data loaded successfully");
                dataList.clear();
                dataList.add(new Data().setViewType(0)); // Header view
                
                if (data.getPosters() != null && data.getPosters().size() > 0) {
                    Data moviesData = new Data();
                    moviesData.setPosters(data.getPosters());
                    dataList.add(moviesData);
                }
                
                showListView();
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("HomeFragment", "TMDB fallback failed: " + error);
                showErrorView();
            }
        });
    }
   private void showLoadingView(){
       linear_layout_load_home_fragment.setVisibility(View.VISIBLE);
       linear_layout_page_error_home_fragment.setVisibility(View.GONE);
       recycler_view_home_fragment.setVisibility(View.GONE);
   }
    private void showListView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        recycler_view_home_fragment.setVisibility(View.VISIBLE);
    }
    private void showErrorView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.VISIBLE);
        recycler_view_home_fragment.setVisibility(View.GONE);
    }
    private void initActions() {
        swipe_refresh_layout_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                swipe_refresh_layout_home_fragment.setRefreshing(false);
            }
        });
        button_try_again.setOnClickListener(v->{
            loadData();
        });
    }
    public boolean checkSUBSCRIBED(){
        return true; // All features are now free
    }
    private void initViews() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        
        // Safely handle preference values with defaults
        String nativeType = prefManager.getString("ADMIN_NATIVE_TYPE");
        if (nativeType == null || nativeType.isEmpty()) {
            nativeType = "FALSE";
        }
        
        if (!nativeType.equals("FALSE")){
            native_ads_enabled=true;
            String nativeLines = prefManager.getString("ADMIN_NATIVE_LINES");
            if (nativeLines == null || nativeLines.isEmpty()) {
                lines_beetween_ads = 6; // default value
            } else {
                try {
                    lines_beetween_ads = Integer.parseInt(nativeLines);
                } catch (NumberFormatException e) {
                    lines_beetween_ads = 6; // default value
                }
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }
        this.swipe_refresh_layout_home_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        this.linear_layout_load_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_home_fragment);
        this.linear_layout_page_error_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_home_fragment);
        this.recycler_view_home_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_home_fragment);
        this.relative_layout_load_more_home_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_home_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);

        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,RecyclerView.VERTICAL,false);


        this.homeAdapter =new HomeAdapter(dataList,getActivity());
        recycler_view_home_fragment.setHasFixedSize(true);
        recycler_view_home_fragment.setAdapter(homeAdapter);
        recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
    }

}
