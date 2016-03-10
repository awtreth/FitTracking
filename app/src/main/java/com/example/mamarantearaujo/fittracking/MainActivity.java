package com.example.mamarantearaujo.fittracking;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleApiClient mApiClient = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String TAG = "Main";
    private Location mLocation = null;
    public static Handler mHandler;

    private int mCurrentActivity;
    private ImageView mImageView;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildApiClient();

        mCurrentActivity = DetectedActivity.UNKNOWN;

        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);

        updateView(DetectedActivity.STILL);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int activity = msg.getData().getInt("Activity");
                Log.v(TAG,String.format("received msg %d",activity));
                updateView(activity);
            }

        };
    }

    void updateView(int activity) {
        if(activity!=mCurrentActivity) {
            mCurrentActivity = activity;
            int textViewVerbId = 0, imageId = 0;//toastVerbId

            switch(activity) {
                case DetectedActivity.STILL:
                    textViewVerbId = R.string.still;
                    imageId = R.drawable.still;
                    break;
                case DetectedActivity.WALKING:
                    textViewVerbId = R.string.walking;
                    imageId = R.drawable.walking;
                    break;
                case DetectedActivity.IN_VEHICLE:
                    textViewVerbId = R.string.in_vehicle;
                    imageId = R.drawable.in_vehicle;
                    break;
                case DetectedActivity.RUNNING:
                    textViewVerbId = R.string.running;
                    imageId = R.drawable.running;
                    break;
                default: Log.v(TAG,"NOT SUPPOSED TO BE HERE");
            }

            mTextView.setText(String.format(getString(R.string.text_msg), getString(textViewVerbId)));
            mImageView.setImageResource(imageId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
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
    protected void onStop() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient, mPendingIntent);
        mApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            //TODO: print an error
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //TODO: print an error
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        }
    }

}