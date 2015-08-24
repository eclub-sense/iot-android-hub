package com.eclubprague.iot.android.driothub.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.LoginActivity;
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
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.VirtualSensorCreator;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.WSDataWrapper;
import com.eclubprague.iot.android.driothub.cloud.sensors.Accelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.GPS;
import com.eclubprague.iot.android.driothub.cloud.sensors.LightSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.ProximitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.eclubprague.iot.android.driothub.tasks.SensorRegistrationTask;
import com.eclubprague.iot.android.driothub.tasks.TestingTask;
import com.eclubprague.iot.android.driothub.tasks.UserRegistrationTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Dat on 28.7.2015.
 */
public class BuiltInSensorsProviderService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {

    //----------------------------------------------------------------
    // SERVICE OVERRIDE METHODS AND BINDER
    //----------------------------------------------------------------

    //Binder for Service to communicate with UI thread
    private final IBinder binder = new BuiltInSensorsProviderBinder();

    private String token = "";
    private String email = "";

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
        mGoogleApiClient.connect();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //might go to onUnbind
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
        stopTimerTask();
        try {
            mConnection.disconnect();
        } catch (Exception e) {
            Log.e("onDisconnect", e.toString());
        }

        super.onDestroy();
    }



    public void initService(String token, String email) {

//        if(builtInSensors.size() > 0) {
//            List<Sensor> tmp_sensors = getBuiltInSensors();
//            for (int i = 0; i < tmp_sensors.size(); i++) {
//                tmp_sensors.get(i).getTimer().stopTimerTask();
//            }
//            builtInSensors.clear();
//        }
        this.token = token;
        this.email = email;
        Log.e("O_TOKEN", token);
        Log.e("O_EMAIL", email);


        USER = new User(email);

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        int uuid = stringToInt(email)  * (stringToInt(android_id)%567 + 1) +
                stringToInt(android.os.Build.MODEL)*(stringToInt(android_id)%9 + 1);
        if(uuid < 0) uuid *= -1;

        UUID = Integer.toString(uuid);

        /*File file = new File(getFilesDir(), "config.co");
        if (!file.exists()) {
            try {
                file.createNewFile();


                FileOutputStream outputStream = openFileOutput("config.co", Context.MODE_PRIVATE);
                outputStream.write(UUID.getBytes());
                outputStream.close();

            } catch (Exception e) {
                Log.e("FILE", e.toString());
            }
        } else {
            sensorsRegistered = true;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                if ((line = br.readLine()) != null) {
                    UUID = line;
                }
                br.close();
            } catch (IOException e) {
                Log.e("FILE", e.toString());
            }
        }*/


        START_ID = uuid + stringToInt(android_id);

        THISHUB = new Hub(UUID, USER);

        connectWebSocket(THISHUB);

        mConnectionRef.add(mConnection);

        initBuiltInSensorsCollection();
    }

    private int stringToInt(String param) {
        int sum = 0;
        for (int i = 0; i < param.length(); i++) {
            sum += (int) param.charAt(i);
        }
        return sum;
    }

    /*private int getRandomInt(int lowerBound, int upperBound) {
        Random r = new Random();
        return r.nextInt(upperBound - lowerBound) + lowerBound;
    }*/


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
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
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
            if (builtInSensors.containsKey(gpsKey)) {
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
        if (mLocationRequest != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this, "mLocationRequest is null", Toast.LENGTH_SHORT).show();
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

    private String gpsKey;

    public void initBuiltInSensorsCollection() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL);

        gpsKey = "GPS_" + email;

        builtInSensors.put(gpsKey, new GPS(Integer.toString(START_ID), "gps_secret_" + Integer.toString(START_ID), THISHUB));
        builtInSensors.get(gpsKey).setTimer(USER, UUID, 5, mConnectionRef);

        for (int i = 0; i < deviceSensors.size(); i++) {
            android.hardware.Sensor sensor = deviceSensors.get(i);
            switch (sensor.getType()) {
                case android.hardware.Sensor.TYPE_ACCELEROMETER:
                    builtInSensors.put(sensor.getName(),
                            new Accelerometer(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_LIGHT:
                    builtInSensors.put(sensor.getName(),
                            new LightSensor(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_PROXIMITY:
                    builtInSensors.put(sensor.getName(),
                            new ProximitySensor(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_MAGNETIC_FIELD:
                    builtInSensors.put(sensor.getName(),
                            new Magnetometer(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_GYROSCOPE:
                    builtInSensors.put(sensor.getName(),
                            new Gyroscope(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_PRESSURE:
                    builtInSensors.put(sensor.getName(),
                            new Barometer(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_GRAVITY:
                    builtInSensors.put(sensor.getName(),
                            new GravitySensor(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_LINEAR_ACCELERATION:
                    builtInSensors.put(sensor.getName(),
                            new LinearAccelerometer(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_ROTATION_VECTOR:
                    builtInSensors.put(sensor.getName(),
                            new RotationSensor(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY:
                    builtInSensors.put(sensor.getName(),
                            new HumiditySensor(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                case android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE:
                    builtInSensors.put(sensor.getName(),
                            new AmbientThermometer(Integer.toString(i + START_ID + 1), sensor.getName(), THISHUB));
                    mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
                default:
            }
            if(builtInSensors.get(sensor.getName()) != null) {
                builtInSensors.get(sensor.getName()).setTimer(USER, UUID, 5, mConnectionRef);
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
        for (int i = 0; i < deviceSensors.size(); i++) {
            if (builtInSensors.get(
                    deviceSensors.get(i).getName()) != null) {
                sensors.add(builtInSensors.get(
                        deviceSensors.get(i).getName()));
            }
        }
        return sensors;
    }

    private boolean sensorsRegistered = false;

    public void registerSensors() {
        sensorsRegistered = true;
//        Log.d("SENSOR_REG", USER.toString());
//        new SensorRegistrationTask(USER).execute(VirtualSensorCreator.createSensorInstance(
//                "0101010101", SensorType.PRESSURE, "pressure_secret", THISHUB
//        ));
        List<Sensor> sensors = getBuiltInSensors();
        for (int i = 0; i < sensors.size(); i++) {
            Log.d("SensorReg", sensors.get(i).toString());
            if(Integer.parseInt(sensors.get(i).getType()) == SensorType.GPS) {
                new SensorRegistrationTask(token, email).execute(sensors.get(i));
                //new TestingTask().execute(token);
                break;
            }
        }

        //if(!mConnection.isConnected()) connectWebSocket(THISHUB);
    }

    public void test() {
        new TestingTask().execute(token);
    }

    //----------------------------------------------------------------
    // WEBSOCKET COMMUNICATION WITH CLOUD-SIDE
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

    private String UUID;
    private User USER;
    //private final String WSURI = "ws://147.32.107.139:8080/events";
    private final String WSURI = "ws://147.32.107.139:1337";
    //private final String WSURI = "ws://192.168.200.19:9002/";
    //private final String WSURI = "ws://echo.websocket.org";
    private Hub THISHUB;
    private int START_ID;

    //A WebSocket to exchange data with cloud-side
    private WebSocketConnection mConnection;

    private ArrayList<WebSocketConnection> mConnectionRef = new ArrayList<>();

    /**
     * Connect to cloud via websocket, including registering device as hub
     *
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
                    Log.d("OpenMessage", hub.toString());
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("WonMessage", payload);
                    /*if (payload.contains("LOGIN_ACK")) {
                        if (!sensorsRegistered) {
                            //registerSensors();
                        }
                        startTimer();
                    }*/
                    startTimer();
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


    //----------------------------------------------------------------
    // TIMER TASK
    // DO SOME WORKS PERIODICALLY
    //----------------------------------------------------------------

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();


    public void startTimer() {
        if (timer != null) return;
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 10000ms the TimerTask will run every 5000ms
        timer.schedule(timerTask, 10000, 5000); //
    }

    public void stopTimerTask() {
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
                        //BuiltInSensorsProviderService.this.activityRef.get().actualizeListView(getBuiltInSensors());

                        //send updated data
                        if(!mConnection.isConnected()) {
                            Log.d("RECONNECT","reconnecting");
                            connectWebSocket(THISHUB);
                            mConnectionRef.clear();
                            mConnectionRef.add(mConnection);
                        }
//                        if (mConnection.isConnected()) {
//
//                            WSDataWrapper data = new WSDataWrapper(getBuiltInSensors(), THISHUB.getUuid());
//
//                            Log.e("WSDATA", data.toString());
//
//                            mConnection.sendTextMessage(data.toString());
//                        }
                    }
                });
            }
        };
    }

}


