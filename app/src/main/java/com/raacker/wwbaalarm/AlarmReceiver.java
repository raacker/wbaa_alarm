package com.raacker.wwbaalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called");

        boolean[] week = intent.getBooleanArrayExtra("weekday");

        Calendar cal = Calendar.getInstance();
        if(week[cal.get(Calendar.DAY_OF_WEEK)]){
            //URL 재생
            Log.d(TAG, "Alarm called");
        }
    }
}
