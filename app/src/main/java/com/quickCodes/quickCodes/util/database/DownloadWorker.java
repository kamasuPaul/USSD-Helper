package com.quickCodes.quickCodes.util.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DownloadWorker extends Worker {
    String TAG = "DownloadWorker";
    Context context;
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        download();
        return Result.success();
    }

    public void download() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://blackbooksuganda.com/api/quickcodes";

        //build the request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            s -> {
                try {
                    DataRepository dataRepository = new DataRepository((Application)context);
                    JSONArray codes = new JSONArray(s);//array with all the codes
                    //loop over json array with all the codes
                    for(int i=0;i<codes.length();i++){
                        JSONObject code = codes.getJSONObject(i);

                        int id = code.optInt("id");
                        String name = code.optString("name");
                        String airtelCode = code.optString("code");
                        String hni = code.optString("hni");
                        int section = code.optInt("section");
                        UssdAction ussdAction = new UssdAction(id, name, airtelCode, hni, section, 0);

                        //get the local object and get its weight,and set this objects weight
                        //to that of the local object so its not overriden
                        UssdActionWithSteps localAction = dataRepository.getUssdAction(String.valueOf(id));
                        if (localAction != null) {
                            ussdAction.setWeight(localAction.action.getWeight());
                        }

                        //TODO check if, it was deleted by checking is it has a flag of deleted set to true
                        JSONArray steps = code.getJSONArray("steps");
                        List<Step>stepList = new ArrayList<>();
                        for(int j=0;j<steps.length();j++){
                            JSONObject step = steps.getJSONObject(j);
                            int type = step.optInt("type");
                            int weight = step.optInt("weight");
                            String desc = step.optString("description");
                            String defaultValue = step.optString("defaultValue");
                            int ussd_action_id = step.optInt("ussd_action_id");
                            Step step1 = new Step(ussd_action_id, type, weight, desc, defaultValue);
                            stepList.add(step1);
                        }
                        //insert the code into the local database
                        dataRepository.insertAll(new UssdActionWithSteps(ussdAction,stepList));

                    }
//                    Toast.makeText(context, "Codes have updated", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            },
            (VolleyError volleyError) -> {
                Log.d(TAG, volleyError.getMessage());
            });

        // Add the request to the RequestQueue.
//        queue.add(stringRequest);

    }
}
