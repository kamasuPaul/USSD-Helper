package com.quickCodes.quickCodes.util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.quickCodes.quickCodes.modals.CustomAction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadWorker extends Worker {
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

    private void download() {
        //reftrofit
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<CustomAction>> call = apiInterface.getAllCustomActions();
        call.enqueue(new Callback<List<CustomAction>>() {
            @Override
            public void onResponse(Call<List<CustomAction>> call, Response<List<CustomAction>> response) {
                DataRepository r = new DataRepository((Application) context);
                for (CustomAction w: response.body()
                ) {
                    r.insert(w);
                }
            }

            @Override
            public void onFailure(Call<List<CustomAction>> call, Throwable t) {

            }
        });
    }
}
