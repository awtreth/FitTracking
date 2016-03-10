package com.example.mamarantearaujo.fittracking;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
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

    private int lastActivity = DetectedActivity.STILL;
    private final String TAG = "ActivityRecognition";
    private final Integer goalActivities[] = {DetectedActivity.IN_VEHICLE, DetectedActivity.STILL, DetectedActivity.RUNNING, DetectedActivity.WALKING};

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
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result);
        }
    }

    private void handleDetectedActivities(ActivityRecognitionResult result) {

        Log.v(TAG, "-----------------------------------------------------");
        int act = 0;

        for (DetectedActivity activity : result.getProbableActivities()) {
            Log.v(TAG, String.format("Activity %d; Confidence %d", activity.getType(), activity.getConfidence()));
            if (Arrays.asList(goalActivities).contains(activity.getType())) {
                act = activity.getType();
                Log.v(TAG, String.format("THE Activity %d; Confidence %d", activity.getType(), activity.getConfidence()));
                break;
            }
        }

        //int activity = result.getMostProbableActivity().getType();
        if (act != lastActivity){
            lastActivity = act;
            updateActivity(act);
        }

        //Toast.makeText(getApplicationContext(), "Hello Toast!", Toast.LENGTH_LONG).show();
    }

    private void updateActivity(int activity) {
        int imageID = 0;
        int toastMsgID = 0;
        int textMsgID = 0;

        switch (activity) {
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
            default:
                Log.e("ActivityRecogition", "Not supposed to be here");
        }

        //TextView textView = findViewByID();

        //Toast.makeText(this, toastMsgID, Toast.LENGTH_SHORT).show();
    }


}
