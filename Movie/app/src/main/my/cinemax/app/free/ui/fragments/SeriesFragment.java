package my.cinemax.app.free.ui.fragments;


import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.JsonDataService;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesFragment extends Fragment {

    private View view;
    private RelativeLayout relative_layout_series_fragement_filtres_button;
    private CardView card_view_series_fragement_filtres_layout;
    private ImageView image_view_series_fragement_close_filtres;
    private AppCompatSpinner spinner_fragement_series_genres_list;
    private AppCompatSpinner spinner_fragement_series_orders_list;
    private SwipeRefreshLayout swipe_refresh_layout_series_fragment;
    private LinearLayout linear_layout_load_series_fragment;
    private LinearLayout linear_layout_page_error_series_fragment;
    private RecyclerView recycler_view_series_fragment;
    private RelativeLayout relative_layout_load_more_series_fragment;
    private Button button_try_again;
    private ImageView image_view_empty_list;

    private List<Poster> seriesList = new ArrayList<>();
    private List<Poster> allSeriesList = new ArrayList<>();
    private List<Genre> genreList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private Integer genreSelected = 0;
    private String orderSelected = "year";
    private Integer page = 0;
    private boolean loading = true;
    private boolean mLocked = false;

    private Integer lines_beetween_ads = 2;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0;

    public SeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_series, container, false);
        prefManager = new PrefManager(getApplicationContext());

        initView();
        getGenreList();
        initActon();

        return view;
    }

    private void getGenreList() {
        // Use JsonDataService to get genres from GitHub JSON data
        JsonDataService jsonDataService = new JsonDataService(getContext());
        jsonDataService.loadHomeData(new JsonDataService.DataCallback() {
            @Override
            public void onSuccess(Data data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data.getGenres() != null && data.getGenres().size() > 0) {
                            final String[] countryCodes = new String[data.getGenres().size() + 1];
                            countryCodes[0] = "All genres";
                            genreList.add(new Genre());

                            for (int i = 0; i < data.getGenres().size(); i++) {
                                countryCodes[i + 1] = data.getGenres().get(i).getTitle();
                                genreList.add(data.getGenres().get(i));
                            }
                            ArrayAdapter<String> filtresAdapter = new ArrayAdapter<String>(getActivity(),
                                    R.layout.spinner_layout, R.id.textView, countryCodes);
                            filtresAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            spinner_fragement_series_genres_list.setAdapter(filtresAdapter);
                        }
                        
                        // Store all series for filtering
                        if (data.getPosters() != null) {
                            allSeriesList.clear();
                            for (Poster poster : data.getPosters()) {
                                if ("serie".equals(poster.getType())) {
                                    allSeriesList.add(poster);
                                }
                            }
                            loadSeries();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("SeriesFragment", "Failed to load genres: " + error);
                        // Show error or use default empty list
                        loadSeries();
                    }
                });
            }
        });
    }

    private void initActon() {
        relative_layout_series_fragement_filtres_button.setOnClickListener(v -> {
            card_view_series_fragement_filtres_layout.setVisibility(View.VISIBLE);
            relative_layout_series_fragement_filtres_button.setVisibility(View.INVISIBLE);
        });
        image_view_series_fragement_close_filtres.setOnClickListener(v -> {
            card_view_series_fragement_filtres_layout.setVisibility(View.INVISIBLE);
            relative_layout_series_fragement_filtres_button.setVisibility(View.VISIBLE);
        });
        
        spinner_fragement_series_genres_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mLocked) {
                    if (id == 0) {
                        genreSelected = 0;
                    } else {
                        genreSelected = (int) id;
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    seriesList.clear();
                    loadSeries();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        spinner_fragement_series_orders_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mLocked) {
                    switch ((int) id) {
                        case 0:
                            orderSelected = "year";
                            break;
                        case 1:
                            orderSelected = "title";
                            break;
                        case 2:
                            orderSelected = "rating";
                            break;
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    seriesList.clear();
                    loadSeries();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        swipe_refresh_layout_series_fragment.setOnRefreshListener(() -> {
            item = 0;
            page = 0;
            loading = true;
            seriesList.clear();
            loadSeries();
        });
        
        button_try_again.setOnClickListener(v -> {
            item = 0;
            page = 0;
            loading = true;
            seriesList.clear();
            loadSeries();
        });
    }

    private void initView() {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")) {
            native_ads_enabled = true;
            if (tabletSize) {
                lines_beetween_ads = Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            } else {
                lines_beetween_ads = Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled = false;
        }

        this.relative_layout_series_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_series_fragement_filtres_button);
        this.card_view_series_fragement_filtres_layout = (CardView) view.findViewById(R.id.card_view_series_fragement_filtres_layout);
        this.image_view_series_fragement_close_filtres = (ImageView) view.findViewById(R.id.image_view_series_fragement_close_filtres);
        this.spinner_fragement_series_genres_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_series_genre_list);
        this.spinner_fragement_series_orders_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_series_orders_list);
        this.swipe_refresh_layout_series_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_series_fragment);
        this.linear_layout_load_series_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_series_fragment);
        this.linear_layout_page_error_series_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_series_fragment);
        this.recycler_view_series_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_series_fragment);
        this.relative_layout_load_more_series_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_series_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);

        adapter = new PosterAdapter(seriesList, getActivity());

        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 4, RecyclerView.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? 4 : 1;
                }
            });
        } else {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3, RecyclerView.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? 3 : 1;
                }
            });
        }

        recycler_view_series_fragment.setHasFixedSize(true);
        recycler_view_series_fragment.setAdapter(adapter);
        recycler_view_series_fragment.setLayoutManager(gridLayoutManager);

        final String[] countryCodes = getResources().getStringArray(R.array.orders_list);
        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_layout, R.id.textView, countryCodes);
        ordersAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_fragement_series_orders_list.setAdapter(ordersAdapter);
    }

    public boolean checkSUBSCRIBED() {
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    private void loadSeries() {
        if (page == 0) {
            linear_layout_load_series_fragment.setVisibility(View.VISIBLE);
            seriesList.clear();
        } else {
            relative_layout_load_more_series_fragment.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_series_fragment.setRefreshing(false);
        
        // Filter series based on selected genre and order
        List<Poster> filteredSeries = new ArrayList<>();
        
        for (Poster series : allSeriesList) {
            // Filter by genre if a specific genre is selected
            if (genreSelected == 0) {
                // Show all series
                filteredSeries.add(series);
            } else if (genreSelected <= genreList.size()) {
                // Check if series belongs to selected genre
                Genre selectedGenre = genreList.get(genreSelected);
                if (series.getClassification() != null && 
                    series.getClassification().equals(selectedGenre.getTitle())) {
                    filteredSeries.add(series);
                }
            }
        }
        
        // Sort series based on selected order
        if ("year".equals(orderSelected)) {
            filteredSeries.sort((a, b) -> {
                String yearA = a.getYear() != null ? a.getYear() : "0";
                String yearB = b.getYear() != null ? b.getYear() : "0";
                return yearB.compareTo(yearA); // Descending order (newest first)
            });
        } else if ("title".equals(orderSelected)) {
            filteredSeries.sort((a, b) -> {
                String titleA = a.getTitle() != null ? a.getTitle() : "";
                String titleB = b.getTitle() != null ? b.getTitle() : "";
                return titleA.compareToIgnoreCase(titleB);
            });
        } else if ("rating".equals(orderSelected)) {
            filteredSeries.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
        }
        
        // Implement pagination
        int itemsPerPage = 20;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredSeries.size());
        
        if (startIndex < filteredSeries.size()) {
            List<Poster> pageSeries = filteredSeries.subList(startIndex, endIndex);
            
            for (int i = 0; i < pageSeries.size(); i++) {
                seriesList.add(pageSeries.get(i));

                if (native_ads_enabled) {
                    item++;
                    if (item == lines_beetween_ads) {
                        item = 0;
                        if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                            seriesList.add(new Poster().setTypeView(4));
                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                            seriesList.add(new Poster().setTypeView(5));
                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                            if (type_ads == 0) {
                                seriesList.add(new Poster().setTypeView(4));
                                type_ads = 1;
                            } else if (type_ads == 1) {
                                seriesList.add(new Poster().setTypeView(5));
                                type_ads = 0;
                            }
                        }
                    }
                }
            }
            
            linear_layout_page_error_series_fragment.setVisibility(View.GONE);
            recycler_view_series_fragment.setVisibility(View.VISIBLE);
            image_view_empty_list.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
            page++;
            loading = true;
        } else {
            if (page == 0) {
                linear_layout_page_error_series_fragment.setVisibility(View.GONE);
                recycler_view_series_fragment.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.VISIBLE);
            }
        }
        
        relative_layout_load_more_series_fragment.setVisibility(View.GONE);
        swipe_refresh_layout_series_fragment.setRefreshing(false);
        linear_layout_load_series_fragment.setVisibility(View.GONE);
    }
}
