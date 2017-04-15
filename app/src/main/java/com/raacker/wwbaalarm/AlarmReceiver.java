package com.raacker.wwbaalarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmReceiver";
    private MediaPlayer mediaPlayer;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    //@Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called");
        boolean[] week = intent.getBooleanArrayExtra("weekday");
        powerManager = ((PowerManager)context.getSystemService(Context.POWER_SERVICE));
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "alarmStart");

        wakeLock.acquire();
        Calendar cal = Calendar.getInstance();
        if(week[cal.get(Calendar.DAY_OF_WEEK)]){
            Log.d(TAG, "Alarm called");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    PlaySync play = new PlaySync();
                    play.execute();
                }
            }, 10000);
        }
    }

    private class PlaySync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource("https://purdue.streamguys1.com/wbaa-news");
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                if(wakeLock.isHeld())
                    wakeLock.release();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
