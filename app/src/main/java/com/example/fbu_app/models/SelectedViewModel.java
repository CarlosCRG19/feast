package com.example.fbu_app.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

// Communication object that stores the list of selected businesses
public class SelectedViewModel extends ViewModel{

    private final MutableLiveData<List<Business>> selectedBusinesses = new MutableLiveData<>();

    public void initializeSelectedBusinesses() {
        selectedBusinesses.setValue(new ArrayList<>());
    }

    public void addBusiness(Business business ){
        selectedBusinesses.getValue().add(business);
    }

    public LiveData<List<Business>> getSelectedBusinesses() {
        return selectedBusinesses;
    }
}
