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

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.VisitsAdapter;
import com.example.fbu_app.models.Visit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NextVisitsFragment extends Fragment {

    // VIEWS
    RecyclerView rvVisits; // RV to display visits
    Button btnPastVisits; // Button to go to past visits

    // Model to store visits data
    List<Visit> visits;
    // Adapter for RecyclerView
    VisitsAdapter adapter;

    // Required empty constructor
    public NextVisitsFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init visits list and adapter
        visits = new ArrayList<>();
        adapter = new VisitsAdapter(getContext(), visits, 0);

        // Setup RecyclerView
        rvVisits = view.findViewById(R.id.rvNextVisits);
        rvVisits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVisits.setAdapter(adapter);

        // Button to pass to next visits
        btnPastVisits = view.findViewById(R.id.btnPastVisits);
        btnPastVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PastVisitsFragment pastVisitsFragment = new PastVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, pastVisitsFragment)
                        .commit();
            }
        });

        // Make query for NextVisits
        queryNextVisits();

    }

    // Makes a query to parse DataBase and gets visits whos date is greater or equal than todays
    private void queryNextVisits() {
        // Specify which class we want to query
        ParseQuery<Visit> query = ParseQuery.getQuery(Visit.class);
        // Include business object in query
        query.include("business");
        // Set max date as today
        query.whereGreaterThanOrEqualTo("date", Calendar.getInstance().getTime());
        // order posts by date
        query.addDescendingOrder("date");
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
                return;
            }
        });
    }
}

