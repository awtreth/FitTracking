package com.example.mamarantearaujo.fittracking;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mamarantearaujo.fittracking.DataBase.DbHelper;
import com.example.mamarantearaujo.fittracking.DataBase.DbManager;
import com.example.mamarantearaujo.fittracking.DataBase.DbSchema;
import com.google.android.gms.location.DetectedActivity;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/*
    UI Activity. It handles the connection with the Activitity Recognition Google Service and view updates
 */
public class MainActivity extends AppCompatActivity {

    private ActivityRecognitionApiClient activityRecognitionApiClient = null;//To connect to Google Servieces
    private final String TAG = "Main";//for debug purposes

    //Store the lastActivity (actually it's the current while there is no activity update)
    private int mLastActivity = DetectedActivity.STILL;//STILL by default (only for the 1st time)
    private long mLastTime = 0;//Store the time stamp of mLastActivity

    private ImageView mImageView;
    private TextView mTextView;

    private DbManager mDataBase;
    private ActivityRecognitionLab mActivityRecognitionHelper;
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityRecognitionApiClient = new ActivityRecognitionApiClient(this);

        mDataBase = new DbManager(this);

        mActivityRecognitionHelper = new ActivityRecognitionLab(this);


        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);

        //In case of landscape-portrait switch
//        if (savedInstanceState != null) {
//            mLastActivity = savedInstanceState.getInt("Activity");
//            mLastTime = savedInstanceState.getLong("Time");
//        }

        updateView(mLastActivity);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("ActivityRecognition"));

    }

    private void playMusic() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.beat_02);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    private void stopMusic() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            //Detected Activity and Time
            Bundle msg = intent.getBundleExtra("ActivityInfo");
            int activity = msg.getInt("ActivityType");
            long time = msg.getLong("ActivityTime");

            Log.v(TAG, String.format("received msg %d", activity));

            //The activity has changed OR it's the first run
            if (activity != mLastActivity || mLastTime == 0) {
                if (mLastTime != 0)//not the first run
                    toastMsg(mLastActivity, time - mLastTime);

                updateView(activity);

                if(activity == DetectedActivity.RUNNING)
                    playMusic();
                else if(mLastActivity == DetectedActivity.RUNNING)
                    stopMusic();

                mDataBase.storeActivity(new ActivityRecord(activity, time));

                Log.v(TAG,"Just stored: " + mDataBase.getLastActivityRecord().toString());

                mLastActivity = activity;
                mLastTime = time;
            }
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt("Activity", mLastActivity);
        //outState.putLong("Time", mLastTime);
        super.onSaveInstanceState(outState);
    }

    void toastMsg(int activity, long duration) {
        ActivityIds activityIds = mActivityRecognitionHelper.getActivityIds(activity);//new ActivityIds(activity);
        Toast.makeText(this, String.format(getString(R.string.toast_msg), getString(activityIds.toastMsgId),
                mActivityRecognitionHelper.durationToString(duration)), Toast.LENGTH_SHORT).show();
    }

    void updateView(int activity) {
        ActivityIds activityIds = mActivityRecognitionHelper.getActivityIds(activity);//new ActivityIds(activity);
        mTextView.setText(String.format(getString(R.string.text_msg), getString(activityIds.textViewId)));
        mImageView.setImageResource(activityIds.imageId);

    }

    // Communication with the Google Service ActivityRecognitionApi
    @Override
    protected void onStart() {
        activityRecognitionApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        activityRecognitionApiClient.disconnect();
        Log.v(TAG, mDataBase.toString());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //mDataBase.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}