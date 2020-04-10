package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.content.Intent;

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
        //stop the screen overlay
        context.stopService(new Intent(context, ChatHeadService.class));


    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void startOverlay(){
        //start the screen overlay
        context.startService(new Intent(context, ChatHeadService.class));




    }

}
