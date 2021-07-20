package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.models.VisitViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FiltersFragment extends Fragment {

    VisitViewModel visitViewModel; // object to communicate data between fragments

    // VIEWS
    Button btnFilter; // Apply filters button

    // Distance selection views
    RadioGroup rgDistance;
    RadioButton rbD5, rbD10, rbD15, rbD20;

    // Price selection views
    RadioGroup rgPrice;
    RadioButton rbP1, rbP2, rbP3, rbP4, rbP5, rbPAll;

    // Checkboxes
    LinearLayout llCheckboxes;
    CheckBox cbAll, cbPizza, cbPasta, cbBurger, cbSushi, cbMexican;

    // Filters values
    String price, radius, categories;

    // Views dictionary
    HashMap<RadioButton, String> distanceMap;
    HashMap<RadioButton, String> priceMap;
    HashMap<CheckBox, String> categoriesMap;


    public FiltersFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign value for ViewModel
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);
        // Get member vars values from viewmodel
        getFiltersValues();

        // Distance selection setup

        // Get radio group
        rgDistance = view.findViewById(R.id.rgDistance);
        // Get each radio button
        rbD5 = view.findViewById(R.id.rbD5);
        rbD10 = view.findViewById(R.id.rbD10);
        rbD15 = view.findViewById(R.id.rbD15);
        rbD20 = view.findViewById(R.id.rbD20);

        // Price selection setup

        // Get radio group
        rgPrice = view.findViewById(R.id.rgPrice);
        // Get each radio button
        rbP1 = view.findViewById(R.id.rbP1);
        rbP2 = view.findViewById(R.id.rbP2);
        rbP3 = view.findViewById(R.id.rbP3);
        rbP4 = view.findViewById(R.id.rbP4);
        rbP5 = view.findViewById(R.id.rbP5);
        rbPAll = view.findViewById(R.id.rbPAll); // all prices


        // Checkboxes setup

        // Get checkboxes layout
        llCheckboxes = view.findViewById(R.id.llCheckboxes);
        // Get individual checkboxes
        cbPizza = view.findViewById(R.id.cbPizza);
        cbPasta = view.findViewById(R.id.cbPasta);
        cbBurger = view.findViewById(R.id.cbBurger);
        cbSushi = view.findViewById(R.id.cbSushi);
        cbMexican = view.findViewById(R.id.cbMexican);
        // Get SELECT ALL checkbox
        cbAll = view.findViewById(R.id.cbAll);
        // Set listener for SELECT ALL
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If this checkbox is selected, select all checkboxes inside llCheckboxes
                for(int i=0 ; i < llCheckboxes.getChildCount(); i++) {
                    // Check if child is an instance of checkbox
                    if(llCheckboxes.getChildAt(i) instanceof CheckBox) {
                        // Change check state
                        ((CheckBox) llCheckboxes.getChildAt(i)).setChecked(isChecked);
                    }
                }
            }
        });

        // Initialize hashmaps for values
        initializePriceMap();
        initializeDistanceMap();
        initializeCategoriesMap();

        // Change state of views and check if they have been selected
        setFilterViewsState();

        // Apply button setup
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set values for member variables
                // This values are obtained from the views
                setSelectedPrice();
                setSelectedDistance();
                setCategories();
                // Add filters to ViewModel

                // Check if price is All (to search all prices we need to remove the price attribute from the call)
                if (price == "All")
                    visitViewModel.removeFilter("price");
                else
                    visitViewModel.addFilter("price", price);
                // Add rest of filters
                visitViewModel.addFilter("radius", radius);
                visitViewModel.addFilter("categories", categories);
                // Return to ExploreFragment
                getActivity().onBackPressed(); // since this transaction was made with addToBackStack, when backpressed, the user returns to previous fragment
            }
        });

    }

    private void getFiltersValues() {
        price = visitViewModel.getFilterValue("price");
        radius = visitViewModel.getFilterValue("radius");
        categories = visitViewModel.getFilterValue("categories");
    }

    private void setFilterViewsState() {
        // PRICE SETTINGS
        if (price == null)
            rbPAll.setChecked(true);
        else
            getRbFromMap(priceMap, price).setChecked(true);
        // DISTANCE SETTINGS
        getRbFromMap(distanceMap, radius).setChecked(true);
        // CATEGORIES SETUP
        if (categories == null || categories == "All")
            cbAll.setChecked(true);
        else {
            String[] categoriesList = categories.split(",");
            for(String category : categoriesList) {
                getCbFromMap(categoriesMap, category).setChecked(true);
            }

        }
    }

    // Returns the price selected with the radiogroup
    private void setSelectedPrice() {
        // Check each RadioButton and verify if it has been checked
        for (int i = 0; i < rgPrice.getChildCount(); i++) {
            // Verify is radio group child at that position is a radio button
            if (rgPrice.getChildAt(i) instanceof RadioButton) {
                // Get current rb
                RadioButton radioButton = (RadioButton) rgPrice.getChildAt(i);
                // Check if it has been selected
                if (radioButton.isChecked()) {
                    // Get price from hashmap
                    price = priceMap.get(radioButton);
                    // Stop loop
                    break;
                }
            }
        }
    }

    // Returns the price selected with the radiogroup
    private void setSelectedDistance() {
        for (int i = 0; i < rgDistance.getChildCount(); i++) {
            if (rgDistance.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) rgDistance.getChildAt(i);
                if (radioButton.isChecked()) {
                    // Get distance from hashmap
                    radius = distanceMap.get(radioButton);
                    // Break loop
                    break;
                }
            }
        }
    }


    // Returns the string of categories to search
    private void setCategories() {
        // Check ig SELECT ALL option is selected
        if(cbAll.isChecked()) categories = "All";
        else {
            // Create empty string for categories
            categories = "";
            // Check each checkboxes with a for loop
            for(int i=0 ; i < llCheckboxes.getChildCount(); i++) {
                // Verify that view is an instance of checkbox
                if(llCheckboxes.getChildAt(i) instanceof CheckBox) {
                    // Get checkbox at postion
                    CheckBox checkBox = (CheckBox) llCheckboxes.getChildAt(i);
                    // Check if current checkbox if selected
                    if (checkBox.isChecked()) {
                        // Get category from hashmap
                        String newCategory = categoriesMap.get(checkBox);
                        // add new category
                        categories += newCategory + ",";
                    }
                }
            }
            // If none of the checkboxes are selected, return All categories
            if (categories.equals("")) categories = "All";
            // Delete last element, which would be a comma
            categories = categories.substring(0, categories.length() - 1);
        }
    }

    // UTILITY METHODS TO FILL HASHMAPS
    private void initializeDistanceMap() {
        distanceMap = new HashMap<>();
        distanceMap.put(rbD5, "5000");
        distanceMap.put(rbD10, "10000");
        distanceMap.put(rbD15, "15000");
        distanceMap.put(rbD20, "20000");
    }

    private void initializePriceMap() {
        priceMap = new HashMap<>();
        priceMap.put(rbP1, "1");
        priceMap.put(rbP2, "2");
        priceMap.put(rbP3, "3");
        priceMap.put(rbP4, "4");
        priceMap.put(rbP5, "5");
        priceMap.put(rbPAll, "All");

    }

    private void initializeCategoriesMap() {
        categoriesMap = new HashMap<>();
        categoriesMap.put(cbPizza, "pizza");
        categoriesMap.put(cbPasta, "italian");
        categoriesMap.put(cbBurger, "burgers");
        categoriesMap.put(cbSushi, "sushi");
        categoriesMap.put(cbMexican, "mexican");
    }

    private RadioButton getRbFromMap(HashMap<RadioButton, String> map, String searchValue) {
        for(Map.Entry<RadioButton,String> entry : map.entrySet()) {
            if(entry.getValue().equals(searchValue))
                return entry.getKey();
        }
        return null;
    }

    private CheckBox getCbFromMap(HashMap<CheckBox, String> map, String searchValue) {
        for(Map.Entry<CheckBox,String> entry : map.entrySet()) {
            if(entry.getValue().equals(searchValue))
                return entry.getKey();
        }
        return null;
    }

}
