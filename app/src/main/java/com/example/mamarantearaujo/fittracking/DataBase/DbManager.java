package com.example.mamarantearaujo.fittracking.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mamarantearaujo.fittracking.ActivityRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Handle database interaction
 */
public class DbManager {
    private final Context mContext;//In this case it'll be our MainActivity instance
    private static SQLiteDatabase mDataBase = null;

    public DbManager(Context context) {
        this.mContext = context;
        if(mDataBase == null) {
            mDataBase = new DbHelper(mContext).getWritableDatabase();
        }
    }

    /*
    Save the provided ActivityRecord in the database
     */
    public void storeActivity(ActivityRecord record) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.activityTable.Cols.activityType, record.mActivity);
        values.put(DbSchema.activityTable.Cols.activityTime, record.mStartTime);

        mDataBase.insert(DbSchema.activityTable.NAME, null, values);
    }

    /*
    Return a list of all saved ActivityRecords currently in the database
     */
    List<ActivityRecord> queryAllActivityRecords(){
        LinkedList<ActivityRecord> list =  new LinkedList<>();

        Cursor cursor = mDataBase.query(DbSchema.activityTable.NAME, null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int activity = cursor.getInt(cursor.getColumnIndex(DbSchema.activityTable.Cols.activityType));
            long startTime = cursor.getLong(cursor.getColumnIndex(DbSchema.activityTable.Cols.activityTime));
            list.push(new ActivityRecord(activity,startTime));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    /*
    Return the last ActivityRecord that was saved in the database.
    For now it does not check if the database is empty or not
     */
    public ActivityRecord getLastActivityRecord() {
        Cursor cursor = mDataBase.query(DbSchema.activityTable.NAME, null, null, null, null, null, null);
        cursor.moveToLast();
        int activity = cursor.getInt(cursor.getColumnIndex(DbSchema.activityTable.Cols.activityType));
        long startTime = cursor.getLong(cursor.getColumnIndex(DbSchema.activityTable.Cols.activityTime));
        return new ActivityRecord(activity, startTime);
    }

    /*
    Close the database
     */
    public void close() {
        mDataBase.close();
    }

    /*
    Return a string that show all the contents of the database
    ActivityNumber ActivityStartTime
     */
    public String toString(){
        String str = new String();

        List<ActivityRecord> list = queryAllActivityRecords();

        for (ActivityRecord record : list){
            str = str + record.toString() + "\n";
        }

        return str;
    }
}
