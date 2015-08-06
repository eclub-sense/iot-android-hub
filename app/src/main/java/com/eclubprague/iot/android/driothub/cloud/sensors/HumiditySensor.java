package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorType;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 6.8.2015.
 */
public class HumiditySensor extends Sensor {

    protected String unit = "%";
    protected float humidity = 0;

    public HumiditySensor() {
        super();
    }
    public HumiditySensor(String uuid, String secret) {
        super(uuid, SensorType.HUMIDITY, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("humidity = " + humidity + " %");
    }

    public float getHumidity() {
        return humidity;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<DataNameValuePair> getDataList() {
        data.clear();
        data.add(new DataNameValuePair("humidity", Float.toString(humidity)));
        return data;
    }

    @Override
    public void setData(float[] values) {
        humidity = values[0];
    }
}