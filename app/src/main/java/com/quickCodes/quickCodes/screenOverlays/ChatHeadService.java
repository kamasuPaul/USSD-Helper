package com.quickCodes.quickCodes.screenOverlays;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;

import androidx.annotation.Nullable;

public class ChatHeadService extends Service {
    View chatHead;
    boolean app_runing_in_foreground = false;

    private WindowManager windowManager;
    public ChatHeadService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
//        //check if the entire application is in background or not
//        Log.d("SERVICE ","INSIDE SERVICE");
//
//        ActivityManager activityManager =
//            (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
//            activityManager.getRunningAppProcesses();
//        String packageName = getApplicationContext().getPackageName();
//        for(ActivityManager.RunningAppProcessInfo appProcessInfo: runningAppProcesses){
//            Log.d("APP ",appProcessInfo.processName);
//            Log.d("APP ",packageName);
//
//            if(appProcessInfo.processName.equalsIgnoreCase(packageName)){
//                //app in foreground stop service
//                Log.d("APP RUNNING",appProcessInfo.processName);
//                stopSelf();
//
//            }
//        }
        //inflate the chat head layout
        chatHead = LayoutInflater.from(this).inflate(R.layout.chat_head, null);
        //add the view to the window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        //Specify the chat head position
//Initially view will be added to top-left corner
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 400;

        //add view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHead, params);


        //Set the close button.
        ImageView closeButton = (ImageView) chatHead.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                stopSelf();
            }
        });

        //Drag and move chat head using user's touch action.
        final ImageView chatHeadImage = (ImageView) chatHead.findViewById(R.id.chat_head_profile_iv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatHeadImage.setBackgroundColor(getResources().getColor(R.color.grey_100));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.transparent));
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            Intent intent = new Intent(ChatHeadService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            //close the service and remove the chat heads
                            stopSelf();
                        }
                        lastAction = event.getAction();
                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.transparent));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //set background of chat head
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(chatHead, params);
                        lastAction = event.getAction();
                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.grey_100));
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
