package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.quickCodes.quickCodes.screenOverlays.ChatHeadService;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AppLifeCycleListener  implements  LifecycleObserver {


    private Context context;

    public AppLifeCycleListener(Context cxt) {
        this.context = cxt;
    }

    //register lifecylce callbacks
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void stopOverlay(){
        Toast.makeText(context, "RESUME", Toast.LENGTH_SHORT).show();
        //stop the screen overlay
        context.stopService(new Intent(context, ChatHeadService.class));


    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void startOverlay(){
        //start the screen overlay
        Toast.makeText(context, "STOP", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, ChatHeadService.class));




    }

}
