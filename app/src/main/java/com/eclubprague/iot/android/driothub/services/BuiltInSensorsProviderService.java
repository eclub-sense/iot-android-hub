package com.eclubprague.iot.android.driothub.services;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.MainActivity;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.tasks.UserRegisterTask;
import com.eclubprague.iot.android.driothub.cloud.sensors.Accelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.BuiltInSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.GPS;
import com.eclubprague.iot.android.driothub.cloud.sensors.LightSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.ProximitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        UserRegisterTask.UserRegisterCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private Context context;
    private List<MainActivity> activityList = new ArrayList<>();

    private final IBinder binder = new BuiltInSensorsProviderBinder();

    @Override
    public void handleUserRegistered(UserRegisterTask.UserRegisterResult result) {
        //Toast.makeText(this, "User registration done", Toast.LENGTH_SHORT).show();
        try{

            //connectWebSocket(new Hub("2309", result.getUser()));
            //connectWebSocket(new Hub("2309", new User("User", "123")));
            //Log.e("connecting ws: ", hubs[0].toString());

        } catch(Exception e) {
            Log.e("HubRegisterProxyTask:", e.toString());
        }


    }

    WebSocketClient mWebSocketClient;

    private void connectWebSocket(final Hub hub) {
        URI uri;
        try {
            uri = new URI("ws://192.168.201.222:8080/events");
            //uri = new URI("ws://echo.websocket.org/");
        } catch (Exception e) {
            Log.e("URI:", e.toString());
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {



            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("onOpen:","wSocket opened");

                try {
                    Log.e("Sending:", hub.toString());
                    send(hub.toString());
                } catch(Exception e) {
                    Log.e("Send:", e.toString());
                }


            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.e("onMessage:", message);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("onClose:","Websocket Closed");
            }

            @Override
            public void onError(Exception e) {
                Log.e("ERROR",e.toString());
            }


            @Override
            public void connect() {
                Log.e("CONNECT","Connecting");
                super.connect();
            }

            @Override
            public void run() {
                Log.e("RUN","run");
                super.run();
            }

            @Override
            public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {
                Log.e("WS","onWebsocketHandshakeSentAsClient");
                try {
                    super.onWebsocketHandshakeSentAsClient(conn, request);
                } catch(Exception e) {
                    Log.e("WSE",e.toString());
                }
            }

            @Override
            public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {
                Log.e("WS", "onWebsocketHandshakeReceivedAsClient");
                try {
                    super.onWebsocketHandshakeSentAsClient(conn, request);
                } catch(Exception e) {
                    Log.e("WSE",e.toString());
                }
            }
        };

        try {
            mWebSocketClient.connect();
        } catch(Exception e) {
            Log.e("onConnect",e.toString());
        }
    }

    private WebSocketConnection mConnection;

    private void connectWebSockett(final Hub hub) {
        final String wsuri = "ws://192.168.201.222:8080/events";
        mConnection = new WebSocketConnection();

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d("WonOpen", "Status: Connected to " + wsuri);
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



    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class BuiltInSensorsProviderBinder extends Binder {

        public BuiltInSensorsProviderService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BuiltInSensorsProviderService.this;
        }

        public void setServiceContext(Context context) {
            BuiltInSensorsProviderService.this.context = context;
        }

        public void setServiceActivity(List<MainActivity> activityList) {
            BuiltInSensorsProviderService.this.activityList = activityList;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

//        if(!sensorsCollectionsInitialized) {
//            initBuiltInSensorsCollection();
//        }
        //new UserRegisterTask(this).execute(new User("DAT", "999"));
        connectWebSockett(new Hub("2309", new User("User","123")));
        mGoogleApiClient.connect();
        startTimer();
        return binder;
    }

    //Build GoogleApiClient to request GPS
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
        //Run the service in background thread
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
        mSensorManager.unregisterListener(this);
        stoptimertask();
        try{
            mWebSocketClient.close();
        } catch (Exception e) {
            Log.e("CLOSED:", "Already closed");
        }
        return super.onUnbind(intent);
    }

    private Location mLastLocation;
    private double longitude = -1;
    private double latitude = -1;

    private boolean connected = false;

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
            if(builtInSensors.containsKey(gpsKey)) {
                ((GPS) (builtInSensors.get(gpsKey))).setCoordinates(latitude, longitude);
            }
        }
    }

    //-----------------------------------------------------------
    //SensorEventListener
    //-----------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {

            case android.hardware.Sensor.TYPE_ACCELEROMETER:
                ( (Accelerometer)(builtInSensors.get(event.sensor.getName())) ).setData(
                        event.values[0], event.values[1], event.values[2]
                );
                break;
            case android.hardware.Sensor.TYPE_LIGHT:
                ( (LightSensor)(builtInSensors.get(event.sensor.getName())) ).setData(
                        event.values[0]
                );
                break;
            case android.hardware.Sensor.TYPE_PROXIMITY:
                ( (ProximitySensor)(builtInSensors.get(event.sensor.getName())) ).setData(
                        event.values[0]
                );
                break;
            default:
                List<Float> data = new ArrayList<>();
                for(int i = 0; i < event.values.length; i++) {
                    data.add(event.values[i]);
                }
                ( (BuiltInSensor)(builtInSensors.get(event.sensor.getName())) ).setData(data);
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
    }

    //-----------------------------------------------------------
    //SensorManagement
    //-----------------------------------------------------------

    private SensorManager mSensorManager;
    private List<android.hardware.Sensor> deviceSensors;

    private HashMap<String, Sensor> builtInSensors = new HashMap<>();

    private String gpsKey = "GPS_phamtdat";

    private boolean sensorsCollectionsInitialized = false;

    public void initBuiltInSensorsCollection(List<MainActivity> activityList) {
        this.activityList = activityList;
        context = activityList.get(0);
        if(context == null) return;
        sensorsCollectionsInitialized = true;
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL);

        builtInSensors.put(gpsKey, new GPS("12346", "gps_secret"));

        for(int i = 0; i < deviceSensors.size(); i++) {
            android.hardware.Sensor sensor = deviceSensors.get(i);
            switch(sensor.getType()) {
                case android.hardware.Sensor.TYPE_ACCELEROMETER:
                    builtInSensors.put(sensor.getName(),
                            new Accelerometer(Integer.toString(i), sensor.getName()));
                    break;
                case android.hardware.Sensor.TYPE_LIGHT:
                    builtInSensors.put(sensor.getName(),
                            new LightSensor(Integer.toString(i), sensor.getName()));
                    break;
                case android.hardware.Sensor.TYPE_PROXIMITY:
                    builtInSensors.put(sensor.getName(),
                            new ProximitySensor(Integer.toString(i), sensor.getName()));
                    break;
                default:
                builtInSensors.put(sensor.getName(),
                        new BuiltInSensor(Integer.toString(i), sensor.getName()));
            }
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
            sensors.add(builtInSensors.get(
                    deviceSensors.get(i).getName()  ) );
        }
        return sensors;
    }

    //-----------------------------------------------------------
    //TimerTask
    //-----------------------------------------------------------

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
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        BuiltInSensorsProviderService.this.activityList.get(0).actualizeListView(getBuiltInSensors());
                    }
                });
            }
        };
    }

}


