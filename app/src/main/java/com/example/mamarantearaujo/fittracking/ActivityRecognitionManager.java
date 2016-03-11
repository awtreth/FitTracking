package com.example.mamarantearaujo.fittracking;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by mateus on 3/10/2016.
 */
public class ActivityRecognitionManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mApiClient = null;
    int mLastActivity;
    //LinkedList<ActivityRecord> recordedActivities;
    private final String TAG = "ActivityRecognitionManager";
    Handler mActivityHandler;
    PendingIntent mServicePendingIntent;

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
