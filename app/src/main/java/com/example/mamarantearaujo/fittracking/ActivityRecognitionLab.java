package com.example.mamarantearaujo.fittracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mamarantearaujo.fittracking.DataBase.DbHelper;
import com.example.mamarantearaujo.fittracking.DataBase.DbSchema;
import com.google.android.gms.location.DetectedActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mateus on 3/15/2016.
 */
public class ActivityRecognitionLab {

    private final Context mContext;
    private static Map<Integer, ActivityIds> mActivityIdMap = null;

    ActivityRecognitionLab(Context context) {
        mContext = context;

        if(mActivityIdMap == null) {
            HashMap<Integer, ActivityIds> map = new HashMap<>();
            map.put(DetectedActivity.STILL, new ActivityIds(R.drawable.still, R.string.still, R.string.still));
            map.put(DetectedActivity.WALKING, new ActivityIds(R.drawable.walking, R.string.walking, R.string.walked));
            map.put(DetectedActivity.RUNNING, new ActivityIds(R.drawable.running, R.string.running, R.string.run));
            map.put(DetectedActivity.IN_VEHICLE, new ActivityIds(R.drawable.in_vehicle, R.string.in_vehicle, R.string.in_vehicle));
            mActivityIdMap = map;
        }
    }

    public ActivityIds getActivityIds(int activity) {
        return mActivityIdMap.get(activity);
    }

    public String durationToString(long time) {
        String str = new String(" ");

        time = time / 1000;//milliseconds to seconds
        Integer hours = (int) (time / 3600);
        Integer minutes = (int) ((time % 3600) / 60);
        Integer seconds = (int) time % 60;

        if (hours > 0) {
            str = str + hours.toString() + " ";
            str = str + mContext.getResources().getQuantityString(R.plurals.hour, hours);
        }
        if (minutes > 0) {
            str = str + " " + minutes.toString() + " ";
            str = str + mContext.getResources().getQuantityString(R.plurals.minute, minutes);
        }
        if (seconds > 0) {
            str = str + " " + seconds.toString() + " ";
            str = str + mContext.getResources().getQuantityString(R.plurals.second, seconds);
        }

        return str;
    }



}
