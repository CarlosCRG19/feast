package com.example.fbu_app.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Communication object that stores the list of selected businesses
public class BusinessesViewModel extends ViewModel{

    //-- FIELDS --//

    // Flag that represents the need for new businesses to be displayed or to persist the current ones
    private final MutableLiveData<boolean[]> makeRequest = new MutableLiveData<boolean[]>(new boolean[]{true});

    // List of business that were swiped to the right by the user
    private final MutableLiveData<List<Business>> selectedBusinesses = new MutableLiveData<>();

    // List of businesses that are currently displayed in explore screen
    private final MutableLiveData<List<Business>> displayedBusinesses = new MutableLiveData<>();

    //-- INITIALIZERS --//

    public void initializeDisplayedBusinesses() { displayedBusinesses.setValue(new ArrayList<>()); }

    public void initializeSelectedBusinesses() { selectedBusinesses.setValue(new ArrayList<>()); }


    //-- SETTERS --//

    public void setMakeRequestFlag(boolean flag) { makeRequest.getValue()[0] = flag; }

    public void addSelectedBusiness(Business business ){ selectedBusinesses.getValue().add(business); }

    public void addDisplayedBusiness(Business business ){ displayedBusinesses.getValue().add(business); }

    public void addDisplayedBusinesses(List<Business> businesses ){ displayedBusinesses.getValue().addAll(businesses); }

    //-- GETTERS --//

    public boolean getMakeRequestFlag() { return Objects.requireNonNull(makeRequest.getValue())[0]; }

    public LiveData<List<Business>> getSelectedBusinesses() { return selectedBusinesses; }

    public LiveData<List<Business>> getDisplayedBusinesses() {
        return displayedBusinesses;
    }

    //-- CLEAR METHOD --//

    public void clearDisplayedBusinesses() {
        displayedBusinesses.getValue().clear();
    }

}
