package com.quickCodes.quickCodes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.quickCodes.quickCodes.screenOverlays.PhoneCallsOverlayService;

public class OutgoingPhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number  = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        //listen to phone call states
        TelephonyManager telephonyManager =
            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener(){
            private  int previousState;
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:
                        previousState = state;
//                        Toast.makeText(context, phoneNumber+"ringing",
//                            Toast.LENGTH_SHORT).show();
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        previousState = state;
                        Toast.makeText(context, phoneNumber+"dialing,active or on hold",
                            Toast.LENGTH_SHORT).show();
                    case TelephonyManager.CALL_STATE_IDLE:
                        Toast.makeText(context, phoneNumber+"idle",
                            Toast.LENGTH_SHORT).show();
                        if(previousState==TelephonyManager.CALL_STATE_OFFHOOK){
                            previousState = state;
                            Toast.makeText(context, "CALL ENDED", Toast.LENGTH_SHORT).show();
                            //previous call which is ended
                        }
                        if(previousState==TelephonyManager.CALL_STATE_RINGING){
                            previousState = state;
                            //rejected or missed call
                            Toast.makeText(context, "CALL MBU MISSED", Toast.LENGTH_SHORT).show();

                        }

                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
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

//        Toast.makeText(context, "YO WHATSUP after service", Toast.LENGTH_SHORT).show();

//                finish();
//        }
    }
}
