package com.quickCodes.quickCodes.util;

import android.app.Application;
import android.provider.ContactsContract;

import com.quickCodes.quickCodes.modals.CustomAction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CustomActionsViewModel extends AndroidViewModel {
    private DataRepository repository;
    private LiveData<List<CustomAction>>customActions;
    public CustomActionsViewModel(Application application) {
        super(application);
        repository = new DataRepository(application);
        customActions = repository.getAllCustomActions();
    }
    public LiveData<List<CustomAction>> getAllCustomActions(){return customActions;}
    public void delete(CustomAction action){ repository.delete(action);}
    public void insert(CustomAction action){ repository.insert(action);}
    public void update(CustomAction action){ repository.update(action);}
    public CustomAction getAction(String id){ return repository.getCustomAction(id);}
}
