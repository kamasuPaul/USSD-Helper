package com.quickCodes.quickCodes.ui.main;

import android.util.Log;

import java.util.HashMap;

import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    HashMap<String,String>simcards = new HashMap<>();
    String selectedSimcard = null;

    public String getSelectedSimcard() {
        return selectedSimcard;
    }

    public void setSelectedSimcard(String selectedSimcard) {
        this.selectedSimcard = selectedSimcard;
    }
    public  void addSimcard(String operatorId,String operatorName){
        simcards.put(operatorId,operatorName);
        Log.d("ADDED",operatorName);
    }

    public HashMap<String, String> getSimcards() {
        return simcards;
    }
}
