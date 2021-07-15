package com.example.fbu_app.models;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// ViewModel object to pass data between fragments, this is attached to the Activity lifecycle, so it persist between fragment transactions)
public class VisitViewModel extends ViewModel {

    // Date of visit
    private final MutableLiveData<Date> visitDate = new MutableLiveData<>();
    private final MutableLiveData<String> visitDateStr = new MutableLiveData<>();

    // List of filters as mutable data
    private final MutableLiveData<HashMap<String, String>> filtersMap = new MutableLiveData<>();

    // Creates an empty ArrayList
    public void initializeFilters() {
        filtersMap.setValue(new HashMap<String, String>());
    }

    // Adds new filter to the ArrayList
    public void addFilter(String filter, String value){
        filtersMap.getValue().put(filter, value);
    }

    // Returns LiveData that contains the list. Must use getValue() to access the list
    public LiveData<HashMap<String, String>> getFilters() {
        return filtersMap;
    }

    public void setVisitDate(Date date) {
        visitDate.setValue(date);
    }

    public void setVisitDateStr(String dateStr) {
        visitDateStr.setValue(dateStr);
    }

    public LiveData<Date> getVisitDate() {
        return visitDate;
    }

    public LiveData<String> getVisitDateStr() {
        return visitDateStr;
    }


}
