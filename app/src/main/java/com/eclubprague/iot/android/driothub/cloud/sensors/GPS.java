package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorDataWrapper;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class GPS  extends Sensor {

    @Expose(deserialize = false) protected double latitude = -1;
    @Expose (deserialize = false) protected double longitude = -1;

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
        return "GPS [latitude=" + latitude + ", longitude=" + longitude + ", uuid=" + uuid + ", type="
                + type + ", secret=" + secret + "]";
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        data.clear();
        data.add(new DataNameValuePair("latitude", Double.toString(latitude)));
        data.add(new DataNameValuePair("longitude", Double.toString(longitude)));
        return data;
    }
}