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
            new PeriodicWorkRequest.Builder(DownloadWorker.class,15,TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(application).enqueue(workRequest);
    }

    public LiveData<List<UssdActionWithSteps>> getAllUssdActions(){return allUssdActions;}
    public LiveData<List<CustomAction>> getAllCustomActions(){return allActions;}
    public void delete(UssdAction ussdAction){
        new deleteAsyncTask(ussdActionDao).execute(ussdAction);
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

    public void update(UssdActionWithSteps action) {

//        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
        new updateAsyncTask(ussdActionDao).execute(action);
    }
    public UssdActionWithSteps getUssdAction(String id){
        AsyncTask<String, Void, UssdActionWithSteps> execute = new getUssdActionAsyncTask(ussdActionDao).execute(id);
        try {
            UssdActionWithSteps ussdActionWithSteps = execute.get();
            return ussdActionWithSteps;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static class deleteAsyncTask extends AsyncTask<UssdAction,Void,Void>{
        UssdActionDao d;
        deleteAsyncTask(UssdActionDao dao){
            d =dao;
        }
        @Override
        protected Void doInBackground(UssdAction... customActions) {
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
    private static class updateAsyncTask extends AsyncTask<UssdActionWithSteps,Void,Void>{
        UssdActionDao d;
        updateAsyncTask(UssdActionDao dao){
            d =dao;
        }

        @Override
        protected Void doInBackground(UssdActionWithSteps... ussdActionWithSteps) {
            d.updateActionWithSteps(ussdActionWithSteps[0]);
            return null;
        }
    }

    private static class getUssdActionAsyncTask extends AsyncTask<String,Void,UssdActionWithSteps> {
        UssdActionDao d;

        public getUssdActionAsyncTask(UssdActionDao dao) {
            d = dao;
        }


        @Override
        protected UssdActionWithSteps doInBackground(String... ids) {
            UssdActionWithSteps ussdActionWithSteps = d.get(String.valueOf(ids[0]));
            return ussdActionWithSteps;
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
