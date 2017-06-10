package com.raacker.wwbaalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AlarmMainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "AlarmMainActivity";
    public static final long INTERVAL_TIME = 24 * 60 * 60 * 1000;
    public static final String[] dayList = {"S", "M", "T", "W" , "T", "F", "S"};

    private TimePickerFragment timePickerFragment;
    private AlarmManager alarmManager;
    private ToggleButton monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private ArrayList<Alarm> alarms;
    private ArrayList<PendingIntent> pendingIntentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_main);

        loadAlarm();
        updateAlarmList();

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        monday = (ToggleButton) findViewById(R.id.toggle_mon);
        tuesday = (ToggleButton) findViewById(R.id.toggle_tue);
        wednesday = (ToggleButton) findViewById(R.id.toggle_wed);
        thursday = (ToggleButton) findViewById(R.id.toggle_thu);
        friday = (ToggleButton) findViewById(R.id.toggle_fri);
        saturday = (ToggleButton) findViewById(R.id.toggle_sat);
        sunday = (ToggleButton) findViewById(R.id.toggle_sun);
        Button setAlarm = (Button) findViewById(R.id.set_alarm);
        Button setTime = (Button) findViewById(R.id.set_time);
        Button playManually = (Button) findViewById(R.id.play_manually);
        timePickerFragment = new TimePickerFragment();
        setAlarm.setOnClickListener(this);
        setTime.setOnClickListener(this);
        playManually.setOnClickListener(this);
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getResources().getString(R.string.url));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        saveAlarm();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_time:
                setAlarmTime();
                break;
            case R.id.set_alarm:
                registerAlarm(this);
                updateAlarmList();
                break;
            case R.id.play_manually:
                playRadioManually();
                break;
        }
    }

    private void updateAlarmList() {
        int textSize = 20;
        int dp20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getApplicationContext().getResources().getDisplayMetrics());

        TableLayout table = (TableLayout) findViewById(R.id.alarm_list);
        table.removeAllViews();

        for(int i = 0; i < alarms.size(); i++) {
            Alarm currentAlarm = alarms.get(i);
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            Button turnOnButton = new Button(this);
            if(currentAlarm.isEnabled()) {
                turnOnButton.setText("enabled");
                turnOnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        turnOffAlarm();
                    }
                });
                //turnOnButton.setBackground(getDrawable(R.mipmap.alarm_enabled));
            } else {
                turnOnButton.setText("disabled");
                turnOnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        turnOnAlarm();
                    }
                });
               // turnOnButton.setBackground(getDrawable(R.mipmap.alarm_disabled));
            }
            turnOnButton.setWidth(5);
            turnOnButton.setHeight(5);

            TextView time = new TextView(this);
            time.setText(convertTime(currentAlarm.getAlarmTime()));
            time.setTextSize(textSize);

            TextView days = new TextView(this);
            days.setTextSize(textSize);
            boolean[] daysBoolean = currentAlarm.getDayList();
            StringBuilder dayBuilder = new StringBuilder("");
            for(int j = 1; j < daysBoolean.length; j++) {
                if(daysBoolean[j]) {
                    dayBuilder.append(dayList[j-1]);
                    dayBuilder.append(" ");
                }
            }
            days.setText(dayBuilder.toString());

            //row.addView(turnOnButton);
            row.addView(time);
            row.addView(days);
            table.addView(row);
        }
    }

    public void registerAlarm(Context context) {
        Log.i(TAG, "registerAlarm() called");

        boolean[] week = {false, sunday.isChecked(), monday.isChecked(),
                tuesday.isChecked(), wednesday.isChecked(), thursday.isChecked(),
                friday.isChecked(), saturday.isChecked()};
        long alarmTime = getTime();
        Alarm newAlarm = new Alarm(alarmTime, true, week);
        this.alarms.add(newAlarm);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("weekday", newAlarm.getDayList());
        PendingIntent pendingIntent = getPendingIntent(intent);
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                newAlarm.getAlarmTime(), INTERVAL_TIME, pendingIntent);

        pendingIntentArray.add(pendingIntent);
    }

    private PendingIntent getPendingIntent(Intent intent) {
        return  PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void turnOnAlarm(Alarm alarm) {

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("weekday", alarm.getDayList());
        PendingIntent pendingIntent = getPendingIntent(intent);
        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                alarm.getAlarmTime(), INTERVAL_TIME, pendingIntent);

        pendingIntentArray.add(pendingIntent);
    }

    private void turnOffAlarm(PendingIntent pendingIntent) {
        alarmManager.cancel(pendingIntent);
        pendingIntentArray.remove(pendingIntent);
    }

    private void setAlarmTime() {
        timePickerFragment.show(getFragmentManager(), "show");
    }

    private long getTime(){
        long aTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePickerFragment.getHour());
        calendar.set(Calendar.MINUTE, timePickerFragment.getMinute());
        calendar.set(Calendar.SECOND, 5);
        calendar.set(Calendar.MILLISECOND, 0);

        long bTime = calendar.getTimeInMillis();

        long alarmTime = bTime;
        if(aTime > bTime) {
            alarmTime += INTERVAL_TIME;
        }
        return alarmTime;
    }

    private String convertTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String AM_PM;
        if(((int)calendar.get(Calendar.AM_PM)) == 0) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        return AM_PM + " " +
                String.format(Locale.ENGLISH, "%d%d", calendar.get(Calendar.HOUR)/10,
                        calendar.get(Calendar.HOUR) % 10) + " : " +
                String.format(Locale.ENGLISH, "%d%d", calendar.get(Calendar.MINUTE)/10,
                        calendar.get(Calendar.MINUTE) % 10);
    }

    private void playRadioManually() {
        try{
            mediaPlayer.prepare();
            if(isPlaying) {
                isPlaying = false;
                mediaPlayer.pause();
            } else {
                isPlaying = true;
                mediaPlayer.start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAlarm() {
        SharedPreferences pref = getSharedPreferences("alarm_list", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("alarm_count", alarms.size());
        for(int i = 0; i < alarms.size(); i++) {
            editor.putString("alarm" + i, alarms.get(i).toString());
        }
        editor.apply();
    }

    private void loadAlarm() {
        SharedPreferences pref = getSharedPreferences("alarm_list", Activity.MODE_PRIVATE);
        int size = pref.getInt("alarm_count", 0);
        this.alarms = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            String alarm = pref.getString("alarm" + i, null);
            Alarm newAlarm = new Alarm(alarm);
            this.alarms.add(newAlarm);
        }
    }
}
