package com.eclubprague.iot.android.driothub.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Dat on 25.7.2015.
 */
public class LocationListenerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private Context context;



    private final IBinder binder = new LocationListenerBinder();
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocationListenerBinder extends Binder {

        public LocationListenerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationListenerService.this;
        }

        public void setServiceContext(Context context) {
            LocationListenerService.this.context = context;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mGoogleApiClient.connect();
        return binder;
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        createLocationRequest();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        return super.onUnbind(intent);
    }

    private Location mLastLocation;
    private double longitude = -1;
    private double latitude = -1;

    private boolean connected = false;

    @Override
    public void onConnected(Bundle connectionHint) {
        connected = true;
        Toast.makeText(this,"Connection connected",Toast.LENGTH_SHORT).show();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection Failed",Toast.LENGTH_SHORT).show();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean getConnected() {
        return connected;
    }

    //------------------------------------------------------

    private LocationRequest mLocationRequest;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if(mLocationRequest != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this,"mLocationRequest is null", Toast.LENGTH_SHORT).show();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private String mLastUpdateTime;

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }

        showLocation();
    }

    //-----------------------------------------------------------------------

    private void doit(String code)
    {

        if(context == null) {
            return;
        }
        // Create an execution environment.
        org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();

        // Turn compilation off.
        cx.setOptimizationLevel(-1);

        try
        {
            // Initialize a variable scope with bindnings for
            // standard objects (Object, Function, etc.)
            Scriptable scope = cx.initStandardObjects();

            // Set a global variable that holds the activity instance.
            ScriptableObject.putProperty(
                    scope, "TheActivity", org.mozilla.javascript.Context.javaToJS(context, scope));

            // Evaluate the script.
            cx.evaluateString(scope, code, "doit:", 1, null);
        }
        finally
        {
            org.mozilla.javascript.Context.exit();
        }
    }

    private void showLocation() {
        String msg = "connected: " + Boolean.toString(connected) +
                ", latitude: " + Double.toString(latitude) +
                ", longitude: " + Double.toString(longitude);

        doit(
                "var widgets = Packages.android.widget;\n" +
                        "var view = new widgets.TextView(TheActivity);\n" +
                        "TheActivity.setContentView(view);\n" +
                        "var text = '" +
                        msg +
                        "';\n" +
                        "view.append(text);"
        );

        //Toast.makeText(this, "got GPS update",Toast.LENGTH_SHORT).show();
    }
}
