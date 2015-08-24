package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;

//import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;

/**
 * Created by Dat on 13.8.2015.
 */
public class SensorDataSendingTimer {

    //private ArrayList<Sensor> sensorRef;
    private Sensor sensor;
    private User user;
    private int seconds = 5;
    private ArrayList<WebSocketConnection> connectionRef;
    //private WebSocketConnection connection;
    private String hub_uuid;

    /*public ArrayList<WebSocketConnection> getConnectionRef() {
        return connectionRef;
    }*/

    /*public WebSocketConnection getConnection() {
        return connection;
    }*/


    public ArrayList<WebSocketConnection> getConnectionRef() {
        return connectionRef;
    }

    public String getHub_uuid() {
        return hub_uuid;
    }

    public User getUser() {
        return user;
    }

    public SensorDataSendingTimer(/*ArrayList<Sensor> sensorRef*/ Sensor sensor, User user, String hub_uuid,
                                  int seconds, ArrayList<WebSocketConnection> connectionRef/*WebSocketConnection connection*/) {
        //this.sensorRef = sensorRef;
        this.sensor = sensor;
        this.user = user;
        this.seconds = seconds;
        this.connectionRef = connectionRef;
        //this.connection = connection;
        this.hub_uuid = hub_uuid;
        if(this.seconds != 0) {
            startTimer();
        }
    }

    //----------------------------------------------------------------
    // TIMER TASK
    // DO SOME WORKS PERIODICALLY
    //----------------------------------------------------------------

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    public void setPeriod(int seconds) {
        stopTimerTask();
        this.seconds = seconds;
        startTimer();
    }


    public void startTimer() {
        if(timer != null || seconds == 0) return;
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every @seconds s
        timer.schedule(timerTask, 10000, seconds*1000); //
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        //TODO SEND SENSOR DATA
                        if (connectionRef != null && connectionRef.size() > 0 &&
                                connectionRef.get(0)/*connection*/.isConnected()) {

                            List<Sensor> sensorList = new ArrayList<>();
                            sensorList.add(/*sensorRef.get()*/sensor);
                            WSDataWrapper data = new WSDataWrapper(sensorList, hub_uuid);

                            Log.e("WSDATA", data.toString());

                            connectionRef.get(0)/*connection*/.sendTextMessage(data.toString());
                        }

                    }
                });
            }
        };
    }

}
