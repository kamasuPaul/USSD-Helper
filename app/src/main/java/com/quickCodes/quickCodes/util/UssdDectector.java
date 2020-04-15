package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.screenOverlays.PhoneCallsOverlayService;

public class UssdDectector extends AccessibilityService {
    private static final String TAG = "ACCESSIBILITY";
    public static final String BUTTON_VIEW_ID = "android:id/button1";
    private static final String MESSAGE_VIEW_ID = "android:id/message";
    public static int dialtimes = 1;
    private static boolean pinbox = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
            if (event.getText().toString().equalsIgnoreCase("[OK]")) {
                //when they click okay, save the code in shared preferences
                SharedPreferences preferences =
                    this.getSharedPreferences("AUTOSAVED_CODES", Context.MODE_PRIVATE);
                String code = preferences.getString("code", null);
                if (code != null) {
                    showSummary(UssdDectector.this, code);
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
//        //detect on only popups
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.EditText")) {
            AccessibilityNodeInfo source = event.getSource();
            AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);

            String nodeValue = String.valueOf(inputNode.getText());
            Log.d(TAG, nodeValue);
            SharedPreferences preferences =
                this.getSharedPreferences("AUTOSAVED_CODES", Context.MODE_PRIVATE);
            String code = preferences.getString("code", null);
            if (code != null) {
            }
            //append the value
            //append the value on if its not null and if its value is not equal to the previous value
            if (nodeValue.contains("null")) {//just append a comma is value is null
//                if(code.lastIndexOf())
                preferences.edit().putString("middleValue", "null").commit();

            } else {//append the value
                preferences.edit().putString("middleValue", nodeValue).commit();

            }


        }
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
            Log.d(TAG, event.toString());
            if (event.getText().toString().equalsIgnoreCase("[Send]")) {
                //now add the middle value to code
                SharedPreferences preferences =
                    this.getSharedPreferences("AUTOSAVED_CODES", Context.MODE_PRIVATE);
                String code = preferences.getString("code", null);
                String middleValue = preferences.getString("middleValue", null);


                preferences.edit().putString("code", code + "," + middleValue).commit();


            }

        }

//        if (event.getClassName().equals("android.app.AlertDialog")) {
//            AccessibilityNodeInfo source = event.getSource();
//            if(source!=null) {
//                Toast.makeText(this, "source not null", Toast.LENGTH_SHORT).show();
//                AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
//                if (inputNode != null) {
//                    Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(this, inputNode.getText().toString(), Toast.LENGTH_SHORT).show();
//                    Bundle arguments = new Bundle();
//                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "1");
//                    inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,arguments);
//                    boolean b = inputNode.performAction(AccessibilityNodeInfo.ACTION_SELECT);
//                    Log.d(TAG, String.valueOf(b));
//                    boolean b1 = inputNode.performAction(AccessibilityNodeInfo.ACTION_COPY);
//
//                    Log.d(TAG, String.valueOf(b1));
//
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        ClipboardManager systemService = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                    ;
////                    }
////                    String text = event.getText().toString();
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////                        Log.d(TAG,inputNode.getExtras().);
////                    }
//
//                    //CLICK SEND BUTTON
//                    if(dialtimes<2) {
//                        dialtimes = dialtimes+1;
//                        List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("ok");
//                        for (AccessibilityNodeInfo node : list) {
//                            Toast.makeText(this, "okay pressed", Toast.LENGTH_SHORT).show();
////                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        }
//                    }
//
//                } else {
//                    Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
//                }
//            }else {
//                Toast.makeText(this, "source null", Toast.LENGTH_SHORT).show();
//        }
//
////            //        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
////            int eventType = event.getEventType();
////            Log.d(TAG,"TEXT CHANGED");
////            Log.d(TAG, "dfdfd"+ String.valueOf(eventType));
////
//////                Toast.makeText(this, event.getEventType(), Toast.LENGTH_SHORT).show();
//            }
//
//            // write a broad cast receiver and call sendbroadcast() from here, if you want to parse the message for balance, date
//        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "ON service connected", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
//        Log.d(TAG, "onServiceConnected");
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.flags = AccessibilityServiceInfo.DEFAULT;
//        info.packageNames = new String[]{"com.android.phone"};
//        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        setServiceInfo(info);
    }

    public void showSummary(Context context, String code) {
        Intent intent = new Intent(context, PhoneCallsOverlayService.class);
        intent.putExtra("code", code);
        context.startService(intent);

    }
}
