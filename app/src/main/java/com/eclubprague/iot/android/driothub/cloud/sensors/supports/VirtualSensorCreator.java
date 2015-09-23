package com.eclubprague.iot.android.driothub.cloud.sensors.supports;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.sensors.Accelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.AmbientThermometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.Barometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.Beacon;
import com.eclubprague.iot.android.driothub.cloud.sensors.ESCLed;
import com.eclubprague.iot.android.driothub.cloud.sensors.ESCThermometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.GPS;
import com.eclubprague.iot.android.driothub.cloud.sensors.GravitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Gyroscope;
import com.eclubprague.iot.android.driothub.cloud.sensors.HumiditySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.LightSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.LinearAccelerometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.Magnetometer;
import com.eclubprague.iot.android.driothub.cloud.sensors.ProximitySensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.PublicSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.RotationSensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.SensorEntity;

/**
 * Created by Dat on 12.8.2015.
 */
public class VirtualSensorCreator {

    public static Sensor createSensorInstance(String uuid, int type, String secret, Hub hub) {
        switch (type) {
            case SensorType.THERMOMETER:
                return new ESCThermometer(uuid, secret, hub);
            case SensorType.LED:
                return new ESCLed(uuid, secret, hub);
            case SensorType.GPS:
                return new GPS(uuid, secret, hub);
            case SensorType.ACCELEROMETER:
                return new Accelerometer(uuid, secret, hub);
            case SensorType.LIGHT:
                return new LightSensor(uuid, secret, hub);
            case SensorType.PROXIMITY:
                return new ProximitySensor(uuid, secret, hub);
            case SensorType.MAGNETOMETER:
                return new Magnetometer(uuid, secret, hub);
            case SensorType.GYROSCOPE:
                return new Gyroscope(uuid, secret, hub);
            case SensorType.PRESSURE:
                return new Barometer(uuid, secret, hub);
            case SensorType.GRAVITY:
                return new GravitySensor(uuid, secret, hub);
            case SensorType.LINEAR_ACCELEROMETER:
                return new LinearAccelerometer(uuid, secret, hub);
            case SensorType.ROTATION:
                return new RotationSensor(uuid, secret, hub);
            case SensorType.HUMIDITY:
                return new HumiditySensor(uuid, secret, hub);
            case SensorType.AMBIENT_THERMOMETER:
                return new AmbientThermometer(uuid, secret, hub);
            case SensorType.BEACON:
                return new Beacon(uuid, secret, hub);
            case 2:
                return new Accelerometer(uuid, secret, hub);
            default:
                return null;
        }
    }

    public static Sensor createSensorInstance(SensorEntity entity) {
        switch (entity.getType()) {
            case SensorType.BEACON:
                return new Beacon(entity);
            default:
                return new PublicSensor(entity);
        }
    }
}