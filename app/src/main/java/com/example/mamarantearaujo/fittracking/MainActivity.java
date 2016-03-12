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
import android.widget.Toast;

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

    private int mLastActivity = DetectedActivity.STILL;
    private long mLastTime = 0;

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

        mTextView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);

        if (savedInstanceState != null) {
            mLastActivity = savedInstanceState.getInt("Activity");
            mLastTime = savedInstanceState.getLong("Time");
        }

        updateView(mLastActivity);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int activity = msg.getData().getInt("ActivityType");
                long time = msg.getData().getLong("ActivityTime");
                Log.v(TAG, String.format("received msg %d", activity));
                if (activity != mLastActivity) {
                    if (mLastTime != 0)
                        toastMsg(mLastActivity, time-mLastTime);
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

    private class ActivityIds {
        public int imageId;
        public int textViewId;
        public int toastMsgId;

        ActivityIds(int activity) {
            switch (activity) {
                case DetectedActivity.STILL:
                    textViewId = R.string.still;
                    imageId = R.drawable.still;
                    toastMsgId = R.string.still;
                    break;
                case DetectedActivity.WALKING:
                    textViewId = R.string.walking;
                    toastMsgId = R.string.walked;
                    imageId = R.drawable.walking;
                    break;
                case DetectedActivity.IN_VEHICLE:
                    textViewId = R.string.in_vehicle;
                    toastMsgId = R.string.in_vehicle;
                    imageId = R.drawable.in_vehicle;
                    break;
                case DetectedActivity.RUNNING:
                    textViewId = R.string.running;
                    toastMsgId = R.string.run;
                    imageId = R.drawable.running;
                    break;
                default:
                    Log.v("ActivityId", "NOT SUPPOSED TO BE HERE");
            }
        }
    }

    private String timeToString(long time) {
        String str = new String();

        Integer hours = (int) (time/3600);
        Integer minutes = (int) ((time%3600)/60);
        Integer seconds = (int) time%60;

        if(hours > 0) {
            str.concat(" "+hours.toString()+" ");
            str.concat(getResources().getQuantityString(R.plurals.hour, hours));
        }
        if(minutes > 0) {
            str.concat(" "+minutes.toString()+" ");
            str.concat(getResources().getQuantityString(R.plurals.minute, minutes));
        }

        if(seconds> 0) {
            str.concat(" "+seconds.toString()+" ");
            str.concat(getResources().getQuantityString(R.plurals.second, seconds));
        }

        return str;
    }


    void toastMsg(int activity, long time) {
        ActivityIds activityIds = new ActivityIds(mLastActivity);
        Toast.makeText(this, String.format(getString(R.string.toast_msg), getString(activityIds.toastMsgId), timeToString(time)), Toast.LENGTH_SHORT).show();
    }

    void updateView(int activity) {
        ActivityIds activityIds = new ActivityIds(activity);
        mTextView.setText(String.format(getString(R.string.text_msg), getString(activityIds.textViewId)));
        mImageView.setImageResource(activityIds.imageId);

    }

    // Communication with the Google Service ActivityRecognitionApi
    //TODO: complete
    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
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


    //GOOGLE MAPS STUFF
    //TODO: Zoom in the maps automatically to show current location.
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