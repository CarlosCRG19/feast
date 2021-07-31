package com.example.fbu_app.fragments;

import android.opengl.ETC1;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.DialogFragments.InviteFragment;
import com.example.fbu_app.fragments.DialogFragments.NotificationsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

public class ConfirmationFragment extends Fragment {

    Visit visit;
    ImageView ivBusinessImage;
    TextView tvName, tvDate, tvAddress;

    Button btnConfirm;

    // Invite button
    ImageButton btnInvite;

    // Map view
    MapView mapView;
    // Google Map
    GoogleMap googleMap;

    public ConfirmationFragment() {}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Get visit from arguments
        visit = getArguments().getParcelable("visit");
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Business visitBusiness = visit.getBusiness();

        ivBusinessImage = view.findViewById(R.id.ivBusinessImage);
        tvName = view.findViewById(R.id.tvName);
        tvDate = view.findViewById(R.id.tvDate);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        // Invite button
        btnInvite = view.findViewById(R.id.btnInvite);
        // Map view for business location
        mapView = view.findViewById(R.id.mvLocation);

        // Get business location
        LatLng businessLocation = new LatLng(visitBusiness.getCoordLatitude(), visitBusiness.getCoordLongitude());
        // Setup map with location
        setUpMap(businessLocation);


        String enjoyText = "Enjoy your next feast at <font color='#F65C36'>" + visitBusiness.getName() + "</font>!";
        tvName.setText(Html.fromHtml(enjoyText, 42));
        tvDate.setText(visit.getDateStr());
        // Set address text
        String addressText = visitBusiness.getAddress() + ", " + visitBusiness.getCity() + ", " + visitBusiness.getCountry();
        tvAddress.setText(addressText);

        Glide.with(getContext())
                .load(visitBusiness.getImageUrl())
                .into(ivBusinessImage);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transaction to new fragment
                NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, nextVisitsFragment)
                        .commit();
            }
        });

        // TEST BUTTON LISTENER
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create bundle to pass visit
                Bundle bundle = new Bundle();
                bundle.putParcelable("visit", visit);
                // Create new instance of fragment
                InviteFragment inviteFragment = new InviteFragment();
                // Add arguments to fragment
                inviteFragment.setArguments(bundle);
                // Launch fragment
                inviteFragment.show(getChildFragmentManager(), "fragment_invite");
            }
        });

    }


    // MAP METHODS

    // Assigns the map component of the app. Which is represented as a child fragment inside LocationFragment
    private void setUpMap(LatLng businessLocation) {
        // Check if map is null
        if (mapView != null) {
            // Call onCreate method
            mapView.onCreate(null);
            // Set callback to begin
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull @NotNull GoogleMap map) {
                    // Initialize map action
                    MapsInitializer.initialize(getContext());
                    // assign map to member variable once it is ready
                    googleMap = map;
                    // Set map style
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    pointLocation(businessLocation);
                }
            });
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
