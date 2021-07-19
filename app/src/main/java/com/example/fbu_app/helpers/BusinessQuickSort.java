package com.example.fbu_app.helpers;

import com.example.fbu_app.models.Business;

import java.util.List;

public class BusinessQuickSort {


    // UTILITY FUNCTIONS
    private  static void swap(List<Business> businessesList, int i, int j) {
        Business tempBusiness = businessesList.get(i);
        businessesList.set(i, businessesList.get(j));
        businessesList.set(j, tempBusiness);
    }

    private static int partition(List<Business> businessList, String attribute, int low, int high) {
        int pivot = (int) businessList.get(high).get(attribute);

        int i = low - 1;

        for (int j=low; j <= high - 1; j++) {

            if( (int) businessList.get(j).get(attribute) < pivot ) {
                i++;
                swap(businessList, i, j);
            }

        }
        swap(businessList, i+1, high);
        return i+1;
    }

    public static void quickSort(List<Business> businessesList, String attribute, int low, int high) {
        if (low < high) {
            int pi = partition(businessesList, attribute, low, high);

            quickSort(businessesList, attribute, low, pi -1);
            quickSort(businessesList, attribute, pi + 1, high);
        }
    }



}
