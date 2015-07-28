package com.eclubprague.iot.android.driothub.cloud;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Created by Dat on 28.7.2015.
 */
public interface SensorRegistrator {
    @Get("json")
    public Sensor retrieve();

    @Post("json")
    public void store(Sensor sensor);

    @Delete
    public void remove();
}