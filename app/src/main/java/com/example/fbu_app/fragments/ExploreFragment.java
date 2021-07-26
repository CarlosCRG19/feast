package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.adapters.CardStackAdapter;
import com.example.fbu_app.R;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.VisitViewModel;
import com.example.fbu_app.models.BusinessesViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

// Main Fragment where the user will see the restaurants matching the criteria
public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment"; // tag for log messages

    // Filters data
    private VisitViewModel visitViewModel; // communication object between fragments

    // Navigation views
    Button btnCompare;
    ImageButton btnFilters;

    // API client to handle requests
    YelpClient yelpClient;

    // Model to store received businesses
    List<Business> displayedBusinesses;

    // ViewModel that stores the selected businesses
    BusinessesViewModel businessesViewModel;

    // CardStack tools
    CardStackAdapter adapter; // binds data to the cards
    CardStackLayoutManager manager; // general manager of the CardStack
    CardStackView cardStackView; // view from yuyakaido library

    // Index of business from displayeBusinesses that is on screen
    int displayIndex;

    // Required empty constructor
    public ExploreFragment(){}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set value for ViewModel
        businessesViewModel = ViewModelProviders.of(getActivity()).get(BusinessesViewModel.class);
        // Initialize the businesses to be selected
        businessesViewModel.initializeSelectedBusinesses();
        // Initialize the businesses to be displayed
        businessesViewModel.initializeDisplayedBusinesses();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get ViewModel, which stores the filters applied atm
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);

        // Create instance of Client
        yelpClient = new YelpClient();

        // Init the list of businesses
        displayedBusinesses = new ArrayList<>();
        // Init adapter to bind data
        adapter = new CardStackAdapter(getContext(), displayedBusinesses);

        // Check businessesViewModel to get displayedBusinesses
        if (businessesViewModel.getMakeRequestFlag()) {
            // If flag say, get businesses
            // Request to get businesses that match the current filters
            yelpClient.getMatchingBusinesses(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "Success doing request");
                            // Receive response
                    JSONObject response = json.jsonObject;
                    try {
                        // Get list of businesses
                        JSONArray jsonArray = response.getJSONArray("businesses");
                        // Use static methods to create array of businesses (these businesses are only stored locally, not in Parse)
                        displayedBusinesses = Business.fromJsonArray(jsonArray);
                        // Add new businesses to adapter
                        adapter.clear();
                        adapter.addAll(displayedBusinesses);

                        // Log messages to see functionality
                        Log.i(TAG, "NEW REQUEST - " + String.valueOf(displayedBusinesses.size()) + " businesses found for filters:");
                        visitViewModel.getFilters().getValue().forEach((key, value) -> Log.i(TAG, key + ": " + value));
                        Log.i(TAG, "FOUND BUSINESSES:");
                        Log.i(TAG, jsonArray.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    // Display log message
                    Log.i(TAG, "Failure doing request: " + response, throwable);
                }
            }, Objects.requireNonNull(visitViewModel.getFilters().getValue()));
        } else {
            Log.i(TAG, "Using view model!");
            // Save new displayedBusiness from a copy of viewModel
            displayedBusinesses = new ArrayList<>(businessesViewModel.getDisplayedBusinesses().getValue());
            // Clear adapter
            adapter.clear();
            // Add new businesses
            adapter.addAll(displayedBusinesses);
        }

        // ------ CARD STACK SETUP ------ //

        // Set index of the business that is being displayed
        displayIndex = 0;

        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                if(direction == Direction.Top) {
                    Log.i(TAG, "onCardDragged: direction " + direction );
                }
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + "d=" + direction);
                // Get position of manager
                int selectedPosition = manager.getTopPosition() - 1;
                // Handle swipe direction
                if(direction == Direction.Right) {
                    // Get business from position
                    Business selectedBusiness = displayedBusinesses.get(selectedPosition);
                    // Check if business is already in selectedBusinesses
                    if (!isInBusinesses(selectedBusiness)){
                        // Save business into ViewModel
                        businessesViewModel.addSelectedBusiness(selectedBusiness);
                        // Display log and toast messages
                        Log.i(TAG, "Total Businesses selected: " + businessesViewModel.getSelectedBusinesses().getValue().size());
                        Toast.makeText(getContext(), "Restaurant selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                // Change display index
                displayIndex += 1;
            }

            @Override
            public void onCardRewound() { }

            @Override
            public void onCardCanceled() { }

            @Override
            public void onCardAppeared(View view, int position) { }

            @Override
            public void onCardDisappeared(View view, int position) { }
        });

        // Manager settings
        manager.setStackFrom(StackFrom.None); // no stack shown
        manager.setSwipeThreshold(0.3f); // threshold to consider item as selected
        manager.setMaxDegree(20.0f); // degree of card rotation when dragged
        manager.setDirections(Direction.HORIZONTAL); // only allow horizontal swipes
        manager.setStackFrom(StackFrom.Top); // show stack at the bottom
        manager.setTranslationInterval(4.0f);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator()); // interpolator to show overlay when dragged

        // Set CardStackView
        cardStackView = view.findViewById(R.id.card_stack_view);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        // Navigation button to launch FiltersFragment
        btnFilters = view.findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change flag for new businesses
                businessesViewModel.setMakeRequestFlag(true);
                // Create new instance of filters fragment
                FiltersFragment filtersFragment = new FiltersFragment();
                // Make transaction
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, filtersFragment)
                        .addToBackStack(null) // replace transaction is saved to the back stack so user can return to this fragment
                        .commit();
            }
        });

        // Navigation button to launch CompareFragment
        btnCompare = view.findViewById(R.id.btnCompare);
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear businesses stored in the view model
                businessesViewModel.clearDisplayedBusinesses();
                // Get the businesses that are currently being displayed
                List<Business> currentDisplayedBusinesses = displayedBusinesses.subList(displayIndex, displayedBusinesses.size());
                // Add new current businesses to ViewModel
                businessesViewModel.addDisplayedBusinesses(currentDisplayedBusinesses);
                // Change request flag
                businessesViewModel.setMakeRequestFlag(false);
                // Make fragment transaction
                CompareFragment compareFragment = new CompareFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, compareFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


    }

    // Checks whether the newly selected restaurant has previously been selected
    public boolean isInBusinesses(Business business) {
        // Set boolean flag
        boolean result = false;
        // Iterate through the selected businesses list (O(n))
        for (Business businessInList : businessesViewModel.getSelectedBusinesses().getValue()) {
            // Compare businesses through yelpId
            if(businessInList.getYelpId().equals(business.getYelpId())) {
                // Change result and exit loop
                result = true;
                break;
            }
        }
        return  result;
    }
}


