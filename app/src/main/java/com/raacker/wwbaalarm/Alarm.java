package com.raacker.wwbaalarm;

import java.util.StringTokenizer;

public class Alarm {
    public static final int WEEK_LENGTH = 8;
    private long alarmTime;
    private boolean isEnabled;
    private boolean[] dayList;

    public Alarm(long alarmTime, boolean isEnabled, boolean[] dayList) {
        this.alarmTime = alarmTime;
        this.isEnabled = isEnabled;
        this.dayList = dayList;
    }

    public Alarm(String alarmData) {
        StringTokenizer tok = new StringTokenizer(alarmData, ",");
        this.alarmTime = Long.valueOf(tok.nextToken());
        this.isEnabled = Boolean.valueOf(tok.nextToken());
        boolean[] dList = new boolean[WEEK_LENGTH];
        int i = 0;
        while(tok.hasMoreTokens()) {
            dList[i] = Boolean.valueOf(tok.nextToken());
            i++;
        }
        this.dayList = dList;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean[] getDayList() {
        return dayList;
    }

    public void setDayList(boolean[] dayList) {
        this.dayList = dayList;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(alarmTime + ",");
        buf.append(isEnabled);
        buf.append(",");
        for(int i = 0; i < WEEK_LENGTH; i++) {
            buf.append(dayList[i]);
            buf.append(",");
        }
        String result = buf.toString();
        return result.substring(0, result.length()-1);
    }
}
