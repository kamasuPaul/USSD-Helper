package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.MODE_PRIVATE;
import static com.quickCodes.quickCodes.util.PermissionsActivity.ASK_ACCESSIBILITY;
import static com.quickCodes.quickCodes.util.PermissionsActivity.ASK_TIMES;

public class AskAccessibility extends Worker {
    Context context;

    public AskAccessibility(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        //reset number of times accessibility is asked
        SharedPreferences sharedPreferences = context.getSharedPreferences(ASK_ACCESSIBILITY, MODE_PRIVATE);

        sharedPreferences.edit().putInt(ASK_TIMES, 2).commit();
        Log.d("YAH", "renewing times");
        return null;
    }
}