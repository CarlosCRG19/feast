package com.example.fbu_app.fragments.DetailsFragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fbu_app.R;
import com.example.fbu_app.controllers.DatePickerController;
import com.example.fbu_app.fragments.ConfirmationFragment;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class DetailsFragmentCreate extends DetailsFragmentBase {

    Button btnDate, btnCreateVisit;

    Date visitDate;
    String visitDateStr;

    DatePickerDialog datePickerDialog;

    public DetailsFragmentCreate(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_details_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        // Initialize the datePicker using static method
        datePickerDialog = DatePickerController.initDatePicker(getContext(), dateSetListener);

        // Assign Views
        btnDate = view.findViewById(R.id.btnDate); // Date selection
        btnCreateVisit = view.findViewById(R.id.btnCreateVisit);
    }

    @Override
    protected void setClickListeners() {
        super.setClickListeners();
        // Button date setup
        btnDate.setText(DatePickerController.getTodaysDate(visitDate, visitDateStr));
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        // Set listener to create button
        btnCreateVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify if business exists and pass date info to create visit
                createVisit(visitDate, visitDateStr);
            }
        });
    }

}

