package com.quickCodes.quickCodes.util.database;

import android.app.Application;
import android.os.AsyncTask;

import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.List;

import androidx.lifecycle.LiveData;

public class DataRepository {
    UssdActionDao ussdActionDao;
    LiveData<List<UssdActionWithSteps>> allUssdActions;
    public DataRepository(Application application){
        MyRoomDatabase db = MyRoomDatabase.getDatabase( application);
        ussdActionDao = db.ussdActionDao();
        allUssdActions = ussdActionDao.getActionsWithSteps();


    }

    public LiveData<List<UssdActionWithSteps>> getAllUssdActions(){return allUssdActions;}

    public void delete(UssdAction ussdAction){
        new deleteAsyncTask(ussdActionDao).execute(ussdAction);
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

    public List<UssdActionWithSteps> getAllUssdActionsNoLiveData() {
        try {
            List<UssdActionWithSteps> ussdActionWithSteps =
                new getAllUssdActionAsyncTask(ussdActionDao).execute().get();
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
            d.updateUssdActionWithSteps(ussdActionWithSteps[0]);
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
            dao.insertUssdActionWithSteps(ussdActionWithSteps[0]);
            return null;
        }
    }

    private static class getAllUssdActionAsyncTask extends AsyncTask<Void, Void, List<UssdActionWithSteps>> {
        UssdActionDao d;

        public getAllUssdActionAsyncTask(UssdActionDao dao) {
            d = dao;
        }

        @Override
        protected List<UssdActionWithSteps> doInBackground(Void... voids) {
            List<UssdActionWithSteps> ussdActionWithSteps = d.getActionsWithStepsNoLiveData();
            return ussdActionWithSteps;
        }
    }

}
