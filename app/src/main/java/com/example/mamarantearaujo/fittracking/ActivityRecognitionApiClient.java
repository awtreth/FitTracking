package com.example.mamarantearaujo.fittracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Handle the connection with Google Activity Recognition Service
 */
public class ActivityRecognitionApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mApiClient = null;//To connect to Google Services
    private Context mContext;//In our case it'll be the MainActivity instance

    private PendingIntent mPendingIntent;//Will be the ActivityRecognizedService ServiceIntent

    /*
    Already setup conncetion with Google Google Activity Recognition Service
     */
    ActivityRecognitionApiClient(Context context) {
        mContext = context;
        buildApiClient();
    }

    /*
    Setup the connection with Google Google Activity Recognition Service
     */
    private void buildApiClient() {
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /*
    Start the connection with Google Google Activity Recognition Service
    Supposed to be called in onStart method of MainActivity
     */
    public void connect(){
        mApiClient.connect();
    }

    /*
   Explicitly disconnect from Google Google Activity Recognition Service. Also removeActivityUpdates from ActivityRecognizedService
   Supposed to be called in onStop method of MainActivity
    */
    public void disconnect(){
        try {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient, mPendingIntent);
        } catch (IllegalStateException e) {
            //Ignore
        }
        mApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        /*
        Whenever Google Activity Recognition Service has an update, it sends it to ActivityRecognizedService
        that is created on demand
         */
        Intent intent = new Intent(mContext, ActivityRecognizedService.class);
        mPendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Google Activity Recognition Service will try to send information every 3 seconds, although this is not guaranteed
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, mPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
