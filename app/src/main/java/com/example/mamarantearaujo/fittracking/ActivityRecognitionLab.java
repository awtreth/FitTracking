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
 * Handle some Helper functions related to Detected Activity
 */
public class ActivityRecognitionLab {

    private final Context mContext;
    //Map ActivityType -> resource ids
    private final Map<Integer, ActivityIds> mActivityIdMap;

    ActivityRecognitionLab(Context context) {
        mContext = context;
        //Map ActivityType -> resource ids
        HashMap<Integer, ActivityIds> map = new HashMap<>();
        map.put(DetectedActivity.STILL, new ActivityIds(R.drawable.still, R.string.still, R.string.still));
        map.put(DetectedActivity.WALKING, new ActivityIds(R.drawable.walking, R.string.walking, R.string.walked));
        map.put(DetectedActivity.RUNNING, new ActivityIds(R.drawable.running, R.string.running, R.string.run));
        map.put(DetectedActivity.IN_VEHICLE, new ActivityIds(R.drawable.in_vehicle, R.string.in_vehicle, R.string.in_vehicle));
        mActivityIdMap = map;

    }

    //return the resource ids of the provided activity type
    public ActivityIds getActivityIds(int activity) {
        return mActivityIdMap.get(activity);
    }

    /*
    Convert a duration time in UTC milliseconds to the format:
    "[hh] hours [mm] minutes [ss] seconds"
    This is supposed to be used in the ToastMsg
    */
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

    public String parseActivityType(int activity) {
        return mContext.getResources().getString(this.getActivityIds(activity).textViewId);
    }

    public int parseActivityName(String activityName) {
        if(mContext.getResources().getString(this.getActivityIds(DetectedActivity.STILL).textViewId).equals(activityName))
            return DetectedActivity.STILL;
        if(mContext.getResources().getString(this.getActivityIds(DetectedActivity.WALKING).textViewId).equals(activityName))
            return DetectedActivity.WALKING;
        if(mContext.getResources().getString(this.getActivityIds(DetectedActivity.RUNNING).textViewId).equals(activityName))
            return DetectedActivity.RUNNING;
        if(mContext.getResources().getString(this.getActivityIds(DetectedActivity.IN_VEHICLE).textViewId).equals(activityName))
            return DetectedActivity.IN_VEHICLE;
        return DetectedActivity.STILL;
    }

}
