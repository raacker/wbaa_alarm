package com.raacker.wwbaalarm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private int hour, minute;

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv = (TextView)getActivity().findViewById(R.id.time);
        this.hour = hourOfDay;
        this.minute = minute;
        tv.setText(getResources().getString(R.string.alarm_time) + " " +
                String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(
                getContext(), this, hour, minute,
                DateFormat.is24HourFormat(getContext())
        );
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }
}
