package com.example.fbu_app.fragments.DetailsFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

public class DetailsFragmentGo extends DetailsFragmentBase {

    VisitViewModel visitViewModel;
    Button btnGo;


    public DetailsFragmentGo(){}

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

        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);

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
    }

}