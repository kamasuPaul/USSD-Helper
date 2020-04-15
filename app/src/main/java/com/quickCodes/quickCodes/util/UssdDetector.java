package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.screenOverlays.PhoneCallsOverlayService;

public class UssdDetector extends AccessibilityService {
    public static final String AUTO_SAVED_CODES = "AUTO_SAVED_CODES";
    private static final String TAG = "ACCESSIBILITY";
    private static boolean pinbox = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
            if (event.getText().toString().equalsIgnoreCase("[OK]")) {
                //when they click okay, save the code in shared preferences
                SharedPreferences preferences =
                    this.getSharedPreferences(AUTO_SAVED_CODES, Context.MODE_PRIVATE);
                String code = preferences.getString("code", null);
                if (code != null) {
                    showSummary(UssdDetector.this, code);
                }

            }
        }


        //ignore dialogs with the word pin to protect privacy of user
        if (event.getClassName().equals("android.app.AlertDialog")) {
            if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "pin")) {
                // if it also contains the word enter ,implying a user is going to enter ping,
                //otherwise it might be just a menu option
                if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "enter")) {
                    Log.d(TAG, "PIN STEP SKIPPED");
                    pinbox = true;
                    return;//stop here dont record the text in the box, it will contain apin
                }
            } else if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "password")) {
                Log.d(TAG, "PASS STEP SKIPPED");
                pinbox = true;
                return;//stop here dont record the text in the edit text,it will contain a password
            } else {
                pinbox = false;//continue the current dialog is not apin box;
            }
        }
//        if the current box is apin or password box dont record its tex
        if (pinbox == true) {
            return;
        }

        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.EditText")) {
            AccessibilityNodeInfo source = event.getSource();
            AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);

            String nodeValue = String.valueOf(inputNode.getText());
            SharedPreferences preferences =
                this.getSharedPreferences(AUTO_SAVED_CODES, Context.MODE_PRIVATE);
            //append the value on if its not null an
                preferences.edit().putString("middleValue", nodeValue).commit();

        }
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
            if (event.getText().toString().equalsIgnoreCase("[Send]")) {
                //now add the middle value to code
                SharedPreferences preferences =
                    this.getSharedPreferences(AUTO_SAVED_CODES, Context.MODE_PRIVATE);
                String code = preferences.getString("code", null);
                String middleValue = preferences.getString("middleValue", null);
                preferences.edit().putString("code", code + "," + middleValue).commit();

            }

        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "ON service connected");
        super.onServiceConnected();
    }

    public void showSummary(Context context, String code) {
        Intent intent = new Intent(context, PhoneCallsOverlayService.class);
        intent.putExtra("code", code);
        context.startService(intent);

    }
}
