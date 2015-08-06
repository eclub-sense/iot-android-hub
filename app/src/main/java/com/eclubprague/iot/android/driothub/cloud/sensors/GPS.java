package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorDataWrapper;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class GPS  extends Sensor {

    /*@Expose(deserialize = false)*/ protected double latitude = -1;
    protected double longitude = -1;

    public GPS() {
        super();
    }
    public GPS(String uuid, String secret) {
        super(uuid, SensorType.GPS, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("longitude = " + longitude + ", latitude = " + latitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        data.clear();
        data.add(new DataNameValuePair("latitude", Double.toString(latitude)));
        data.add(new DataNameValuePair("longitude", Double.toString(longitude)));
        return data;
    }

    @Override
    public void setData(float[] values) {
    }
}