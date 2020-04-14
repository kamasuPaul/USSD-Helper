package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class UssdDectector extends AccessibilityService {
    private static final String TAG = "ACCESSIBILITY";
    public static final String BUTTON_VIEW_ID = "android:id/button1";
    private static final String MESSAGE_VIEW_ID = "android:id/message";
    public static int dialtimes = 1;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        //detect on only popups
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.EditText")) {
            AccessibilityNodeInfo source = event.getSource();
            AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            Log.d(TAG, String.valueOf(inputNode.getText()));

        }
        Log.d(TAG, event.toString());
        if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
            Log.d(TAG, String.valueOf(event.getText()));
            Log.d(TAG, String.valueOf("ok"));

            if (event.getText().toString().equalsIgnoreCase("[OK]")) {
                Log.d(TAG, String.valueOf("KD"));
                Toast.makeText(this, "okay pressed", Toast.LENGTH_SHORT).show();

            }
            Log.d(TAG, String.valueOf("KDFJD"));
            List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByText("ok");
            for (AccessibilityNodeInfo node : list) {
                Log.d(TAG, String.valueOf("pressed"));
                Toast.makeText(this, "okay pressed", Toast.LENGTH_SHORT).show();
//                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        if (event.getClassName().equals("android.app.AlertDialog")) {
            AccessibilityNodeInfo source = event.getSource();
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
        }
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
}
