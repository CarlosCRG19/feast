package com.example.fbu_app.controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.fbu_app.models.VisitViewModel;

import java.util.Calendar;
import java.util.Date;

// Contains most of the methods required to set
// A DatePicker
public class DatePickerController {

    // Creates a datePicker using an specified listener
    public static DatePickerDialog initDatePicker(Context context, DatePickerDialog.OnDateSetListener dateSetListener) {
        // Initialize dialog with current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        // Set style for dialog
        int style = AlertDialog.THEME_HOLO_LIGHT;
        // Create datePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // set today's date as minimum date
        // Return datePicker
        return datePickerDialog;
    }

    // Returns todays date as a string
    public static String getTodaysDate(Date visitDate, String visitDateStr) {
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

    // Returns todays date as a string
    public static String getTodaysDate(VisitViewModel visitViewModel) {
        // Create calendar instance and get date for today
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Save visit date into ViewModel
        visitViewModel.setVisitDate(new Date(year - 1900, month, day));

        // Formate date as string
        month += 1; // months starts with 0 for January
        String date = makeDateString(day, month, year);
        // Save date string
        visitViewModel.setVisitDateStr(date);
        return date;
    }

    // Gets date info as ints and returns a string in 'MONTH DD YYYY' format
    public static String makeDateString(int dayOfMonth, int month, int year) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year;
    }

    // Returns the name from the month's number
    private static String getMonthFormat(int month) {
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
