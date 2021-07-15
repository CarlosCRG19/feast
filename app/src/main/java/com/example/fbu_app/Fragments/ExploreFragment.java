package com.example.fbu_app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.FiltersAdapter;
import com.example.fbu_app.R;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.FiltersViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

// Main Fragment where the user will see the restaurants matching the criteria
public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment"; // tag for log messages

    // Filters data
    private FiltersViewModel filtersViewModel; // communication object between fragments
    List<Pair<String, String>> filters;

    // Test views
    RecyclerView rvFilters; // temporary Recycler view to display the applied filters
    TextView tvFoundBusinesses;
    FiltersAdapter adapter;

    // Navigation views
    Button btnFilters, btnCompare;

    // API client to handle requests
    YelpClient yelpClient;

    // Model to store received businesses
    List<Business> businesses;

    // Required empty constructor
    public ExploreFragment(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get ViewModel, which stores the filters applied atm
        filtersViewModel = ViewModelProviders.of(getActivity()).get(FiltersViewModel.class);

        // Create instance of Client
        yelpClient = new YelpClient();

        // initialize test data
        filters = new ArrayList<>();
        adapter = new FiltersAdapter(getContext(), filters);
        rvFilters = view.findViewById(R.id.rvFilters); // rv for testing
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFilters.setAdapter(adapter);

        tvFoundBusinesses = view.findViewById(R.id.tvFoundBusinesses); // tv to display number of found businesses
        // Populate filters with info from ViewModel
        filtersViewModel.getFilters().getValue().forEach((key, value) -> filters.add(new Pair<String,String>(key, value)));
        adapter.notifyDataSetChanged();

        // Request to get businesses that match the current filters
        yelpClient.getMatchingBusinesses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Receive response
                JSONObject response = json.jsonObject;
                try {
                    // Get list of businesses
                    JSONArray jsonArray = response.getJSONArray("businesses");
                    Log.i(TAG, jsonArray.toString());
                    // Use static methods to create array of businesses (these businesses are only stored locally, not in Parse)
                    businesses = Business.fromJsonArray(jsonArray);
                    // Set test TV text
                    tvFoundBusinesses.setText("Businesses found: " + String.valueOf(businesses.size()));
                    Log.i(TAG, String.valueOf(businesses.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                // Display log message
                Log.i(TAG, "Failure doing request: " + response, throwable);
            }
        }, filtersViewModel.getFilters().getValue());

        // Navigation button to launch FiltersFragment
        btnFilters = view.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltersFragment filtersFragment = new FiltersFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, filtersFragment)
                        .addToBackStack(null) // replace transaction is saved to the back stack so user can return to this fragment
                        .commit();
            }
        });

        // Navigation button to launch CompareFragment
        btnCompare = view.findViewById(R.id.btnCompare);
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompareFragment compareFragment = new CompareFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, compareFragment)
                        .commit();
            }
        });
    }
}
