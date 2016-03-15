package com.example.mamarantearaujo.fittracking.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mamarantearaujo on 3/15/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "activityBaseHelper";//for debug purposes
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "activityBase.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + DbSchema.activityTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        DbSchema.activityTable.Cols.activityType + ", " +
                        DbSchema.activityTable.Cols.activityTime +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}