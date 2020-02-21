package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.widget.Toast;

import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionApi;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

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

    public void download() {
        //reftrofit
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<UssdActionApi>> call = apiInterface.getAllCustomActions();
        call.enqueue(new Callback<List<UssdActionApi>>() {
            @Override
            public void onResponse(Call<List<UssdActionApi>> call, Response<List<UssdActionApi>> response) {
//                DataRepository r = new DataRepository((Application) context);
//                Log.d("API",response.toString());
//                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
//                for (UssdActionApi w: response.body()
//                ) {
//                    r.insertAll(convert(w));
//                    Toast.makeText(getApplicationContext(), w.toString(), Toast.LENGTH_SHORT).show();
//
//                }
            }

            @Override
            public void onFailure(Call<List<UssdActionApi>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }
    public static UssdActionWithSteps convert(UssdActionApi data){
        UssdAction s = new UssdAction(data.actionId,data.getName(),data.getAirtelCode(),
            data.getMtnCode(),data.getAfricellCode(),data.section);
        return new UssdActionWithSteps(s,data.getSteps());
    }
}
