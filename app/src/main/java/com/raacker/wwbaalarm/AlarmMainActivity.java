package com.raacker.wwbaalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class AlarmMainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final long INTERVAL_TIME = 24 * 60 * 60 * 1000;

    private TimePickerFragment timePickerFragment;
    private AlarmManager alarmManager;
    private ToggleButton monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_main);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        monday = (ToggleButton) findViewById(R.id.toggle_mon);
        tuesday = (ToggleButton) findViewById(R.id.toggle_tue);
        wednesday = (ToggleButton) findViewById(R.id.toggle_wed);
        thursday = (ToggleButton) findViewById(R.id.toggle_thu);
        friday = (ToggleButton) findViewById(R.id.toggle_fri);
        saturday = (ToggleButton) findViewById(R.id.toggle_sat);
        sunday = (ToggleButton) findViewById(R.id.toggle_sun);
        Button set_alarm = (Button) findViewById(R.id.set_alarm);
        Button set_time = (Button) findViewById(R.id.set_time);
        timePickerFragment = new TimePickerFragment();
        set_alarm.setOnClickListener(this);
        set_time.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_time:
                setAlarmTime();
                break;
            case R.id.set_alarm:
                registerAlarm(this);
                break;
        }
    }

    public void registerAlarm(Context context) {
        Log.e(TAG, "registerAlarm() called");

        boolean[] week = {false, sunday.isChecked(), monday.isChecked(),
                tuesday.isChecked(), wednesday.isChecked(), thursday.isChecked(),
                friday.isChecked(), saturday.isChecked()};

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("weekday", week);
        PendingIntent pendingIntent = getPendingIntent(intent);
        long alarmTime = getTime();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, INTERVAL_TIME, pendingIntent);
    }

    private PendingIntent getPendingIntent(Intent intent) {
        return  PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setAlarmTime() {
        timePickerFragment.show(getFragmentManager(), "show");
    }

    private long getTime(){
        long aTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePickerFragment.getHour());
        calendar.set(Calendar.MINUTE, timePickerFragment.getMinute());
        calendar.set(Calendar.SECOND, 10);
        calendar.set(Calendar.MILLISECOND, 0);

        long bTime = calendar.getTimeInMillis();

        long alarmTime = bTime;
        if(aTime > bTime) {
            alarmTime += INTERVAL_TIME;
        }
        return alarmTime;
    }

    private void saveAlarm() {
        SharedPreferences pref = getSharedPreferences("alarm_list", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //editor.putStringSet();
    }

    private void loadAlarm() {
        SharedPreferences pref = getSharedPreferences("alarm_list", Activity.MODE_PRIVATE);
        Map<String, ?> stringSet = pref.getAll();


    }
}
