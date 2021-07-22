package com.example.fbu_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fbu_app.R;
import com.example.fbu_app.fragments.CreateFragment;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.fragments.ProfileFragment;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; // TAG for log messages
    public static final String USER_TAG = "user"; // tag to pass user

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object responsible of adding, removing or replacing Fragments in the stack
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Assign bottom navigation bar from layout
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Create listener for bottomNavigationView items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                // Check which item was selected using ids
                switch (item.getItemId()) {
                    case R.id.action_history: // Launch compose fragment to create a post
                        fragment = new NextVisitsFragment();
                        break;
                    case R.id.action_profile: // Launch profile fragment (for current user)
                        // Get current user and pass it to bundle
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(USER_TAG, ParseUser.getCurrentUser());
                        // Assign fragment with new args
                        fragment = new ProfileFragment();
                        // Add arguments to fragment
                        fragment.setArguments(bundle);
                        break;
                    default: // By default, go to main feed
                        fragment = new CreateFragment();
                        break;
                }
                // Change to selected fragment using manager
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set home option as default
        bottomNavigationView.setSelectedItemId(R.id.action_explore);
    }
}