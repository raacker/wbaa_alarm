package com.raacker.wwbaalarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver{
    public static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called");
        MediaPlayer mediaPlayer;
        boolean[] week = intent.getBooleanArrayExtra("weekday");

        Calendar cal = Calendar.getInstance();
        if(week[cal.get(Calendar.DAY_OF_WEEK)]){
            Log.d(TAG, "Alarm called");

            try{
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context.getResources().getString(R.string.url));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //mediaPlayer.setWakeMode(context, PowerManager.);
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch(IOException e) {
               // Toast.makeText(get, "Some problem caused while streaming", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        }
    }
}
