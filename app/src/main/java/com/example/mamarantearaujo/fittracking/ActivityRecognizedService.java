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
 * PendingIntent that gets updates from Google Activity Recognition Service
 * The connection is handled by ActivityRecognitionApiClient used in MainActivity
 */
public class ActivityRecognizedService extends IntentService {

    //private int lastActivity = DetectedActivity.UNKNOWN;
    private final String TAG = "ActivityRecognition";

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


    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            //Log.v(TAG, "got result");
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result);
        } else {
            //Log.v(TAG, "no result");
        }
    }

    /*
    Called when ON_FOOT is the most probable activity
    Return the most probable activity between RUNNING and WALKING
    @param probableActivities: list of DetectedActivities
    @param startPoint: the position it will start looking for the running and walking activities in the list
     */
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

        List<DetectedActivity> probableActivities = result.getProbableActivities();//List of probable activities

        //Filter the activities of interest
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

        //Send activity information (ActivityType and ActivityTime) to MainActivity
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
