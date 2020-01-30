package com.quickCodes.quickCodes.util;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.quickCodes.quickCodes.modals.CustomAction;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;

public class DataRepository {
    CustomActionDao customActionDao;
    LiveData<List<CustomAction>> allActions;
    public DataRepository(Application application){
        MyRoomDatabase db = MyRoomDatabase.getDatabase( application);
        customActionDao = db.customActionDao();
        allActions = customActionDao.getAll();

    }

    public LiveData<List<CustomAction>> getAllCustomActions(){return allActions;}
    public void delete(CustomAction action){
        new deleteAsyncTask(customActionDao).execute(action);
    }
    public void insert(CustomAction action){
        new insertAsyncTask(customActionDao).execute(action);
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
}
