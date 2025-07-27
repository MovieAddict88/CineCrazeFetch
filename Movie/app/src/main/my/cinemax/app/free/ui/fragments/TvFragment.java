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
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.ui.Adapters.ChannelAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvFragment extends Fragment {

    private View view;
    private RelativeLayout relative_layout_tv_fragement_filtres_button;
    private CardView card_view_tv_fragement_filtres_layout;
    private ImageView image_view_tv_fragement_close_filtres;
    private AppCompatSpinner spinner_fragement_tv_categories_list;
    private SwipeRefreshLayout swipe_refresh_layout_tv_fragment;
    private LinearLayout linear_layout_load_tv_fragment;
    private LinearLayout linear_layout_page_error_tv_fragment;
    private RecyclerView recycler_view_tv_fragment;
    private RelativeLayout relative_layout_load_more_tv_fragment;
    private Button button_try_again;
    private ImageView image_view_empty_list;

    private List<Channel> channelList = new ArrayList<>();
    private List<Channel> allChannelsList = new ArrayList<>();
    private List<String> categoriesList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private ChannelAdapter adapter;

    private Integer categorySelected = 0;
    private Integer page = 0;
    private boolean loading = true;
    private boolean mLocked = false;

    private Integer lines_beetween_ads = 2;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0;

    public TvFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tv, container, false);
        prefManager = new PrefManager(getApplicationContext());

        initView();
        getChannelData();
        initActon();

        return view;
    }

    private void getChannelData() {
        // Use JsonDataService to get channels from GitHub JSON data
        JsonDataService jsonDataService = new JsonDataService(getContext());
        jsonDataService.loadHomeData(new JsonDataService.DataCallback() {
            @Override
            public void onSuccess(Data data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Extract unique categories from channels
                        categoriesList.clear();
                        categoriesList.add("All categories");
                        
                        // Store all channels for filtering
                        if (data.getChannels() != null) {
                            allChannelsList.clear();
                            allChannelsList.addAll(data.getChannels());
                            
                            // Extract unique categories
                            List<String> uniqueCategories = new ArrayList<>();
                            for (Channel channel : data.getChannels()) {
                                if (channel.getClassification() != null && 
                                    !uniqueCategories.contains(channel.getClassification())) {
                                    uniqueCategories.add(channel.getClassification());
                                }
                            }
                            categoriesList.addAll(uniqueCategories);
                            
                            // Setup categories spinner
                            ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getActivity(),
                                    R.layout.spinner_layout, R.id.textView, 
                                    categoriesList.toArray(new String[0]));
                            categoriesAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            spinner_fragement_tv_categories_list.setAdapter(categoriesAdapter);
                            
                            loadChannels();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TvFragment", "Failed to load channel data: " + error);
                        // Show error or use default empty list
                        loadChannels();
                    }
                });
            }
        });
    }

    private void initActon() {
        relative_layout_tv_fragement_filtres_button.setOnClickListener(v -> {
            card_view_tv_fragement_filtres_layout.setVisibility(View.VISIBLE);
            relative_layout_tv_fragement_filtres_button.setVisibility(View.INVISIBLE);
        });
        image_view_tv_fragement_close_filtres.setOnClickListener(v -> {
            card_view_tv_fragement_filtres_layout.setVisibility(View.INVISIBLE);
            relative_layout_tv_fragement_filtres_button.setVisibility(View.VISIBLE);
        });
        
        spinner_fragement_tv_categories_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mLocked) {
                    categorySelected = (int) id;
                    item = 0;
                    page = 0;
                    loading = true;
                    channelList.clear();
                    loadChannels();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        swipe_refresh_layout_tv_fragment.setOnRefreshListener(() -> {
            item = 0;
            page = 0;
            loading = true;
            channelList.clear();
            loadChannels();
        });
        
        button_try_again.setOnClickListener(v -> {
            item = 0;
            page = 0;
            loading = true;
            channelList.clear();
            loadChannels();
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

        this.relative_layout_tv_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_tv_fragement_filtres_button);
        this.card_view_tv_fragement_filtres_layout = (CardView) view.findViewById(R.id.card_view_tv_fragement_filtres_layout);
        this.image_view_tv_fragement_close_filtres = (ImageView) view.findViewById(R.id.image_view_tv_fragement_close_filtres);
        this.spinner_fragement_tv_categories_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_tv_category_list);
        this.swipe_refresh_layout_tv_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_tv_fragment);
        this.linear_layout_load_tv_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_tv_fragment);
        this.linear_layout_page_error_tv_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_tv_fragment);
        this.recycler_view_tv_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_tv_fragment);
        this.relative_layout_load_more_tv_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_tv_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);

        adapter = new ChannelAdapter(channelList, getActivity());

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

        recycler_view_tv_fragment.setHasFixedSize(true);
        recycler_view_tv_fragment.setAdapter(adapter);
        recycler_view_tv_fragment.setLayoutManager(gridLayoutManager);
    }

    public boolean checkSUBSCRIBED() {
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    private void loadChannels() {
        if (page == 0) {
            linear_layout_load_tv_fragment.setVisibility(View.VISIBLE);
            channelList.clear();
        } else {
            relative_layout_load_more_tv_fragment.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_tv_fragment.setRefreshing(false);
        
        // Filter channels based on selected category
        List<Channel> filteredChannels = new ArrayList<>();
        
        for (Channel channel : allChannelsList) {
            // Filter by category if a specific category is selected
            if (categorySelected == 0) {
                // Show all channels
                filteredChannels.add(channel);
            } else if (categorySelected < categoriesList.size()) {
                // Check if channel belongs to selected category
                String selectedCategory = categoriesList.get(categorySelected);
                if (channel.getClassification() != null && 
                    channel.getClassification().equals(selectedCategory)) {
                    filteredChannels.add(channel);
                }
            }
        }
        
        // Sort channels alphabetically by title
        filteredChannels.sort((a, b) -> {
            String titleA = a.getTitle() != null ? a.getTitle() : "";
            String titleB = b.getTitle() != null ? b.getTitle() : "";
            return titleA.compareToIgnoreCase(titleB);
        });
        
        // Implement pagination
        int itemsPerPage = 20;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredChannels.size());
        
        if (startIndex < filteredChannels.size()) {
            List<Channel> pageChannels = filteredChannels.subList(startIndex, endIndex);
            
            for (int i = 0; i < pageChannels.size(); i++) {
                channelList.add(pageChannels.get(i));

                if (native_ads_enabled) {
                    item++;
                    if (item == lines_beetween_ads) {
                        item = 0;
                        if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                            channelList.add(new Channel().setTypeView(4));
                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                            channelList.add(new Channel().setTypeView(5));
                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                            if (type_ads == 0) {
                                channelList.add(new Channel().setTypeView(4));
                                type_ads = 1;
                            } else if (type_ads == 1) {
                                channelList.add(new Channel().setTypeView(5));
                                type_ads = 0;
                            }
                        }
                    }
                }
            }
            
            linear_layout_page_error_tv_fragment.setVisibility(View.GONE);
            recycler_view_tv_fragment.setVisibility(View.VISIBLE);
            image_view_empty_list.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
            page++;
            loading = true;
        } else {
            if (page == 0) {
                linear_layout_page_error_tv_fragment.setVisibility(View.GONE);
                recycler_view_tv_fragment.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.VISIBLE);
            }
        }
        
        relative_layout_load_more_tv_fragment.setVisibility(View.GONE);
        swipe_refresh_layout_tv_fragment.setRefreshing(false);
        linear_layout_load_tv_fragment.setVisibility(View.GONE);
    }
}
