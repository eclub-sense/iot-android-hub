package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.SensorEntity;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by Dat on 25.8.2015.
 */
public class PublicSensor extends Sensor {

    public PublicSensor(String uuid, int type, String secret, Hub hub) {
        super(uuid, type, secret, hub);
    }

    public PublicSensor(SensorEntity entity) {
        super(entity);
    }

    @Override
    public void readPayload(byte[] data) {

    }

    @Override
    public String printData() {
        return null;
    }

    @Override
    public List<DataNameValuePair> getMeasured() {
        return null;
    }

    public List<NameValuePair> getDataList() {
        return null;
    }

    @Override
    public void setData(float[] values) {

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
