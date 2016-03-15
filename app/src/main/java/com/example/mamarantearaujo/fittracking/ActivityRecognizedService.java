package com.example.mamarantearaujo.fittracking;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
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
    private final Integer goalActivities[] = {DetectedActivity.IN_VEHICLE, DetectedActivity.ON_FOOT, DetectedActivity.STILL, DetectedActivity.RUNNING, DetectedActivity.WALKING};


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
            Log.v(TAG, "got result");
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result);
        } else {
            Log.v(TAG, "no result");
        }
    }

    private int walkingOrRunning(List<DetectedActivity> probableActivities, int startPoint) {
        for (int i = startPoint; i < probableActivities.size(); i++) {
            if (probableActivities.get(i).getType() == DetectedActivity.RUNNING)
                return DetectedActivity.RUNNING;
            else if (probableActivities.get(i).getType() == DetectedActivity.WALKING)
                return DetectedActivity.WALKING;
        }
        return DetectedActivity.WALKING;
    }

    private void handleDetectedActivities(ActivityRecognitionResult result) {

        //Log.v(TAG, String.format("-----Last activity = %d-------------------", lastActivity));
        int act = -1;

        List<DetectedActivity> probableActivities = result.getProbableActivities();

        for (int i = 0; i < probableActivities.size(); i++) {
            int activityType = probableActivities.get(i).getType();
            Log.v(TAG, String.format("Activity %d; Confidence %d", activityType, probableActivities.get(i).getConfidence()));

            if (activityType == DetectedActivity.STILL) {
                act = DetectedActivity.STILL;
                break;
            } else if (activityType == DetectedActivity.IN_VEHICLE) {
                act = DetectedActivity.IN_VEHICLE;
                break;
            } else if (activityType == DetectedActivity.ON_FOOT) {
                act = walkingOrRunning(probableActivities, i);
                break;
            }
        }

        //Send activity information to MainActivity
        if (act != -1) {
            // lastActivity = act;
            Bundle bundle = new Bundle();
            bundle.putInt("ActivityType", act);
            bundle.putLong("ActivityTime", result.getTime());

            Intent intent = new Intent("ActivityRecognition");
            intent.putExtra("ActivityInfo",bundle);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

}
