package com.quickCodes.quickCodes.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.DataRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<SimCard>> simcards;
    private DataRepository repository;
    private LiveData<List<UssdActionWithSteps>> ussdActions;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        simcards = new MutableLiveData<>();
        mText.setValue("This is home fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<SimCard>> getSimCards(Context context) {
        simcards.setValue(Tools.getAvailableSimCards(context));
        return simcards;
    }

    public LiveData<List<UssdActionWithSteps>> getUssdActions(Context context) {
        repository = new DataRepository(context);
        ussdActions = repository.getAllUssdActions();
        return ussdActions;
    }

}