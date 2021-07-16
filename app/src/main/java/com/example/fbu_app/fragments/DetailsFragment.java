package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.R;
import com.example.fbu_app.adapters.HoursAdapter;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Hour;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class DetailsFragment extends Fragment {

    Business business;
    VisitViewModel visitViewModel;

    ImageView ivBusinessImage;
    TextView tvName, tvRating, tvPrice, tvTelephone;
    Button btnGo;

    HoursAdapter adapter;
    List<Hour> hours;
    RecyclerView rvHours;

    YelpClient yelpClient;

    public DetailsFragment(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_details_go, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yelpClient = new YelpClient();

        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);

        business = getArguments().getParcelable("business");

        ivBusinessImage = view.findViewById(R.id.ivBusinessImage);
        tvName = view.findViewById(R.id.tvName);
        tvRating = view.findViewById(R.id.tvRating);
        tvPrice =view.findViewById(R.id.tvPrice);
        tvTelephone = view.findViewById(R.id.tvTelephone);

        // Set TVs with info from the business
        tvName.setText(business.getName());
        tvPrice.setText("Price: " + business.getPrice());
        tvRating.setText("Rating: " + business.getRating() + "/5");
        tvTelephone.setText("Telephone: " + business.getTelephone());

        Glide.with(getContext())
                .load(business.getImageUrl())
                .centerCrop()
                .into(ivBusinessImage);


        btnGo = view.findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new visit
                Visit newVisit = new Visit();
                newVisit.setBusiness(business);
                // Add fields
                newVisit.setUser(ParseUser.getCurrentUser());
                newVisit.setDate(visitViewModel.getVisitDate().getValue());
                newVisit.setDateStr(visitViewModel.getVisitDateStr().getValue());
                // Save visit using background thread
                newVisit.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.i("ParseSave", "Failed to save visit", e);
                            return;
                        }
                        // Display success message
                        Toast.makeText(getContext(), "Succesfully created visit!", Toast.LENGTH_SHORT).show();
                        // Transaction to new fragment
                        NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, nextVisitsFragment)
                                .commit();
                        // Change selected item in bottom nav bar
                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                        bottomNavigationView.setSelectedItemId(R.id.action_history);
                    }
                });

            }
        });

        hours = new ArrayList<>();
        adapter = new HoursAdapter(getContext(), hours);

        rvHours = view.findViewById(R.id.rvHours);
        rvHours.setAdapter(adapter);
        rvHours.setLayoutManager(new LinearLayoutManager(getContext()));

        yelpClient.getBusinessDetails(business.getYelpId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {

                    JSONObject hoursJSON = json.jsonObject.getJSONArray("hours").getJSONObject(0);
                    hours = Hour.fromJsonArray(hoursJSON.getJSONArray("open"), business);
                    adapter.addAll(hours);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("DetailsFragment", "Failure getting details: " + response, throwable);
            }
        });

    }

}