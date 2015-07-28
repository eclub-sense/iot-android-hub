package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class BuiltInSensor extends Sensor {

    protected List<Float> data = new ArrayList<>();

    public BuiltInSensor() {
        super();
    }
    public BuiltInSensor(int uuid, String secret) {
        super(uuid, SensorType.BUILTIN, secret);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        String res = "";
        for(int i = 0; i < data.size(); i++){
            res = res + "data_" + i + "=" + String.format("%.2f", data.get(i)) + "; ";
        }
        return res;
    }

    public List<Float> getData() {
        return data;
    }

    public void setData(List<Float> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public String toString() {
        return "BuiltIn";
        /*return "GPS [latitude=" + latitude + ", longitude=" + longitude + ", uuid=" + uuid + ", type="
                + type + ", secret=" + secret + "]";*/
    }
}
