package com.quickCodes.quickCodes.screenOverlays;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;

import androidx.annotation.Nullable;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

public class ChatHeadService extends Service {
    public static String TAG = "TOUCH";
    View chatHead;
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
//Initially view will be added to top-left corner
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 300;

        //add view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHead, params);


//        //Set the close button.
//        ImageView closeButton = (ImageView) chatHead.findViewById(R.id.close_btn);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //close the service and remove the chat head from the window
//                stopSelf();
//            }
//        });
        final ImageView chatHeadImage = chatHead.findViewById(R.id.chat_head_profile_iv);

        chatHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatHeadService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);

            }
        });

        //Drag and move chat head using user's touch action.
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"touch event");
//                Toast.makeText(ChatHeadService.this, "dont touch me", Toast.LENGTH_SHORT).show();
//                chatHeadImage.setBackgroundColor(getResources().getColor(R.color.grey_100));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG,"touch DOWN");


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
//                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        chatHeadImage.setBackgroundColor(getResources().getColor(R.color.transparent));
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        Log.d(TAG,"touch UP");

//                        Toast.makeText(ChatHeadService.this, "dont touch up", Toast.LENGTH_SHORT).show();

                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            Log.d(TAG,"touch EQUAL");

//                            Toast.makeText(ChatHeadService.this, "dont touch down", Toast.LENGTH_SHORT).show();

                            //Open the chat conversation click.
//                            Intent intent = new Intent(ChatHeadService.this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);

                            //close the service and remove the chat heads
//                            stopSelf();
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG,"touch MOVE");
                        //set background of chat head
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(chatHead, params);
                        lastAction = event.getAction();
                        return true;
                    default:
                        Log.d(TAG, String.valueOf(event.getAction()));
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
