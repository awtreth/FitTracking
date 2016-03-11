package com.example.mamarantearaujo.fittracking;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mamarantearaujo on 3/5/2016.
 */
public class ActivityRecognizedService extends IntentService {

    //private int lastActivity = DetectedActivity.UNKNOWN;
    private final String TAG = "ActivityRecognition";
    private final Integer goalActivities[] = {DetectedActivity.IN_VEHICLE, DetectedActivity.STILL, DetectedActivity.RUNNING, DetectedActivity.WALKING};
    //private Handler mHandler;


    //Default Constructor
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActivityRecognizedService(String name) {
        super(name);
    }

    //@Override
    //It is called in the Activity context
    /*public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();//create a handler of MainActivity
        return super.onStartCommand(intent, flags, startId);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result);
        }
    }

    private void handleDetectedActivities(ActivityRecognitionResult result) {

        //Log.v(TAG, String.format("-----Last activity = %d-------------------", lastActivity));
        int act = 0;

        for (DetectedActivity activity : result.getProbableActivities()) {
            Log.v(TAG, String.format("Activity %d; Confidence %d", activity.getType(), activity.getConfidence()));
            if (Arrays.asList(goalActivities).contains(activity.getType())) {
                act = activity.getType();
                Log.v(TAG, String.format("THE Activity %d; Confidence %d", activity.getType(), activity.getConfidence()));
                break;
            }
        }

        //Send activity information to MainActivity
        //if (act != lastActivity){
           // lastActivity = act;

            Bundle bundle = new Bundle();
            bundle.putInt("ActivityType",  act);
            bundle.putLong("ActivityTime", result.getTime());
            Message msg = new Message();
            msg.setData(bundle);
            MainActivity.mHandler.sendMessage(msg);
        //}
    }

}
