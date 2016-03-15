package com.example.mamarantearaujo.fittracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Created by mateus on 3/15/2016.
 */
public class ActivityRecognitionApiClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mApiClient = null;//To connect to Google Servieces
    private Context mContext;
    private PendingIntent mPendingIntent;

    ActivityRecognitionApiClient(Context context) {
        mContext = context;
        buildApiClient();
    }

    private void buildApiClient() {
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connect(){
        mApiClient.connect();
    }

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
        Intent intent = new Intent(mContext, ActivityRecognizedService.class);
        mPendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, mPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
