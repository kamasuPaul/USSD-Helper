package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class UssdDectector extends AccessibilityService {
    private static final String TAG = "ACCESSIBILITY";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
        String text = event.getText().toString();
//        Log.d(TAG,text);
//        performGlobalAction()
        //lauch overlay
        if (event.getClassName().equals("android.app.AlertDialog")) {
            performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(TAG, text);
//            Intent intent = new Intent("com.times.ussd.action.REFRESH");
//            intent.putExtra("message", text);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            // write a broad cast receiver and call sendbroadcast() from here, if you want to parse the message for balance, date
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "ON service connected", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}
