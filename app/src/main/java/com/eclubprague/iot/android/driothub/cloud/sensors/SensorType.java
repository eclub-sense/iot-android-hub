package com.eclubprague.iot.android.driothub.cloud.sensors;

/**
 * Created by Dat on 28.7.2015.
 */
public enum SensorType {

    THERMOMETER(0x41, "thermometer"), // 0x41
    LED(0x1, "led"),
    GPS(0x2, "gps"),
    ACCELEROMETER(0x4, "accelerometer"),
    LIGHT(0x5, "light_sensor"),
    PROXIMITY(0x6, "proximity_sensor"),
    BUILTIN(0X3, "builtin");

    private final int code;
    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private SensorType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
