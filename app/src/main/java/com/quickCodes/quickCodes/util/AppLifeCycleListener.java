package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.content.Intent;

import com.quickCodes.quickCodes.MainActivity;
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
        //stop the screen overlay ,the 3 dots
        context.stopService(new Intent(context, ChatHeadService.class));

        //make the accessibility ussd detector stop since all actions dialed from
        //quick codes are already saved
        MainActivity.accessibilityServiceShouldRun = false;


    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void startOverlay(){
        if (Tools.showMeOverlay(context)) {
            //start the screen overlay, the 3 dots
            context.startService(new Intent(context, ChatHeadService.class));
        } else {
            context.stopService(new Intent(context, ChatHeadService.class));
        }

        MainActivity.accessibilityServiceShouldRun = true;

    }

}
