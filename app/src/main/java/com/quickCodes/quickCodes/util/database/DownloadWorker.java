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
import com.quickCodes.quickCodes.util.Tools;

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
        String url1 = "https://blackbooksuganda.com/api/quickcodes";
        String url = "https://quickcodes.kamasupaul.com/api/network/" + Tools.getMcc(context) + "/ussdcodes";

        //build the request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            s -> {
                try {
                    DataRepository dataRepository = new DataRepository((Application)context);
                    JSONArray networks = new JSONArray(s);
                    for (int i = 0; i < networks.length(); i++) {
                        Log.d(TAG, "size" + networks.length());
                        JSONObject network = networks.getJSONObject(i);
                        String hni = network.optString("hni", "");
                        JSONArray ussd_actions = network.optJSONArray("ussd_actions");
                        Log.d(TAG, "size_actions" + ussd_actions.length());

                        for (int j = 0; j < ussd_actions.length(); j++) {

                            JSONObject code = ussd_actions.getJSONObject(j);

                            int id = code.optInt("id");
                            String name = code.optString("name");
                            String airtelCode = code.optString("code");
                            int section = code.optInt("section");
                            int weight = code.optInt("weight");
                            UssdAction ussdAction = new UssdAction(id, name, airtelCode, hni, section, weight);

                            //get the local object and get its weight,and set this objects weight
                            //to that of the local object so its not overriden
                            UssdActionWithSteps localAction = dataRepository.getUssdAction(String.valueOf(id));
                            if (localAction != null) {
                                ussdAction.setWeight(localAction.action.getWeight());
                            }

                            //TODO check if, it was deleted by checking is it has a flag of deleted set to true
                            JSONArray steps = code.getJSONArray("steps");
                            List<Step> stepList = new ArrayList<>();
                            for (int n = 0; n < steps.length(); n++) {
                                JSONObject step = steps.getJSONObject(n);
                                int type = step.optInt("type");
                                int step_weight = step.optInt("weight");
                                String desc = step.optString("description");
                                String defaultValue = step.optString("defaultValue");
                                int ussd_action_id = step.optInt("ussd_action_id");
                                Step step1 = new Step(ussd_action_id, type, step_weight, desc, defaultValue);
                                stepList.add(step1);
                            }
                            Log.d(TAG, ussdAction.toString());

                            //insert the code into the local database
                            dataRepository.insertAll(new UssdActionWithSteps(ussdAction, stepList));

                        }
                    }

                } catch (JSONException e) {
                    Log.d(TAG, "error" + e.getMessage());
                    e.printStackTrace();
                }

            },
            (VolleyError volleyError) -> {
                //this line was causing a crash
//                Log.d(TAG, volleyError.getMessage());
            });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
