package com.example.fbu_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.models.VisitViewModel;

import org.jetbrains.annotations.NotNull;

public class FiltersFragment extends Fragment {

    VisitViewModel visitViewModel; // object to communicate data between fragments

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
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);

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
                visitViewModel.addFilter("rating", etRating.getText().toString());
                visitViewModel.addFilter("price", etPrice.getText().toString());
                visitViewModel.addFilter("distance", etDistance.getText().toString());
                visitViewModel.addFilter("categories", etCategories.getText().toString());
                // Return to ExploreFragment
                getActivity().onBackPressed(); // since this transaction was made with addToBackStack, when backpressed, the user returns to previous fragment
            }
        });

    }



}
