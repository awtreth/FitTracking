package com.example.mamarantearaujo.fittracking;

import java.sql.Time;

/**
 * Information saved in the database
 */
public class ActivityRecord {
    public int mActivity;//Activity Number
    public long mStartTime;//Activity start time (UTC in milliseconds) Use the method toString to convert it to hour:minutes:seconds format
    //public int mDuration;

    public ActivityRecord(int activity, long startTime){
        mActivity = activity;
        mStartTime = startTime;
    }

    /*
    ActivityNumber StartTime
    The StartTime is showed in the format:
        hour:minutes:seconds
     */
    public String toString() {
        return new Integer(mActivity).toString() + " " + new Time(mStartTime).toString();
    }

}
