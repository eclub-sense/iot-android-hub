package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.sensors.Accelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.ESCLed;
import com.eclubprague.iot.android.driothub.cloud.sensors.ESCThermometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.GPS;
import com.eclubprague.iot.android.driothub.cloud.sensors.LightSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.ProximitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;

/**
 * Created by Dat on 28.7.2015.
 */
public class VirtualSensorCreator {

    public static Sensor createSensorInstance(String uuid, int type, String secret) {
        switch (type) {
            case SensorType.THERMOMETER : return new ESCThermometer(uuid, secret);
            case SensorType.LED : return new ESCLed(uuid, secret);
            case SensorType.GPS : return new GPS(uuid, secret);

            case SensorType.PROXIMITY : return new ProximitySensor(uuid, secret);
            case SensorType.ACCELEROMETER : return new Accelerometer(uuid, secret);
            case SensorType.LIGHT : return new LightSensor(uuid, secret);

            default : return null;
        }
    }

}
