package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.BusinessAdapterGo;
import com.example.fbu_app.helpers.BusinessQuickSort;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.VisitViewModel;
import com.example.fbu_app.models.SelectedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CompareFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // MEMBER VARIABLES

    // ViewModels for fragment communication
    SelectedViewModel selectedViewModel;
    VisitViewModel visitViewModel;

    // Model to store businesses
    List<Business> selectedBusinesses;

    // Date variables for visit creation
    Date visitDate;
    String visitDateStr;


    // RV variables
    RecyclerView rvBusinesses;
    BusinessAdapterGo adapter;

    // Button for Explore Screen
    Button btnExplore;

    Spinner attributeSpinner;

    public CompareFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_compare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // From visit view model, get the visitDate and visitDateStr
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);
        visitDate = visitViewModel.getVisitDate().getValue();
        visitDateStr = visitViewModel.getVisitDateStr().getValue();

        // Get selected businesses from ViewModel
        selectedViewModel = ViewModelProviders.of(getActivity()).get(SelectedViewModel.class);
        selectedBusinesses = selectedViewModel.getSelectedBusinesses().getValue();

        // Setup adapter with businesses and visitDates
        adapter = new BusinessAdapterGo(getContext(), selectedBusinesses, visitDateStr, visitDate);
        // Setup RV
        rvBusinesses = view.findViewById(R.id.rvBusinesses);
        rvBusinesses.setAdapter(adapter);
        rvBusinesses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup return to ExploreFragment
        btnExplore = view.findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // backpressed to return to explore fragment without starting it again
            }
        });

        // Set up spinner
        attributeSpinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.attributes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attributeSpinner.setAdapter(spinnerAdapter);
        attributeSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String attribute = parent.getItemAtPosition(position).toString();
        switch (attribute) {
            case "Highest Rating":
                BusinessQuickSort.quickSort(selectedBusinesses, "rating", 0, selectedBusinesses.size() - 1);
                Collections.reverse(selectedBusinesses);
                adapter.notifyDataSetChanged();
                break;
            case "Nearest":
                BusinessQuickSort.quickSort(selectedBusinesses, "distance", 0, selectedBusinesses.size() - 1);
                adapter.notifyDataSetChanged();
                break;
            case "Lowest Prices":
                BusinessQuickSort.quickSort(selectedBusinesses, "price", 0, selectedBusinesses.size() - 1);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
