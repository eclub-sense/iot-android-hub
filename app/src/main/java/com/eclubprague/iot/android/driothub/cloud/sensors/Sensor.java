package com.eclubprague.iot.android.driothub.cloud.sensors;

//import java.lang.ref.WeakReference;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorDataSendingTimer;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.WSDataWrapper;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.SensorEntity;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.google.gson.Gson;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.eclubprague.iot.android.driothub.cloud.registry.Identificable;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;

import de.tavendo.autobahn.WebSocketConnection;

/**
 * Created by Dat on 28.7.2015.
 */
public abstract class Sensor implements Identificable {

    protected String uuid;
    protected String hub_uuid;
    protected String secret;
    protected int type = SensorType.THERMOMETER;
    protected String s_type = "sensor";
    protected transient int incr;
    protected transient SensorDataSendingTimer timer;
    protected transient int seconds = 5;
    /*@Expose (deserialize = false) protected int battery;
    @Expose (deserialize = false) protected String hubID;*/
    //protected transient ArrayList<Hub> hubRef = new ArrayList<>();
    protected transient Hub hub;
    protected transient byte reserved[] = new byte[3];

    protected transient List<DataNameValuePair> measured = new ArrayList<>();

    public abstract void readPayload(byte[] data);
    public abstract String printData();
    public abstract List<DataNameValuePair> getMeasured();
    public abstract void setData(float values[]);

    protected Sensor() {

    }

    protected Sensor(SensorEntity entity) {
        try {
            //this.hubRef = new WeakReference<Hub>(new Hub(entity.getHub().getUuid()));
            //this.hub_uuid = hubRef.get().getUuid();
        } catch (Exception e) {
            Log.e("EXC", e.toString());
        }
        this.uuid = entity.getUuid();
        this.type = entity.getType();
        this.s_type = SensorType.getStringSensorType(type);
        this.secret = "secret";
        //this.description = entity.getDescription();
        //this.access = entity.getAccess();
        //this.owner = entity.getOwnerEmail();
//		if(entity.getActions() != null && entity.getActions().size() > 0) {
//			this.actions = entity.getActions();
//		}
    }

    protected Sensor(String uuid, int type, String secret, Hub hub) {
        //this.hubRef.add(hub);
        this.hub = hub;
        this.hub_uuid = /*hubRef.get(0).getUuid()*/ hub.getUuid();
        this.uuid = uuid;
        this.type = type;
        this.s_type = SensorType.getStringSensorType(type);
        this.secret = secret;
    }

    public void readPacket(String p) throws DecoderException {
        byte[] packet = decrypt(p);
        incr = (int)(packet[0]);
        //battery = (int)(packet[2]);
        reserved[0] = packet[3];
        reserved[1] = packet[4];
        reserved[2] = packet[5];
        readPayload(Arrays.copyOfRange(packet, 6, (p.length() / 2) + 1));
    }

    private byte[] decrypt(String encrypted) throws DecoderException {
        int len = encrypted.length()/2;
        byte[] secretBytes = Hex.decodeHex(secret.toCharArray());
        byte[] encryptedBytes = Hex.decodeHex(encrypted.toCharArray());
        for (int i = 0; i < len; i++) {
            encryptedBytes[i] = (byte)(0xff & ((int)secretBytes[i] ^ (int)encryptedBytes[i]));
        }
        return encryptedBytes;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    /*public String getStringUuid() {
        return String.format("%08d", uuid);
    }*/

    public int getType() {
        return type;
    }

    public String getSecret() {
        return secret;
    }

    public int getIncr() {
        return incr;
    }

    public String getStringType() {
        return s_type;
    }

    /*public int getBattery() {
        return battery;
    }*/

//    public Hub getHub() {
//        return hub;
//    }
//
//    public void setHub(Hub hub) {
//        this.hub = hub;
//    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTimer(User user, String hub_uuid, int seconds, /*WebSocketConnection webSocketConnection*/
                         ArrayList<WebSocketConnection> connectionRef) {
        timer = new SensorDataSendingTimer(this,
                user, hub_uuid, seconds, connectionRef);
    }

    public void stopTimer() {
        timer.stopTimerTask();
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
        timer.setPeriod(seconds);
    }

    public int getSeconds() {
        return seconds;
    }

    public SensorDataSendingTimer getTimer() {
        return timer;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

        public void sendData(User user, String uuid, ArrayList<WebSocketConnection> mConnectionRef) {
        //TODO SEND SENSOR DATA
        if (mConnectionRef != null && mConnectionRef.size() > 0 &&
                mConnectionRef.get(0)/*connection*/.isConnected()) {

            List<Sensor> sensorList = new ArrayList<>();
            sensorList.add(this);
            WSDataWrapper data = new WSDataWrapper(sensorList, hub_uuid);

            Log.e("WSDATA", data.toString());

            mConnectionRef.get(0)/*connection*/.sendTextMessage(data.toString());
        }
    }
}

