package com.example.mamarantearaujo.fittracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mamarantearaujo.fittracking.DataBase.DbManager;
import com.google.android.gms.location.DetectedActivity;


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

        //Google ActivityRecognitionAPI connecition handler
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

        updateView(mLastActivity);//Update the views with STILL activity (default)

        // Register to receive messages from ActivityRecognizedService about activity changes (from Google ActivityRecognitionAPI)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("ActivityRecognition"));

    }

    //Initialize mMediaPlayer and plays it (since it's stored in the phone, the execution time is short)
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


    // Our handler for received messages from ActivityRecognizedService about activity changes
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle msg = intent.getBundleExtra("ActivityInfo");
            int activity = msg.getInt("ActivityType");
            long time = msg.getLong("ActivityTime");

            Log.v(TAG, String.format("received msg %d", activity));//Debug messages

            //The activity has changed OR it's the first run
            if (activity != mLastActivity || mLastTime == 0) {
                if (mLastTime != 0)//not the first run
                    toastMsg(mLastActivity, time - mLastTime); //time-mLastActivity: duration of the last activity in milliseconds

                updateView(activity);

                //Special case
                if(activity == DetectedActivity.RUNNING)
                    playMusic();
                else if(mLastActivity == DetectedActivity.RUNNING)
                    stopMusic();

                mDataBase.storeActivity(new ActivityRecord(activity, time));

                //To debug the database saving routines
                Log.v(TAG,"Just stored: " + mDataBase.getLastActivityRecord().toString());

                mLastActivity = activity;
                mLastTime = time;
            }
        }
    };


    //Used when the screen orientation is changed (no more used. We restricted for portrait only)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt("Activity", mLastActivity);
        //outState.putLong("Time", mLastTime);
        super.onSaveInstanceState(outState);
    }

    //Show a toast with the provided activity and its duration (in milliseconds)
    void toastMsg(int activity, long duration) {
        ActivityIds activityIds = mActivityRecognitionHelper.getActivityIds(activity);//new ActivityIds(activity);
        Toast.makeText(this, String.format(getString(R.string.toast_msg), getString(activityIds.toastMsgId),
                mActivityRecognitionHelper.durationToString(duration)), Toast.LENGTH_SHORT).show();
    }

    //Update the textView and the ImageView with the correct images and text
    void updateView(int activity) {
        ActivityIds activityIds = mActivityRecognitionHelper.getActivityIds(activity);//new ActivityIds(activity);
        mTextView.setText(String.format(getString(R.string.text_msg), getString(activityIds.textViewId)));
        mImageView.setImageResource(activityIds.imageId);

    }

    @Override
    protected void onStart() {
        activityRecognitionApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        activityRecognitionApiClient.disconnect();//also disable the update requests
        Log.v(TAG, mDataBase.toString());//just for debug purposes (it's supposed to show the entire database content)
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDataBase.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //if(mMediaPlayer!=null)
            //mMediaPlayer.release();
        super.onDestroy();
    }

}