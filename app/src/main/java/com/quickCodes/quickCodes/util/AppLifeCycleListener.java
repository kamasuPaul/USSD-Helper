package com.quickCodes.quickCodes.util;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.screenOverlays.ChatHeadService;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AppLifeCycleListener  implements  LifecycleObserver {


    private Context context;
    private boolean sharedPreferences;


    public AppLifeCycleListener(Context cxt) {
        this.context = cxt;

    }

    //register lifecylce callbacks
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void stopOverlay(){
        //stop the screen overlay
        context.stopService(new Intent(context, ChatHeadService.class));

        //make the accessibility ussd detector stop since all actions dialed from
        //quick codes are already saved
        MainActivity.accessibilityServiceShouldRun = false;


    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void startOverlay(){
        if (showMeOverlay(context)) {
            //start the screen overlay
            context.startService(new Intent(context, ChatHeadService.class));
        } else {
            context.stopService(new Intent(context, ChatHeadService.class));
        }

        MainActivity.accessibilityServiceShouldRun = true;

    }

    private boolean showMeOverlay(Context context) {
        boolean b = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getResources().getString(R.string.quick_access_dots_pref), true);
        return b;
    }

}
