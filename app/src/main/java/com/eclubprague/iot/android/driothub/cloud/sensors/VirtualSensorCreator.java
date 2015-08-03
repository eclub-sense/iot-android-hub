package com.eclubprague.iot.android.driothub.cloud.sensors;

/**
 * Created by Dat on 28.7.2015.
 */
public class VirtualSensorCreator {

    public static Sensor createSensorInstance(String uuid, SensorType type, String secret) {
        switch (type) {
            case THERMOMETER : return new ESCThermometer(uuid, secret);
            case LED : return new ESCLed(uuid, secret);
            case GPS : return new GPS(uuid, secret);
            default : return null;
        }
    }

}
