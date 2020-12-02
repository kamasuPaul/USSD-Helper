package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.adapters.AdapterMenuItems;
import com.quickCodes.quickCodes.screenOverlays.PhoneCallsOverlayService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class UssdDetector extends AccessibilityService {
    public static final String AUTO_SAVED_CODES = "AUTO_SAVED_CODES";
    public static final String STEP_TEL = "step_tel";
    private static final String TAG = "UssdDetector";
    private static boolean pinbox = false;
    private static Map<Integer, String> kamasuMenu;
    List<String> parts;
    View chatHead;
    private AccessibilityNodeInfo textBoxNode;
    private AccessibilityNodeInfo sendButton;
    private WindowManager windowManager;
    private AdapterMenuItems adapterMenuItems;
    private CountDownTimer countDownTimer;

    public static void showSummary(Context context) {
        Intent intent = new Intent(context, PhoneCallsOverlayService.class);
        context.startService(intent);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parts = Tools.parts;

        //inflate the layout_no_item
        chatHead = LayoutInflater.from(this).inflate(R.layout.show_menu_root, null);
        chatHead.setVisibility(View.GONE);//hide it by default
//        chatHead.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(UssdDetector.this, "tocuched", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        //specify the window stuff
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? TYPE_APPLICATION_OVERLAY : TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        );
        //Specify the overlay position
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 30;
        params.y = 300;

        RecyclerView recyclerView = chatHead.findViewById(R.id.menu_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapterMenuItems = new AdapterMenuItems(this);
        recyclerView.setAdapter(adapterMenuItems);
        adapterMenuItems.setOnItemClickListener(new AdapterMenuItems.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Integer obj, int position) {
                fill(String.valueOf(obj));
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                createCountDownTimer();
//                Toast.makeText(UssdDetector.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //close button
        ImageView closeButton = (ImageView) chatHead.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(v -> {
            //close the service and remove the chat head from the window
            chatHead.setVisibility(View.GONE);
        });
        try {
            //add view to the window
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(chatHead, params);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showMenu(List<Integer> list) {
        if (list.size() > 0) {
            chatHead.setVisibility(View.VISIBLE);
            adapterMenuItems.setUssdActions(list);
            //close the automatically after one minute
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            createCountDownTimer();


        } else {
            chatHead.setVisibility(View.GONE);
        }
    }

    public void createCountDownTimer() {
        countDownTimer = new CountDownTimer(40000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                chatHead.setVisibility(View.GONE);
            }
        };
        countDownTimer.start();
    }

    public void fill(String key) {
        Log.d(TAG, "" + key);
        Log.d(TAG, String.valueOf(key));
        if (textBoxNode != null) {
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, key);
            textBoxNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
            if (sendButton != null)
                sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //catch all errors,now will fix them after knowing exact cause
        try {

//            if(!event.getPackageName().equals("com.android.phone")&&(event.getEventType() != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)){
//                //close the  chat head
//                Log.d(TAG,"EVENT OFF");
//                if(chatHead!= null){
//                    Log.d(TAG,"EVENT MILL");
//
//                    chatHead.setVisibility(View.GONE);
//                }
//            }

            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                String typed = event.getText().toString().replace("[", "").replace("]", "").trim();
                //if starts with a * and ends with a # its a ussd code save it in shared preferences
                //other numbers will be concated on later
                if (typed.startsWith("*") && typed.endsWith("#")) {
                    SharedPreferences.Editor editor =
                        getSharedPreferences(UssdDetector.AUTO_SAVED_CODES, Context.MODE_PRIVATE).edit();
                    editor.putString("code", typed.replace("%23", "")).commit();
                }
            }

            SharedPreferences preferences =
                this.getSharedPreferences(AUTO_SAVED_CODES, Context.MODE_PRIVATE);

            if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
                if (event.getText().toString().equalsIgnoreCase("[OK]")) {
                    //when they click okay, save the code in shared preferences

                    String code = preferences.getString("code", null);
                    if (code != null && !code.equals("")) {
                        showSummary(UssdDetector.this);
                    }

                }
            }
            //ignore dialogs with the word pin to protect privacy of user
            if (event.getClassName().equals("android.app.AlertDialog")) {
                if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "pin")) {
                    // if it also contains the word enter ,implying a user is going to enter pin,
                    //otherwise it might be just a menu option
                    if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "enter")) {
//                        Log.d(TAG, "PIN STEP SKIPPED");
                        pinbox = true;
                        return;//stop here dont record the text in the box, it will contain apin
                    }
                } else if (AdapterDialer.containsIgnoreCase(event.getText().toString(), "password")) {
//                    Log.d(TAG, "PASS STEP SKIPPED");
                    pinbox = true;
                    return;//stop here dont record the text in the edit text,it will contain a password
                } else {
                    pinbox = false;//continue the current dialog is not apin box;
                }
                //build the menu
                kamasuMenu = kamasuUssdMenuRebuilder(event.getText().toString());
                List<Integer> ilist = new ArrayList<>(kamasuMenu.keySet());
                Collections.sort(ilist);
