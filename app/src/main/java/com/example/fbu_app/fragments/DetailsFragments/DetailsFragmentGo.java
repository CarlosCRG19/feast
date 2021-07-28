package com.example.fbu_app.fragments.DetailsFragments;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.fragments.ConfirmationFragment;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public class DetailsFragmentGo extends DetailsFragmentBase {

    // VIEW MODEL
    VisitViewModel visitViewModel;

    // VIEWS
    ImageButton btnHide;
    Button btnGo;


    public DetailsFragmentGo(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_details_go, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get view model
        visitViewModel = ViewModelProviders.of(getActivity()).get(VisitViewModel.class);
        // Assign views
        btnGo = view.findViewById(R.id.btnGo);
        btnHide = view.findViewById(R.id.btnHide);
    }

    @Override
    protected void setClickListeners() {
        super.setClickListeners();
        // Button to create a visit from detail screen
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify if business exists and pass data info to create visit
                Date visitDate = visitViewModel.getVisitDate().getValue();
                String visitDateStr = visitViewModel.getVisitDateStr().getValue();
                createVisit(visitDate, visitDateStr);
            }
        });

        // Button to return to to previous fragment
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}