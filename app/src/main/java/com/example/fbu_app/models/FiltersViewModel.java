package com.example.fbu_app.models;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

// ViewModel object to pass data between fragments, this is attached to the Activity lifecycle, so it persist between fragment transactions)
public class FiltersViewModel extends ViewModel {

    // List of filters as mutable data
    private final MutableLiveData<List<Pair<String, String>>> filters = new MutableLiveData<>();

    // Creates an empty ArrayList
    public void initializeFilters() {
        filters.setValue(new ArrayList<>());
    }

    // Adds new filter to the ArrayList
    public void addFilter(Pair<String, String> filter){
        filters.getValue().add(filter);
    }

    // Returns LiveData that contains the list. Must use getValue() to access the list
    public LiveData<List<Pair<String, String>>> getFilters() {
        return filters;
    }


}
