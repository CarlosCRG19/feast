package com.example.fbu_app.fragments;

import com.example.fbu_app.BuildConfig;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.controllers.DatePickerController;
import com.example.fbu_app.models.BusinessesViewModel;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.gms.common.api.Status;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// User will be able to select a location and date for the visit. The first requests for Businesses will be made
// using the selected location
public class LocationFragment extends Fragment {

    public static final String TAG = "LocationFragment"; // tag for log messages
    public static final int AUTOCOMPLETE_REQUEST_CODE = 42;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 100;


    VisitViewModel visitViewModel; // Communication object between fragments
    BusinessesViewModel businessesViewModel;

    // DATE VARIABLES
    Date visitDate;
    String visitDateStr;

    // VIEWS
    DatePickerDialog datePickerDialog; // Date picking
    Button btnDate, btnConfirmLocation;
    EditText etPlaces; // Location selection

    // Latitude and Longitude values for selected location
    double latitude, longitude;

    // Google map
    GoogleMap googleMap;

    // Location Provider
    FusedLocationProviderClient fusedLocationProviderClient;

    // Required empty constructor
    public LocationFragment() {};

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set value for ViewModel
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);
        // Initialize the filters map
        visitViewModel.initializeFilters();
        //Get businesses view model
        businessesViewModel = ViewModelProviders.of(getActivity()).get(BusinessesViewModel.class);
        businessesViewModel.setMakeRequestFlag(true);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Setup Searchbar
        etPlaces = view.findViewById(R.id.etPlaces);
        // Setup Map
        setUpMap();

        // Create Listener for datePicker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save visit date to communication object
                visitDate = new Date(year - 1900, month, dayOfMonth);
                // Create date string
                month += 1; // months start with 0
                // Format date
                visitDateStr = DatePickerController.makeDateString(dayOfMonth, month, year);
                // Populate view
                btnDate.setText(visitDateStr);
            }
        };
        // Initialize the datePicker using controller
        datePickerDialog = DatePickerController.initDatePicker(getContext(), dateSetListener);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());



        // Assign Views
        btnDate = view.findViewById(R.id.btnDate); // Date selection
        btnConfirmLocation = view.findViewById(R.id.btnConfirmLocation); // Location selection
        // Button date setup
        Pair<Date, String> todaysDate = DatePickerController.getTodaysDate();
        visitDate = todaysDate.first;
        visitDateStr = todaysDate.second;
        btnDate.setText(visitDateStr);
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
                        .setCountries(Arrays.asList("MX", "US"))
                        .build(getContext());

                // Launch autocomplete
                startActivityForResult(i, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // Button to finish this activity and send the data to the next fragment
        btnConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save values for date
                visitViewModel.setVisitDate(visitDate);
                visitViewModel.setVisitDateStr(visitDateStr);
                // Add latitude and longitude filters to view model
                visitViewModel.addFilter("latitude", String.valueOf(latitude));
                visitViewModel.addFilter("longitude", String.valueOf(longitude));
                // Set radius search to 20km
                visitViewModel.addFilter("radius", "20000");
                // Search only for food businesses
                visitViewModel.addFilter("term", "food");
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
            // Point location on map
            pointLocation(place.getLatLng());
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

    // CURRENT LOCATION METHODS

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Use requested code for launched activity and determine whether request was approved
        if (requestCode == REQUEST_LOCATION_PERMISSIONS_CODE
                && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // If permissions are granted, get user's location
            getCurrentLocation();
        } else {
            // When permissions are denied, display a toas
            Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyPermissionsAndGetLocation() {
        // Check if required permissions are granted
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Call method to get user's current location
            getCurrentLocation();

        } else {
            // When permission is not granted
            ActivityCompat.requestPermissions(getActivity()
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            ,Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,REQUEST_LOCATION_PERMISSIONS_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE
        );

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize locaiton
                    Location location = task.getResult();
                    // Check condition if a location has been received
                    if(location != null){
                        //When location result is not null, save latitude and longitude
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Create a marker on specified location using pointLocation
                        LatLng locationLatLng = new LatLng(latitude, longitude);
                        pointLocation(locationLatLng);
                        etPlaces.setText("Your location");
                    } else {
                        // When location result is null initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        // Initialize location callback
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location newLocation = locationResult.getLastLocation();
                                //When location result is not null, save latitude and longitude
                                latitude = newLocation.getLatitude();
                                longitude = newLocation.getLongitude();
                                // Create a marker on specified location using pointLocation
                                LatLng locationLatLng = new LatLng(latitude, longitude);
                                pointLocation(locationLatLng);
                                // Change text in editText
                                etPlaces.setText("Your location");
                            }
                        };
                        // Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }

                }
            });
        } else {
            // When location service is not enabled, open location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }



    // MAP METHODS

    // Assigns the map component of the app. Which is represented as a child fragment inside LocationFragment
    private void setUpMap() {
        // Check if map is null
        if (googleMap == null) {
            // Get map from layout
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
           // Check if we were successful on getting the map
            if (supportMapFragment != null) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull @NotNull GoogleMap map) {
                        // assign map to member variable once it is ready
                        googleMap = map;
                        verifyPermissionsAndGetLocation();
                    }
                });
            }
        }
    }

    // Creates a new marker on the map and erases previous locations
    private void pointLocation(@NonNull LatLng latLng) {
        // Create new marker
        MarkerOptions markerOptions = new MarkerOptions();
        // Assign position to marker
        markerOptions.position(latLng);
        // Clear all markers
        googleMap.clear();
        // Make animation for camera movement
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        // Add new marker
        googleMap.addMarker(markerOptions);
    }


}