//                for (int key:ilist
//                     ) {
//                    Log.d(TAG,""+key);
//                }
                showMenu(ilist);
            }
//        if the current box is apin or password box dont record its text
            if (pinbox == true) {
                return;
            }
            //detect fields for entering amount and mobile number
            //ignore dialogs with the word pin to protect privacy of user
            if (event.getClassName().equals("android.app.AlertDialog")) {
                // first get the node
                AccessibilityNodeInfo source = event.getSource();
                AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                textBoxNode = inputNode;
//                Rect rect = new Rect();
//                inputNode.getBoundsInScreen(rect);
//                Log.d(TAG,rect.toString());
                //try to access the send button
                List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByText("Send");
                if (nodes.size() > 0) {
                    sendButton = nodes.get(nodes.size() - 1);
                }

                // try to replay a ussd code
                if (Tools.parts != null && Tools.parts.size() > 0) {
                    String key = Tools.parts.get(0);
                    Tools.parts.remove(0);
                    fill(key);
                }

                if (AdapterDialer.containsIgnoreCase(event.getText().toString().toLowerCase(), "Enter Mobile Number")) {
//                Toast.makeText(this, "mobile number", Toast.LENGTH_SHORT).show();
                    preferences.edit().putString(STEP_TEL, preferences.getString(STEP_TEL, "") + ",T").commit();

                } else if (AdapterDialer.containsIgnoreCase(event.getText().toString().toLowerCase(), "Enter Amount")) {
//                Toast.makeText(this, "amount", Toast.LENGTH_SHORT).show();
//                preferences.edit().putInt(STEP_TEXT, preferences.getInt(STEP_TEXT, 0) + 1).commit();
                    preferences.edit().putString(STEP_TEL, preferences.getString(STEP_TEL, "") + ",A").commit();


                }
                //build the menu
                kamasuMenu = kamasuUssdMenuRebuilder(event.getText().toString());
            }

            if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.EditText")) {
                AccessibilityNodeInfo source = event.getSource();
                AccessibilityNodeInfo inputNode = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                textBoxNode = inputNode;

                String nodeValue = String.valueOf(inputNode.getText());
                //append the value on if its not null an
                preferences.edit().putString("middleValue", nodeValue).commit();

            }
            if (event.getPackageName().equals("com.android.phone") && event.getClassName().equals("android.widget.Button")) {
                if (event.getText().toString().equalsIgnoreCase("[Send]")) {
                    //now add the middle value to code
                    String code = preferences.getString("code", null);
                    String middleValue = preferences.getString("middleValue", null);
                    String previousMenuItem = preferences.getString("menuItem", "");

                    String menuItem = null;
                    if (TextUtils.isDigitsOnly(middleValue)) {//mathes [0-9]
                        menuItem = kamasuMenu.get(Integer.valueOf(middleValue));//get selected menuitem
                    }
                    //when the user chooses * or 0 which means back delete one item from menu and code
                    if (middleValue.matches("[*0]")) {//mathces 0 and *
                        String d = preferences.getString("code", null);

//                    Toast.makeText(this, "* detected code is" + d, Toast.LENGTH_SHORT).show();
                        if (code.lastIndexOf(",") != -1) {
                            preferences.edit()
                                .putString("code", code.substring(0, code.lastIndexOf(",")))
                                .commit();
                            String d1 = preferences.getString("code", null);

//                        Toast.makeText(this, "* detected code is" + d1, Toast.LENGTH_SHORT).show();
                        }
                    }
//
//                Log.d(TAG, "middle value:" + middleValue);
//                Log.d(TAG, "kamasu:" + menuItem);
//                Log.d(TAG, "previous menu:" + previousMenuItem);
                    //if they select something that is in the menu , add it to selected menu and the code
                    if (menuItem != null) {
                        preferences.edit().putString("code", code + "," + middleValue).commit();
                        preferences.edit().
                            putString("menuItem", previousMenuItem + ">" + menuItem).commit();
                    }

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error");
        }

    }

    private Map<Integer, String> kamasuUssdMenuRebuilder(String menucontent) {
        Map<Integer, String> menuItems = new HashMap<>();
        if (menucontent != null) {
            String s = menucontent.replaceAll("\n", ",")
                .substring(1, menucontent.length() - 1);//remove []
            String[] valuePairs = s.split(",");//split into pieces
            for (String p : valuePairs
            ) {
                String s1 = p.trim().replaceFirst("[\\s.]", ":");
                String[] s2 = s1.split(":");
                if (s2.length == 2) {
                    if (s2[0].trim().matches("n") || TextUtils.isDigitsOnly(s2[0].trim())) {
                        try {
                            menuItems.put(Integer.parseInt(s2[0].trim()), s2[1].trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        } else {
            Log.d(TAG, "null");
        }
        return menuItems;
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            if (chatHead != null) windowManager.removeView(chatHead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
