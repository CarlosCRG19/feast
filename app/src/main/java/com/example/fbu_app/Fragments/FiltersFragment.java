package com.example.fbu_app.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.models.FiltersViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FiltersFragment extends Fragment {

    FiltersViewModel filtersViewModel; // object to communicate data between fragments

    // VIEWS
    Button btnFilter; // Apply filters button

    // Test views
    EditText etRating, etPrice, etDistance, etCategories; //

    public FiltersFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign value for ViewModel
        filtersViewModel = ViewModelProviders.of(getActivity()).get(FiltersViewModel.class);

        // Get TEST views
        etRating = view.findViewById(R.id.etRating);
        etPrice = view.findViewById(R.id.etPrice);
        etDistance = view.findViewById(R.id.etDistance);
        etCategories = view.findViewById(R.id.etCategories);

        // Apply button setup
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add filters to ViewModel
                filtersViewModel.addFilter(new Pair<String, String>("rating", etRating.getText().toString()));
                filtersViewModel.addFilter(new Pair<String, String>("price", etPrice.getText().toString()));
                filtersViewModel.addFilter(new Pair<String, String>("distance", etDistance.getText().toString()));
                filtersViewModel.addFilter(new Pair<String, String>("categories", etCategories.getText().toString()));
                // Return to ExploreFragment
                getActivity().onBackPressed(); // since this transaction was made with addToBackStack, when backpressed, the user returns to previous fragment
            }
        });

    }



}
