package com.eclubprague.iot.android.driothub.cloud.sensors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dat on 28.7.2015.
 */
public class SensorInstanceCreator {

    public static Sensor createSensorInstance(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JSONObject jsonObject;
        Object type = null;
        try {
            jsonObject = new JSONObject(json);
            type = jsonObject.get("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch ((String)type) {
            case "THERMOMETER" :
                return gson.fromJson(json, ESCThermometer.class);
            case "LED" :
                return gson.fromJson(json, ESCLed.class);
            case "GPS" :
                return gson.fromJson(json, GPS.class);
            default :
                return null;
        }
    }
}
