package com.example.mamarantearaujo.fittracking;

import java.sql.Time;

/**
 * Created by mateus on 3/15/2016.
 */
public class ActivityRecord {
    public int mActivity;
    public long mStartTime;
    //public int mDuration;

    public ActivityRecord(int activity, long startTime){
        mActivity = activity;
        mStartTime = startTime;
    }

    public String toString() {
        return new Integer(mActivity).toString() + " " + new Time(mStartTime).toString();
    }

}
