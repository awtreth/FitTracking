package com.example.mamarantearaujo.fittracking;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mamarantearaujo on 3/5/2016.
 */
public class ActivityRecognizedService extends IntentService {

    private int lastActivity = DetectedActivity.UNKNOWN;

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
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        HashMap<Integer, Integer> confidenceMap = new HashMap<Integer,Integer>();

        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    break;
                }
                default: Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() ); continue;
            }
            confidenceMap.put(activity.getType(), activity.getConfidence());
        }


        if(confidenceMap.size()==0)
            return;

        int maxConfidence = Collections.max(confidenceMap.values());
        int currentActivity = lastActivity;

        for (Map.Entry<Integer, Integer> entry : confidenceMap.entrySet()) {  // Itrate through hashmap
            if (entry.getValue()==maxConfidence) {
                currentActivity = entry.getKey();     // Print the key with max value
                break;
            }
        }

        if(currentActivity != lastActivity) {
            //TODO: updateActivity
            lastActivity = currentActivity;
        }
    }

    private void updateActivity(int activity) {
        int imageID = 0;
        int toastMsgID = 0;
        int textMsgID = 0;

        switch(activity) {
            case DetectedActivity.IN_VEHICLE: {
                imageID = R.drawable.in_vehicle;
                toastMsgID = R.string.toast_msg;
                break;
            }
            case DetectedActivity.RUNNING: {
                imageID = R.drawable.running;
                toastMsgID = R.string.toast_msg;
                break;
            }
            case DetectedActivity.STILL: {
                imageID = R.drawable.still;
                toastMsgID = R.string.toast_msg;
                break;
            }
            case DetectedActivity.WALKING: {
                imageID = R.drawable.walking;
                toastMsgID = R.string.toast_msg;
                break;
            }
            default: Log.e( "ActivityRecogition", "Not supposed to be here");
        }

        Toast.makeText(this, toastMsgID, Toast.LENGTH_SHORT).show();
    }




}
