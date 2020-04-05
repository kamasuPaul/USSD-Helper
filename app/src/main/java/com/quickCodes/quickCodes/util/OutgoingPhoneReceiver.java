package com.quickCodes.quickCodes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.quickCodes.quickCodes.screenOverlays.PhoneCallsOverlayService;

public class OutgoingPhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number  = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Toast.makeText(context, "YO WHATSUP"+number, Toast.LENGTH_SHORT).show();
        initializeView(context);
    }
    public void initializeView(Context context) {
//        //Check if the application has draw over other apps permission or not?
//        //This permission is by default available for API<23. But for API > 23
//        //you have to ask for the permission in runtime.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
//
//            //If the draw over permission is not available open the settings screen
//            //to grant the permission.
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:" + context.getPackageName()));
//            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
//        } else {
            context.startService(new Intent(context, PhoneCallsOverlayService.class));

        Toast.makeText(context, "YO WHATSUP after service", Toast.LENGTH_SHORT).show();

//                finish();
//        }
    }
}
