package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.adapters.VisitsAdapter;
import com.example.fbu_app.models.Visit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PastVisitsFragment extends Fragment {

    // VIEWS
    private RecyclerView rvVisits; // RV to display visits
    private Button btnNextVisits; // Button to go to next visits

    // HELPERS
    private SwipeRefreshLayout swipeContainer; // handles refresh action

    // Model to store visits data
    private List<Visit> visits;
    // Adapter for RecyclerView
    private VisitsAdapter adapter;

    public PastVisitsFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_past_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Make sure that bottom nav bar is being displayed
        ((MainActivity) getActivity()).showBottomNavBar();

        // Init visits list and adapter
        visits = new ArrayList<>();
        adapter = new VisitsAdapter(getContext(), visits);

        // Setup RecyclerView
        rvVisits = view.findViewById(R.id.rvPastVisits);
        rvVisits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVisits.setAdapter(adapter);

        // Set up listener to return to next visits
        btnNextVisits = view.findViewById(R.id.btnNextVisits);
        btnNextVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, nextVisitsFragment)
                        .commit();
            }
        });

        // Make query for NextVisits
        queryPastVisits();

        // Enable refresh feature
        setRefreshFeature(view);

    }

    // Lets the user refresh the main feed swiping down on the RV
    protected void setRefreshFeature(View view) {
        // Get view from layout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make query to get next visits and invitations
                queryPastVisits();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Makes a query to parse DataBase and gets visits whose date is older than today's
    private void queryPastVisits() {
        // Specify which class we want to query
        ParseQuery<Visit> query = ParseQuery.getQuery(Visit.class);
        // Include business object in query
        query.include("business");
        // Include visit creator
        query.include("user");
        // Include attendees list
        query.include("attendees");
        // Set max date as today
        query.whereLessThan("date", Date.valueOf(LocalDate.now().toString()));
        // order posts by date
        query.addDescendingOrder("date");
        // Set user to current user
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // Make query using background thread
        query.findInBackground(new FindCallback<Visit>() {
            @Override
            public void done(List<Visit> visitsList, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e("NextVisitsFragment", "Issue getting visits", e);
                    return;
                }
                // Clear list
                visits.clear();
                // Add values to visits
                visits.addAll(visitsList);
                // Notify adapter
                adapter.notifyDataSetChanged();
                // Hide refreshing icon
                swipeContainer.setRefreshing(false);
                return;
            }
        });
    }
}

