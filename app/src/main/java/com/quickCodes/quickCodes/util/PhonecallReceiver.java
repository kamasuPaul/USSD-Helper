package com.quickCodes.quickCodes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PhonecallReceiver extends BroadcastReceiver {

    protected Context savedContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        savedContext = context;
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        //if the number number contains a * at the begining and # at the end its a ussd code,
            //save it
        if (number == null) return;
            if (number.startsWith("*")) {

                SharedPreferences.Editor editor =
                    context.getSharedPreferences(UssdDetector.AUTO_SAVED_CODES, Context.MODE_PRIVATE).edit();
                editor.putString("code", number);
                editor.commit();
            }
    }
}
