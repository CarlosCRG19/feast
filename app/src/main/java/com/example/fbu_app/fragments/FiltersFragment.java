package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.models.VisitViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FiltersFragment extends Fragment {

    VisitViewModel visitViewModel; // object to communicate data between fragments

    // VIEWS
    Button btnFilter; // Apply filters button

    // Test views
    EditText etPrice, etDistance;

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
    String price, distance, categories;

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

        // Apply button setup
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add filters to ViewModel
                visitViewModel.addFilter("price", String.valueOf(getSelectedPrice()));
                visitViewModel.addFilter("distance", etDistance.getText().toString());
                // Get categories from checkboxes
                visitViewModel.addFilter("categories", getCategories());
                // Return to ExploreFragment
                getActivity().onBackPressed(); // since this transaction was made with addToBackStack, when backpressed, the user returns to previous fragment
            }
        });

    }

    private void getFiltersValues() {
        price = visitViewModel.getFilterValue("price");
        distance = visitViewModel.getFilterValue("distance");
        categories = visitViewModel.getFilterValue("categories");
    }

    private void setFilterViewsState() {
        // PRICE SETTINGS
        if (price == null) rbPAll.setChecked(true);
        else {
            // Get position of selected price
            int selectedPricePosition = Integer.parseInt(price) - 1; // price goes from 1 to 5
            // Set radio button at that position as checked
            ((RadioButton) rgPrice.getChildAt(selectedPricePosition)).setChecked(true);
        }

    }

    // Returns the price selected with the radiogroup
    private String getSelectedPrice() {
        // Create new price variable
        String price = "";
        // Check each RadioButton and verify if it has been checked
        for (int i = 0; i < rgPrice.getChildCount(); i++) {
            // Verify is radio group child at that position is a radio button
            if (rgPrice.getChildAt(i) instanceof RadioButton) {
                // Get current rb
                RadioButton radioButton = (RadioButton) rgPrice.getChildAt(i);
                // Check if it has been selected
                if (radioButton.isChecked()) {
                    // Create new integer with the price value
                    int priceInt = radioButton.getText().length(); // $ = 1, $$ = 2, $$$ = 3
                    // Set price value with string value of price Int
                    price = String.valueOf(priceInt);
                    // Stop loop
                    break;
                }
            }
        }
        return price;
    }

    // Returns the price selected with the radiogroup
    private String getSelectedDistance() {
        for (int i = 0; i < rgDistance.getChildCount(); i++) {
            if (rgDistance.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) rgDistance.getChildAt(i);
                if (radioButton.isChecked()) {
                    switch (radioButton.getId()) {
                        case R.id.rbD5:
                            return "5000";
                        case R.id.rbD10:
                            return "10000";
                        case R.id.rbD15:
                            return "15000";
                        case R.id.rbD20:
                            return "20000";
                    }
                    break;
                }
            }
        }
        return "";
    }

    // Returns the string of categories to search
    private String getCategories() {
        // Check ig SELECT ALL option is selected
        if(cbAll.isChecked()) return "All";
        else {
            // Create empty string for categories
            String categories = "";
            // Check each checkboxes with a for loop
            for(int i=0 ; i < llCheckboxes.getChildCount(); i++) {
                // Verify that view is an instance of checkbox
                if(llCheckboxes.getChildAt(i) instanceof CheckBox) {
                    // Get checkbox at postion
                    CheckBox checkBox = (CheckBox) llCheckboxes.getChildAt(i);
                    // Check if current checkbox if selected
                    if (checkBox.isChecked()) {
                        // Handle selected checkboxes with switch
                        switch (checkBox.getId()) {
                            case R.id.cbPizza:
                                categories += "pizza,";
                                break;
                            case R.id.cbPasta:
                                categories += "italian,";
                                break;
                            case R.id.cbBurger:
                                categories += "burgers,";
                                break;
                            case R.id.cbSushi:
                                categories += "sushi,";
                                break;
                            case R.id.cbMexican:
                                categories += "mexican,";
                                break;
                        }
                    }
                }
            }
            // If none of the checkboxes are selected, return All categories
            if (categories.equals("")) return "All";
            // Return string without the last element, which would be a comma
            else return categories.substring(0, categories.length() - 1);

        }
    }

    // UTILITY METHODS TO FILL HASHMAPS
    private void initializeDistanceMap() {
        distanceMap.put(rbD5, "5000");
        distanceMap.put(rbD10, "10000");
        distanceMap.put(rbD15, "15000");
        distanceMap.put(rbD20, "20000");
    }

    private void initializePriceMap() {
        priceMap.put(rbP1, "1");
        priceMap.put(rbP2, "2");
        priceMap.put(rbP3, "3");
        priceMap.put(rbP4, "4");
        priceMap.put(rbP5, "5");
        priceMap.put(rbPAll, "All");

    }

    private void initializeCategoriesMap() {
        categoriesMap.put(cbPizza, "pizza");
        categoriesMap.put(cbPasta, "pasta");
        categoriesMap.put(cbBurger, "burgers");
        categoriesMap.put(cbSushi, "sushi");
        categoriesMap.put(cbMexican, "mexican");
    }
}
