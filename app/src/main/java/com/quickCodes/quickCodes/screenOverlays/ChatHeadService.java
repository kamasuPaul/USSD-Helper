package com.quickCodes.quickCodes.screenOverlays;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.util.PermissionsActivity;

import androidx.annotation.Nullable;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

public class ChatHeadService extends Service {
    private static final String CHATHEAD_PREFS = "chat_head_prefs";
    public static String TAG = "TOUCH";
    View chatHead;
    SharedPreferences sharedPreferences;
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
        sharedPreferences = getSharedPreferences(CHATHEAD_PREFS, MODE_PRIVATE);


        //inflate the chat head layout
        chatHead = LayoutInflater.from(this).inflate(R.layout.chat_head, null);
        //add the view to the window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT>=Build.VERSION_CODES.O
                ?TYPE_APPLICATION_OVERLAY:TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        //Specify the chat head position
        //Initially view will be added to top-left corner, if they are not saved positions yet in shared prefs
        //but will use the position where the user last moved it to.
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = sharedPreferences.getInt("x", 0);
        params.y = sharedPreferences.getInt("y", 300);

        //add view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHead, params);

        final ImageView chatHeadImage = chatHead.findViewById(R.id.chat_head_profile_iv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
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
                        return true;
                    case MotionEvent.ACTION_UP:
                        int diff_x = (int) (event.getRawX() - initialTouchX);
                        int diff_y = (int) (event.getRawY() - initialTouchY);
                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.transparent));


                        //check if the diff is less that 10 ,implying a click not scroll
                        if (diff_x < 10 && diff_y < 10) {
                            Intent intent = new Intent(ChatHeadService.this, PermissionsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //save this position in sharedprefs
                        sharedPreferences.edit().putInt("x", params.x)
                            .putInt("y", params.y).commit();

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return true;
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
