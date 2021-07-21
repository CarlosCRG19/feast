package com.example.fbu_app.helpers;

import android.util.Log;

import com.example.fbu_app.models.Business;

import java.util.Collections;
import java.util.List;

public class BusinessQuickSort {

    // UTILITY FUNCTIONS
    private static void swap(List<Business> businessesList, int i, int j) {
        Business tempBusiness = businessesList.get(i);
        businessesList.set(i, businessesList.get(j));
        businessesList.set(j, tempBusiness);
    }

    private static int partition(List<Business> businessList, String attribute, int low, int high) {

        Business pivotBusiness = businessList.get(high);
        int pivotVal = businessAttrSwitch(pivotBusiness, attribute);

        int i = low - 1;

        for (int j=low; j <= high - 1; j++) {

            Business compareBusiness = businessList.get(j);

            int compareVal = businessAttrSwitch(compareBusiness, attribute);

            if( compareVal < pivotVal ) {
                i++;
                Collections.swap(businessList, i, j);
            }
        }
        Collections.swap(businessList, i+1, high);
        return i+1;
    }

    public static void quickSort(List<Business> businessesList, String attribute, int low, int high) {
        if (low < high) {
            int pi = partition(businessesList, attribute, low, high);

            quickSort(businessesList, attribute, low, pi -1);
            quickSort(businessesList, attribute, pi + 1, high);
        }
    }

    public static int businessAttrSwitch(Business business, String attribute) {
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
