package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dat on 5.8.2015.
 */
public class WSDataWrapper {


    private String type = "DATA";

    private List<SensorDataWrapper> data = new ArrayList<>();

    private transient List<Sensor> sensors;

    private String uuid;

    public WSDataWrapper(List<Sensor> sensors, String uuid) {
        this.uuid = uuid;
        this.sensors = sensors;
        for(int i = 0; i < sensors.size(); i++) {
            data.add(new SensorDataWrapper(
                    sensors.get(i)
            ));
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
