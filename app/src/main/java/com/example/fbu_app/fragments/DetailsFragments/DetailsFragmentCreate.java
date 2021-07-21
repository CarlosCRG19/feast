package com.example.fbu_app.fragments.DetailsFragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

    public static final String VISIT_TAG = "visit";

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

        // Initialize the datePicker listener and set current date
        initDatePicker();


        // Assign Views
        btnDate = view.findViewById(R.id.btnDate); // Date selection
        // Button date setup
        btnDate.setText(getTodaysDate());
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnCreateVisit = view.findViewById(R.id.btnCreateVisit);
        btnCreateVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify if business exists
                verifyBusinessExists();
                // Create new visit
                Visit newVisit = new Visit();
                newVisit.setBusiness(business);
                // Add fields
                newVisit.setUser(ParseUser.getCurrentUser());
                newVisit.addAttendee(ParseUser.getCurrentUser());
                newVisit.setDate(visitDate);
                newVisit.setDateStr(visitDateStr);
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
                        // Create bundle to pass busines as argument
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(VISIT_TAG, newVisit);
                        // Transaction to new fragment
                        ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                        confirmationFragment.setArguments(bundle);
                        // Make fragment transaction
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, confirmationFragment)
                                .commit();
                    }
                });
            }
        });
    }

    // Sets a listener for the datePicker dialog and displays the selected date
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save visit date to communication object
                visitDate = new Date(year - 1900, month, dayOfMonth);
                // Create date string
                month += 1; // months start with 0
                // Format date
                visitDateStr = makeDateString(dayOfMonth, month, year);
                // Populate view
                btnDate.setText(visitDateStr);
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

        // Save visit date
        visitDate = new Date(year - 1900, month, day);

        // Formate date as string
        month += 1; // months starts with 0 for January
        String date = makeDateString(day, month, year);
        visitDateStr = date;
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
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }
}
