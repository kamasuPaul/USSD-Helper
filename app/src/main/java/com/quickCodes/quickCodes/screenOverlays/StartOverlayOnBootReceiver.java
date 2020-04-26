package com.quickCodes.quickCodes.screenOverlays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartOverlayOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "service started", Toast.LENGTH_SHORT).show();

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            //show overlay
            Toast.makeText(context, "service started", Toast.LENGTH_SHORT).show();
//            context.startService(new Intent(context,ChatHeadService.class));
        }
    }
}
