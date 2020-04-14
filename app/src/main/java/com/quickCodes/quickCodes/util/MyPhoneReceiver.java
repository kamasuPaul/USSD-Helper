package com.quickCodes.quickCodes.util;

import android.widget.Toast;

import java.util.Date;

public class MyPhoneReceiver extends PhonecallReceiver {
    @Override
    protected void onIncomingCallStarted(String number, Date start) {

    }

    @Override
    protected void onOutgoingCallStarted(String number, Date start) {
        Toast.makeText(savedContext, "the number is " + number + ".." + start.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallEnded(String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallEnded(String number, Date start, Date end) {
        Toast.makeText(savedContext, "outgoing number is " + number + ".." + start.toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onMissedCall(String number, Date start) {

    }
}
