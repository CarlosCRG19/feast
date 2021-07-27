package com.example.fbu_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.fbu_app.R;
import com.example.fbu_app.fragments.CreateFragment;
import com.example.fbu_app.fragments.LocationFragment;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.fragments.ProfileFragments.OwnProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.ProfileFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; // TAG for log messages
    public static final String USER_TAG = "user"; // tag to pass user

    // Floationg action button in the middle of the bottom navbar
    private FloatingActionButton btnCreate;

    // Navigation component
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object responsible of adding, removing or replacing Fragments in the stack
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Assign bottom navigation bar from layout
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Set background to of view to remove shadow
        bottomNavigationView.setBackground(null);
        // Disable placeholder option that creates space
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        // Create listener for bottomNavigationView items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                // Check which item was selected using ids
                if (item.getItemId() == R.id.action_profile) { // Launch profile fragment (for current user)
                    // Get current user and pass it to bundle
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(USER_TAG, ParseUser.getCurrentUser());
                    // Assign fragment with new args
                    fragment = new OwnProfileFragment();
                    // Add arguments to fragment
                    fragment.setArguments(bundle);
                } else {
                    // By default, go to main feed
                    fragment = new NextVisitsFragment();
                }
                // Change to selected fragment using manager
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set home option as default
        bottomNavigationView.setSelectedItemId(R.id.action_history);

        // CREATE BUTTON SETUP

        // Set listener for button
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new location fragment
                LocationFragment locationFragment = new LocationFragment();
                // Make fragment transaction
                fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, locationFragment)
                        .commit();

                // Hide bottom navbar
                hideBottomNavBar();

            }
        });
    }

    // OTHER METHODS
    private void hideBottomNavBar() {
        // Change visibility of toolbar
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        // Change visibility of this button
        btnCreate.hide();
    }

}