package com.quickCodes.quickCodes.screenOverlays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quickCodes.quickCodes.util.Tools;

public class StartOverlayOnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "StartOverlayOnBootRcvr";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "service started", Toast.LENGTH_LONG).show();
//        Log.d(TAG,"boot completed");
        try {

            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                //show the dots if not disabled by user in settings
                if (Tools.showMeOverlay(context)) {
                    context.startService(new Intent(context, ChatHeadService.class));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }
}
