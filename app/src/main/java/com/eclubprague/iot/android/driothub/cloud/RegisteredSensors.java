package com.eclubprague.iot.android.driothub.cloud;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorPaginatedCollection;

import org.joda.time.DateTime;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.util.Date;

/**
 * Created by Dat on 28.7.2015.
 */
public interface RegisteredSensors {
    @Get("json")
    public SensorPaginatedCollection retrieve();

    @Get("json")
    public SensorPaginatedCollection retrieve(int type);

    @Get
    public String getStringData();

    @Get("json")
    public Sensor get(int uuid);

    @Post("json")
    public void store(SensorPaginatedCollection collection);

    @Delete
    public void remove();
}