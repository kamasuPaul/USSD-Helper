package com.quickCodes.quickCodes.util;

import android.app.Application;
import android.os.AsyncTask;

import com.quickCodes.quickCodes.modals.CustomAction;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class DataRepository {
    CustomActionDao customActionDao;
    UssdActionDao ussdActionDao;
    LiveData<List<CustomAction>> allActions;
    LiveData<List<UssdActionWithSteps>> allUssdActions;
    public DataRepository(Application application){
        MyRoomDatabase db = MyRoomDatabase.getDatabase( application);
        customActionDao = db.customActionDao();
        ussdActionDao = db.ussdActionDao();
        allActions = customActionDao.getAll();
        allUssdActions = ussdActionDao.getActionsWithSteps();


        //constraints
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

        //Worker
        PeriodicWorkRequest workRequest =
            new PeriodicWorkRequest.Builder(DownloadWorker.class,24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(application).enqueue(workRequest);
    }

    public LiveData<List<UssdActionWithSteps>> getAllUssdActions(){return allUssdActions;}
    public LiveData<List<CustomAction>> getAllCustomActions(){return allActions;}
    public void delete(CustomAction action){
        new deleteAsyncTask(customActionDao).execute(action);
    }
    public void insert(CustomAction action){
        new insertAsyncTask(customActionDao).execute(action);
    }
    public void insert(UssdAction action){
        new insertUssdAsyncTask(ussdActionDao).execute(action);
    }
    public void insertAll(UssdActionWithSteps ussdActionWithSteps){
        new insertAllAsyncTask(ussdActionDao).execute(ussdActionWithSteps);
    }

    public void update(CustomAction action) {
        new updateAsyncTask(customActionDao).execute(action);
    }
    public CustomAction getCustomAction(String id){
        AsyncTask<String, Void, CustomAction> execute = new getCustomActionAsyncTask(customActionDao).execute(id);
        try {
            CustomAction customAction = execute.get();
            return customAction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static class deleteAsyncTask extends AsyncTask<CustomAction,Void,Void>{
        CustomActionDao d;
        deleteAsyncTask(CustomActionDao dao){
            d =dao;
        }
        @Override
        protected Void doInBackground(CustomAction... customActions) {
            d.delete(customActions[0]);
            return null;
        }
    }
    private static class insertAsyncTask extends AsyncTask<CustomAction,Void,Void>{
        CustomActionDao d;
        insertAsyncTask(CustomActionDao dao){
            d =dao;
        }
        @Override
        protected Void doInBackground(CustomAction... customActions) {
            d.insert(customActions[0]);
            return null;
        }
    }
    private static class insertUssdAsyncTask extends AsyncTask<UssdAction,Void,Void>{
        UssdActionDao d;
        insertUssdAsyncTask(UssdActionDao dao){
            d =dao;
        }
        @Override
        protected Void doInBackground(UssdAction... ussdActionWithSteps) {
            d.insert(ussdActionWithSteps[0]);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<CustomAction,Void,Void>{
        CustomActionDao d;
        updateAsyncTask(CustomActionDao dao){
            d =dao;
        }

        @Override
        protected Void doInBackground(CustomAction... customActions) {
            d.update(customActions[0]);
            return null;
        }
    }

    private static class getCustomActionAsyncTask extends AsyncTask<String,Void,CustomAction> {
        CustomActionDao d;

        public getCustomActionAsyncTask(CustomActionDao customActionDao) {
            d = customActionDao;
        }


        @Override
        protected CustomAction doInBackground(String... ids) {
            CustomAction customAction = d.get(String.valueOf(ids[0]));
            return customAction;
        }
    }

    private static class insertAllAsyncTask extends AsyncTask<UssdActionWithSteps,Void,Void> {
        UssdActionDao dao;
        public insertAllAsyncTask(UssdActionDao ussdActionDao) {
            dao = ussdActionDao;
        }

        @Override
        protected Void doInBackground(UssdActionWithSteps... ussdActionWithSteps) {
            dao.insertStepsForAction(ussdActionWithSteps[0]);
            return null;
        }
    }
}
