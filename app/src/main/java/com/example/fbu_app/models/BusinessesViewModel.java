package com.example.fbu_app.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Communication object that stores the list of selected businesses
public class BusinessesViewModel extends ViewModel{

    private final MutableLiveData<boolean[]> makeRequest = new MutableLiveData<boolean[]>(new boolean[]{true});

    public boolean getMakeRequestFlag() { return Objects.requireNonNull(makeRequest.getValue())[0]; }

    public void setMakeRequestFlag(boolean flag) { makeRequest.getValue()[0] = flag; }

    private final MutableLiveData<List<Business>> selectedBusinesses = new MutableLiveData<>();

    private final MutableLiveData<List<Business>> displayedBusinesses = new MutableLiveData<>();

    public void initializeSelectedBusinesses() {
        selectedBusinesses.setValue(new ArrayList<>());
    }

    public void addSelectedBusiness(Business business ){
        selectedBusinesses.getValue().add(business);
    }

    public LiveData<List<Business>> getSelectedBusinesses() {
        return selectedBusinesses;
    }

    public void initializeDisplayedBusinesses() { displayedBusinesses.setValue(new ArrayList<>()); }

    public void addDisplayedBusiness(Business business ){ displayedBusinesses.getValue().add(business); }

    public void addDisplayedBusinesses(List<Business> businesses ){ displayedBusinesses.getValue().addAll(businesses); }

    public LiveData<List<Business>> getDisplayedBusinesses() {
        return displayedBusinesses;
    }

    public void clearDisplayedBusinesses() {
        displayedBusinesses.getValue().clear();
    }

}
