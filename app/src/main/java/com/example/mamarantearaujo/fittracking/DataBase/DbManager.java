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
 * Created by mateus on 3/15/2016.
 */
public class DbManager {
    private final Context mContext;
    private static SQLiteDatabase mDataBase = null;

    public DbManager(Context context) {
        this.mContext = context;
        if(mDataBase == null) {
            mDataBase = new DbHelper(mContext).getWritableDatabase();
        }
    }

    public void storeActivity(ActivityRecord record) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.activityTable.Cols.activityType, record.mActivity);
        values.put(DbSchema.activityTable.Cols.activityTime, record.mStartTime);

        mDataBase.insert(DbSchema.activityTable.NAME, null, values);
    }

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

    public String dbTohString(){
        String str = new String();

        List<ActivityRecord> list = queryAllActivityRecords();

        for (ActivityRecord record : list){
            str = str + record.toString() + "\n";
        }

        return str;
    }
}
