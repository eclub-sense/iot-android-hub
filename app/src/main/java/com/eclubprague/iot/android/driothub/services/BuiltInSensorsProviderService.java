package com.eclubprague.iot.android.driothub.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.MainActivity;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.AmbientThermometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.Barometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.GravitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Gyroscope;
import com.eclubprague.iot.android.driothub.cloud.sensors.HumiditySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.LinearAccelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.Magnetometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.RotationSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorDataWrapper;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.WSDataWrapper;
//import com.eclubprague.iot.android.driothub.tasks.UserRegisterTask.UserRegisterCallbacks;
import com.eclubprague.iot.android.driothub.cloud.sensors.Accelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.GPS;
import com.eclubprague.iot.android.driothub.cloud.sensors.LightSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.ProximitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.eclubprague.iot.android.driothub.tasks.UserRegistrationTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Dat on 28.7.2015.
 */
public class BuiltInSensorsProviderService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener,
        UserRegistrationTask.TaskDelegate
        /*,
        UserRegisterTask.UserRegisterCallbacks*/ {

    //UI fields, only for testing and debugging purposes
    private Context context;
    private List<MainActivity> activityList = new ArrayList<>();

    //----------------------------------------------------------------
    // SERVICE OVERRIDE METHODS AND BINDER
    //----------------------------------------------------------------

    //Binder for Service to communicate with UI thread
    private final IBinder binder = new BuiltInSensorsProviderBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize client for GPS acquiring
        buildGoogleApiClient();
        createLocationRequest();
        //Run the service in background thread
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO new UserRegisterTask(this).execute(new User("DAT", "999"));

        new UserRegistrationTask(this).execute(USER);

        mGoogleApiClient.connect();
        startTimer();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
        stoptimertask();
        mConnection.disconnect();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class BuiltInSensorsProviderBinder extends Binder {

        public BuiltInSensorsProviderService getService() {
            // Return this instance of this Service so clients can call its public methods
            return BuiltInSensorsProviderService.this;
        }
    }

    //----------------------------------------------------------------
    // GOOGLE API CLIENT OVERRIDE METHODS
    //----------------------------------------------------------------

    //GoogleApiClient to acquire GPS data
    private GoogleApiClient mGoogleApiClient;

    //Build GoogleApiClient to request GPS
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        connected = true;
        Toast.makeText(this, "Connection connected", Toast.LENGTH_SHORT).show();
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


    //----------------------------------------------------------------
    // LOCATION LISTENER OVERRIDE METHODS AND ADDITIONAL METHODS
    //----------------------------------------------------------------

    /*
* GPS location fields
*/
    private Location mLastLocation;
    private double longitude = -1;
    private double latitude = -1;

    private boolean connected = false;


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean getConnected() {
        return connected;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if(builtInSensors.containsKey(gpsKey)) {
                ((GPS) (builtInSensors.get(gpsKey))).setCoordinates(latitude, longitude);
            }
        }
    }

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

    //----------------------------------------------------------------
    // SENSORS AND SENSOR EVENT LISTENER OVERRIDE METHODS
    //----------------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        builtInSensors.get(event.sensor.getName()).setData(event.values);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
    }

    //--SENSOR MANAGEMENT--//

    private SensorManager mSensorManager;
    private List<android.hardware.Sensor> deviceSensors;

    private HashMap<String, Sensor> builtInSensors = new HashMap<>();

    //private List<Sensor> simpleBuiltInSensorsList = new ArrayList<>();

    private String gpsKey = "GPS_phamtdat";

    //private boolean sensorsCollectionsInitialized = false;

    public void initBuiltInSensorsCollection(List<MainActivity> activityList) {
        this.activityList = activityList;
        context = activityList.get(0);
        if(context == null) return;
        //sensorsCollectionsInitialized = true;
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL);

        builtInSensors.put(gpsKey, new GPS(UUID, "gps_secret"));

        for(int i = 0; i < deviceSensors.size(); i++) {
            android.hardware.Sensor sensor = deviceSensors.get(i);
            switch(sensor.getType()) {
                case android.hardware.Sensor.TYPE_ACCELEROMETER:
                    builtInSensors.put(sensor.getName(),
                            new Accelerometer(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_LIGHT:
                    builtInSensors.put(sensor.getName(),
                            new LightSensor(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_PROXIMITY:
                    builtInSensors.put(sensor.getName(),
                            new ProximitySensor(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.MAGNETOMETER:
                    builtInSensors.put(sensor.getName(),
                            new Magnetometer(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.GYROSCOPE:
                    builtInSensors.put(sensor.getName(),
                            new Gyroscope(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.PRESSURE:
                    builtInSensors.put(sensor.getName(),
                            new Barometer(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.GRAVITY:
                    builtInSensors.put(sensor.getName(),
                            new GravitySensor(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.LINEAR_ACCELEROMETER:
                    builtInSensors.put(sensor.getName(),
                            new LinearAccelerometer(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.ROTATION:
                    builtInSensors.put(sensor.getName(),
                            new RotationSensor(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.HUMIDITY:
                    builtInSensors.put(sensor.getName(),
                            new HumiditySensor(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case SensorType.AMBIENT_THERMOMETER:
                    builtInSensors.put(sensor.getName(),
                            new AmbientThermometer(UUID, sensor.getName()));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                default:
                /*builtInSensors.put(sensor.getName(),
                        new BuiltInSensor(UUID, sensor.getName()));*/
            }
        }
    }

    public HashMap<String, Sensor> getBuiltInSensorsHashMap() {
        return builtInSensors;
    }

    public List<android.hardware.Sensor> getDeviceSensors() {
        return deviceSensors;
    }

    public List<Sensor> getBuiltInSensors() {
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(builtInSensors.get(gpsKey));
        for(int i = 0; i < deviceSensors.size(); i++) {
            if(builtInSensors.get(
                    deviceSensors.get(i).getName() ) != null) {
                sensors.add(builtInSensors.get(
                        deviceSensors.get(i).getName()));
            }
        }
        return sensors;
    }

    //----------------------------------------------------------------
    // COMMUNICATION WITH CLOUD-SIDE
    //----------------------------------------------------------------

    /*
    * Data to match cloud-side testing data
    * Default user: User, password: 123
    * New user registration is not available in this moment
    * UUID: hub id, will be probably replaced by some device (phone) id
    * USER: user
    * WSURI: cloud's endpoint
    * THISHUB: this device as hub
    */

    private final String UUID = "2309";
    private final String USERNAME = "User";
    private final String PSSWD = "123";
    private final User USER = new User(USERNAME,PSSWD);
    //private final String WSURI = "ws://192.168.201.222:8080/events";
    private final String WSURI = "ws://echo.websocket.org";
    private final Hub THISHUB = new Hub(UUID, USER);

//    @Override
//    public void handleUserRegistered(UserRegisterTask.UserRegisterResult result) {
//        try{
//
//            //connectWebSocket(new Hub("2309", result.getUser()));
//            //connectWebSocket(new Hub("2309", new User("User", "123")));
//            //Log.e("connecting ws: ", hubs[0].toString());
//
//        } catch(Exception e) {
//            Log.e("HubRegisterProxyTask:", e.toString());
//        }
//
//
//    }

    //A WebSocket to exchange data with cloud-side
    private WebSocketConnection mConnection;

    /**
    * Connect to cloud via websocket, including registering device as hub
    * @param hub this device as hub
    */
    private void connectWebSocket(final Hub hub) {
        mConnection = new WebSocketConnection();

        try {
            mConnection.connect(WSURI, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d("WonOpen", "Status: Connected to " + WSURI);
                    mConnection.sendTextMessage(hub.toString());
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("WonMessage", "Got echo: " + payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("WonClose", "Connection lost.");
                }
            });
        } catch (Exception e) {

            Log.d("WException", e.toString());
        }
    }

    @Override
    public void onUserRegistrationTaskCompleted() {
        connectWebSocket(THISHUB);
    }


    //----------------------------------------------------------------
    // TIMER TASK
    // DO SOME WORKS PERIODICALLY
    //----------------------------------------------------------------

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to do repeatedly send updated data to cloud
                handler.post(new Runnable() {
                    public void run() {
                        //update UI, will be removed in the end
                        BuiltInSensorsProviderService.this.activityList.get(0).actualizeListView(getBuiltInSensors());

                        //send updated data
                        if(mConnection.isConnected()) {

                            WSDataWrapper data = new WSDataWrapper(getBuiltInSensors());

                            Log.e("WSDATA", data.toString());

                            mConnection.sendTextMessage(data.toString());
                        }
                    }
                });
            }
        };
    }

}


