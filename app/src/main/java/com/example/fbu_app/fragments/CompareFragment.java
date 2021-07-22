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
import android.widget.Toast;

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
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitViewModel;
import com.example.fbu_app.models.BusinessesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CompareFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // MEMBER VARIABLES

    public static final String VISIT_TAG = "visit";

    // ViewModels for fragment communication
    BusinessesViewModel businessesViewModel;
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
    Button btnExplore, btnRandomPick;

    Spinner attributeSpinner;

    // Business object to store a random business in case random is picked
    Business randomBusiness;

    ParseUser tryUser;

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
        businessesViewModel = ViewModelProviders.of(getActivity()).get(BusinessesViewModel.class);
        selectedBusinesses = businessesViewModel.getSelectedBusinesses().getValue();

        // Setup adapter with businesses and visitDates
        adapter = new BusinessAdapterGo(getContext(), selectedBusinesses, visitDateStr, visitDate);
        // Setup RV
        rvBusinesses = view.findViewById(R.id.rvBusinesses);
        rvBusinesses.setAdapter(adapter);
        rvBusinesses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup random pick button
        btnRandomPick = view.findViewById(R.id.btnRandomPick);
        // Listener to pick a random business from the list and create a visit
        btnRandomPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that there are selected businesses
                if (selectedBusinesses.size() > 0) {
                    // Create new visit
                    Visit newVisit = new Visit();
                    // Get random number between the selectedBusiness size range
                    Random rand = new Random();
                    int randomNum = rand.nextInt(selectedBusinesses.size());
                    // Get business at specific position
                    randomBusiness = selectedBusinesses.get(randomNum);
                    // Verify if this business already exists
                    verifyBusinessExists();
                    // Set business to new visit
                    newVisit.setBusiness(randomBusiness);
                    // Add current user to attendees list
                    newVisit.addAttendee(ParseUser.getCurrentUser());
                    // Add fields
                    newVisit.setUser(ParseUser.getCurrentUser());
                    newVisit.setDate(visitDate);
                    newVisit.setDateStr(visitDateStr);
                    // Save visit using background thread
                    newVisit.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.i("ParseSave", "Failed to save visit", e);
                                return;
                            }
                            // Display success message
                            Toast.makeText(getContext(), "Created random visit to " + randomBusiness.getName() + "!", Toast.LENGTH_SHORT).show();
                            // Create bundle to pass busines as argument
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(VISIT_TAG, newVisit);
                            // Transaction to new fragment
                            ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                            confirmationFragment.setArguments(bundle);
                            // Make fragment transaction
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.flContainer, confirmationFragment)
                                    .commit();
                        }
                    });
                }
            }
        });

        // Setup return to ExploreFragment
        btnExplore = view.findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // backpressed to return to explore fragment without starting it again
            }
        });

        // Find spinner in layout
        attributeSpinner = view.findViewById(R.id.spinner);
        // Create adapter using the attributes array on resources
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.attributes, android.R.layout.simple_spinner_item);
        // Set dropdown style
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Assign adapter to spinner
        attributeSpinner.setAdapter(spinnerAdapter);
        // Assign item listener
        attributeSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get name of attribute from spinner
        String attribute = parent.getItemAtPosition(position).toString();
        // Create switch to handle each option
        switch (attribute) {
            case "Highest Rating":
                // Call static method QuickSort with the selected attribute
                BusinessQuickSort.quickSort(selectedBusinesses, "rating", 0, selectedBusinesses.size() - 1);
                // For rating, the user would want to first see the businesses with highest ratings, so we reverse the list
                Collections.reverse(selectedBusinesses);
                // Notify adapter
                adapter.notifyDataSetChanged();
                break;
            case "Nearest":
                // Call static method QuickSort using distance as comparison attribute
                BusinessQuickSort.quickSort(selectedBusinesses, "distance", 0, selectedBusinesses.size() - 1);
                // Notify adapter of changes
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

    private void verifyBusinessExists() {
        // Specify type of query
        ParseQuery<Business> query = ParseQuery.getQuery(Business.class);
        // Search for business in database based on yelpId
        query.whereEqualTo("yelpId", randomBusiness.getYelpId());
        // Use getFirstInBackground to finish the search if it has found one matching business
        query.getFirstInBackground(new GetCallback<Business>() {
            @Override
            public void done(Business object, ParseException e) {
                if (e != null) {
                    return;
                }
                // if the business exists, change value of member variable
                if (object != null) {
                    randomBusiness = object;
                }
            }
        });
    }
}
