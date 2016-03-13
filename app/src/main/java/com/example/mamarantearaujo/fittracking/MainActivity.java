package com.example.mamarantearaujo.fittracking;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
    UI Activity. It handles the connection with the Activitity Recognition Google Service and view updates
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mApiClient = null;//To connect to Google Servieces
    private final String TAG = "Main";//for debug purposes
    public static Handler mHandler;//Global handler to be used in ActivityRecognizedService

    //Store the lastActivity (actually it's the current while there is no activity update)
    private int mLastActivity = DetectedActivity.STILL;//STILL by default (only for the 1st time)
    private long mLastTime = 0;//Store the time stamp of mLastActivity

    private ImageView mImageView;
    private TextView mTextView;


    /*
    Auxiliar class that store Ids related to each activity
     */
    private class ActivityIds {
        public int imageId;
        public int textViewId;
        public int toastMsgId;

        ActivityIds(int imageId, int textViewId, int toastMsgId) {
            this.imageId = imageId;
            this.textViewId = textViewId;
            this.toastMsgId = toastMsgId;
        }
    }

    private HashMap<Integer, ActivityIds> mActivityIds;//Map each activity to its ActivityIds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildApiClient();//Initialize mApiClient

        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);

        //Create Hashmap
        mActivityIds = new HashMap<Integer, ActivityIds>();
        mActivityIds.put(DetectedActivity.STILL, new ActivityIds(R.drawable.still, R.string.still, R.string.still));
        mActivityIds.put(DetectedActivity.WALKING, new ActivityIds(R.drawable.walking, R.string.walking, R.string.walked));
        mActivityIds.put(DetectedActivity.RUNNING, new ActivityIds(R.drawable.running, R.string.running, R.string.run));
        mActivityIds.put(DetectedActivity.IN_VEHICLE, new ActivityIds(R.drawable.in_vehicle, R.string.in_vehicle, R.string.in_vehicle));


        //In case of landscape-portrait switch
        if (savedInstanceState != null) {
            mLastActivity = savedInstanceState.getInt("Activity");
            mLastTime = savedInstanceState.getLong("Time");
        }

        updateView(mLastActivity);

        //Initialize global handler (it expects updates from ActivityRecognizedService)
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {//Handle the messages sent by ActivityRecognizedService
                //Detected Activity and Time
                int activity = msg.getData().getInt("ActivityType");
                long time = msg.getData().getLong("ActivityTime");

                Log.v(TAG, String.format("received msg %d", activity));

                //The activity has changed OR it's the first run
                if (activity != mLastActivity || mLastTime == 0) {
                    if (mLastTime != 0)//not the first run
                        toastMsg(mLastActivity, time - mLastTime);

                    updateView(activity);
                    mLastActivity = activity;
                    mLastTime = time;
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Activity", mLastActivity);
        outState.putLong("Time", mLastTime);
        super.onSaveInstanceState(outState);
    }

    /*
    Auxiliary method
    Input in milliseconds
    Output: "[x hour(s)] [y minute(s)] z second(s)"
     */
    private String timeToString(long time) {
        String str = new String(" ");

        time = time / 1000;//milliseconds to seconds
        Integer hours = (int) (time / 3600);
        Integer minutes = (int) ((time % 3600) / 60);
        Integer seconds = (int) time % 60;

        if (hours > 0) {
            str = str + hours.toString() + " ";
            str = str + getResources().getQuantityString(R.plurals.hour, hours);
        }
        if (minutes > 0) {
            str = str + " " + minutes.toString() + " ";
            str = str + getResources().getQuantityString(R.plurals.minute, minutes);
        }
        if (seconds > 0) {
            str = str + " " + seconds.toString() + " ";
            str = str + getResources().getQuantityString(R.plurals.second, seconds);
        }

        return str;
    }


    void toastMsg(int activity, long time) {
        ActivityIds activityIds = mActivityIds.get(activity);//new ActivityIds(activity);
        Toast.makeText(this, String.format(getString(R.string.toast_msg), getString(activityIds.toastMsgId), timeToString(time)), Toast.LENGTH_SHORT).show();
    }

    void updateView(int activity) {
        ActivityIds activityIds = mActivityIds.get(activity);//new ActivityIds(activity);
        mTextView.setText(String.format(getString(R.string.text_msg), getString(activityIds.textViewId)));
        mImageView.setImageResource(activityIds.imageId);

    }

    // Communication with the Google Service ActivityRecognitionApi
    @Override
    protected void onStart() {
        mApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        try {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient, mPendingIntent);
        } catch (IllegalStateException e) {
            //Ignore
        }
        mApiClient.disconnect();
        super.onStop();
    }


    private void buildApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                        //.addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private PendingIntent mPendingIntent;


    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, mPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}