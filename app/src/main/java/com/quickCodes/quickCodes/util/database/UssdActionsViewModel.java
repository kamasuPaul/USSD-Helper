package com.quickCodes.quickCodes.util.database;

import android.app.Application;

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UssdActionsViewModel extends AndroidViewModel {
    private DataRepository repository;
    private LiveData<List<UssdActionWithSteps>>ussdActions;
    public UssdActionsViewModel(Application application) {
        super(application);
        repository = new DataRepository(application);

        ussdActions = repository.getAllUssdActions();
    }
    public LiveData<List<UssdActionWithSteps>> getAllCustomActions(){return ussdActions;}

    public List<UssdActionWithSteps> getAllActionsWithStepsNoLiveData() {
        return repository.getAllUssdActionsNoLiveData();
    }

    public void insert(UssdAction action,List<Step>steps) {
        repository.insertAll(new UssdActionWithSteps(action,steps));
    }
    public void delete(UssdActionWithSteps actionWithSteps){ repository.delete(actionWithSteps.action);}
    public void update(UssdActionWithSteps actionWithSteps){ repository.update(actionWithSteps);}
    public UssdActionWithSteps getussdActionWithSteps(String id){ return repository.getUssdAction(id);}
}
