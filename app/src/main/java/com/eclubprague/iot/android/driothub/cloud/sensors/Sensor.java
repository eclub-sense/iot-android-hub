package com.eclubprague.iot.android.driothub.cloud.sensors;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.eclubprague.iot.android.driothub.cloud.registry.Identificable;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;

/**
 * Created by Dat on 28.7.2015.
 */
public abstract class Sensor implements Identificable {

    protected String uuid;
    protected String hub_uuid;
    protected String secret;
    protected String type = Integer.toString(SensorType.THERMOMETER);
    protected String s_type = "sensor";
    protected transient int incr;
    /*@Expose (deserialize = false) protected int battery;
    @Expose (deserialize = false) protected String hubID;*/
    protected transient WeakReference<Hub> hubRef;
    protected transient byte reserved[] = new byte[3];

    protected transient List<DataNameValuePair> measured = new ArrayList<>();

    public abstract void readPayload(byte[] data);
    public abstract String printData();
    public abstract List<DataNameValuePair> getDataList();
    public abstract void setData(float values[]);

    protected Sensor() {

    }

    protected Sensor(String uuid, int type, String secret, Hub hub) {
        this.hubRef = new WeakReference<Hub>(hub);
        this.hub_uuid = hubRef.get().getUuid();
        this.uuid = uuid;
        this.type = Integer.toString(type);
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

    public String getType() {
        return type;
    }

    public String getSecret() {
        return secret;
    }

    public int getIncr() {
        return incr;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

