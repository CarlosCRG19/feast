package com.example.fbu_app.Fragments;

import com.example.fbu_app.BuildConfig;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.gms.common.api.Status;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// User will be able to select a location and date for the visit. The first requests for Businesses will be made
// using the selected location
public class LocationFragment extends Fragment {

    public static final String TAG = "LocationFragment"; // tag for log messages
    public static final int AUTOCOMPLETE_REQUEST_CODE = 42;

    VisitViewModel visitViewModel; // Communication object between fragments

    // VIEWS
    DatePickerDialog datePickerDialog; // Date picking
    Button btnDate, btnSelectLocation;
    EditText etPlaces; // Location selection
    TextView tvLocation, tvLatLng;

    // Latitude and Longitude values for selected location
    double latitude, longitude;

    // Required empty constructor
    public LocationFragment() {};

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set value for ViewModel
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);
        // Initialize the filters map
        visitViewModel.initializeFilters();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the datePicker listener and set current date
        initDatePicker();

        // Assign Views
        btnDate = view.findViewById(R.id.btnDate); // Date selection

        btnSelectLocation = view.findViewById(R.id.btnSelectLocation); // Location selection
        etPlaces = view.findViewById(R.id.etPlaces);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvLatLng = view.findViewById(R.id.tvLatLng);

        // Button date setup
        btnDate.setText(getTodaysDate());
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Initializes the places class for this app using the given API
        Places.initialize(getContext().getApplicationContext(), BuildConfig.MAPS_API_KEY); // API hid in BuildConfig

        etPlaces.setFocusable(false);

        // Set EditText listener
        etPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Specify which data types to return
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                // Create intent to launch autocomplete feature
                Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(getContext());

                // Launch autocomplete
                startActivityForResult(i, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // Button to finish this activity and send the data to the next fragment
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add latitude and longitude filters to view model
                visitViewModel.addFilter("latitude", String.valueOf(latitude));
                visitViewModel.addFilter("longitude", String.valueOf(longitude));
//                // Create new fragment
                ExploreFragment exploreFragment = new ExploreFragment();
                // Use activity's fragment manager to change fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, exploreFragment)
                        .commit();
            }
        });

    }


    // Catches the result of the Autocomplete Feature and displays the info of the selected location
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check request code and result of activity
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get the selected place (can be an establishment, a geographic location, or a prominent point of interest)
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Set views with place info
            etPlaces.setText(place.getAddress());
            tvLocation.setText("Locality Name: " + place.getName());
            tvLatLng.setText(String.valueOf(place.getLatLng()));
            // Save latitude and longitude values
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
        }
        // Check if there has been an error
        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            // Display error with a toast
            Toast.makeText(getContext().getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Sets a listener for the datePicker dialog and displays the selected date
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save visit date to communication object
                visitViewModel.setVisitDate(new Date(year - 1900, month, dayOfMonth));
                // Create date string
                month += 1; // months start with 0
                // Format date
                String date = makeDateString(dayOfMonth, month, year);
                // Save date string in ViewModel
                visitViewModel.setVisitDateStr(date);
                // Populate view
                btnDate.setText(date);
            }
        };

        // Initialize dialog with current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        // Set style for dialog
        int style = AlertDialog.THEME_HOLO_LIGHT;
        // Create datePickerDialog
        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // set today's date as minimum date
    }

    // Returns todays date as a string
    private String getTodaysDate() {
        // Create calendar instance and get date for today
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Save visit date into ViewModel
        visitViewModel.setVisitDate(new Date(year - 1900, month, day));

        // Formate date as string
        month += 1; // months starts with 0 for January
        String date = makeDateString(day, month, year);
        // Save date string
        visitViewModel.setVisitDateStr(date);
        return date;
    }


    // Gets date info as ints and returns a string in 'MONTH DD YYYY' format
    private String makeDateString(int dayOfMonth, int month, int year) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year;
    }

    // Returns the name from the month's number
    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
            default:
                return "";
        }
    }
}
