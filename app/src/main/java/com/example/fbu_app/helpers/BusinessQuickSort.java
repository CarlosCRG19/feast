package com.example.fbu_app.helpers;

import android.util.Log;

import com.example.fbu_app.models.Business;

import java.util.Collections;
import java.util.List;

// Implementations of QuickSort algorithm to sort business
// according to an specific field
public class BusinessQuickSort {

    /* This function takes last element as pivot, places
       the pivot element at its correct position in sorted
       array, and places all smaller (smaller than pivot)
       to left of pivot and all greater elements to right
       of pivot
     */
    private static int partition(List<Business> businessList, String attribute, int low, int high) {

        // Get business at high position
        Business pivotBusiness = businessList.get(high);
        // Get the value to be used for comparison, specified by attribute
        int pivotVal = businessAttrSwitch(pivotBusiness, attribute);

        // Index of smaller element and right position of pivot so far
        int i = low - 1;

        for (int j=low; j <= high - 1; j++) {

            // Get business at position
            Business compareBusiness = businessList.get(j);
            // Get value for the business to be compared
            int compareVal = businessAttrSwitch(compareBusiness, attribute);

            // If value is smaller than pivot
            if( compareVal < pivotVal ) {
                // increment index of smaller element
                i++;
                Collections.swap(businessList, i, j);
            }
        }
        Collections.swap(businessList, i+1, high);
        return i+1;
    }

    /* The main function that implements QuickSort
          businessesList --> Array to be sorted,
          low --> Starting index,
          high --> Ending index
    */
    public static void quickSort(List<Business> businessesList, String attribute, int low, int high) {
        if (low < high) {
            // pi is the partitioning index, so element at place pi
            // is at right place
            int pi = partition(businessesList, attribute, low, high);

            // Separately sort elements before the partition and after
            // the partition
            quickSort(businessesList, attribute, low, pi -1);
            quickSort(businessesList, attribute, pi + 1, high);
        }
    }

    // Returns the value of an specific attribute from a business
    private static int businessAttrSwitch(Business business, String attribute) {
        switch(attribute) {
            case "distance":
                return business.getDistance();
            case "price":
                return business.getPriceInt();
            default:
                return business.getRating();
        }
    }


}
