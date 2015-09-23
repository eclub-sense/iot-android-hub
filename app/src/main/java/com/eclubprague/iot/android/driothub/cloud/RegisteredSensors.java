package com.eclubprague.iot.android.driothub.cloud;


import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorPaginatedCollection;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.ActionEntity;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.AllSensors;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.SensorAndData;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.ShareSensorEntity;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Created by paulos on 14. 7. 2015.
 */
public interface RegisteredSensors {


    @Get("json")
    public SensorPaginatedCollection retrieve();

    @Get("json")
    public AllSensors retrieve_AllSensors();

    @Get("json")
    public SensorAndData retrieve_SensorAndData();

    @Get("json")
    public String retrieve_test();

    @Get("json")
    public Sensor get(String uuid);

    @Get("json")
    public Sensor get();

    @Post("json")
    public void store(SensorPaginatedCollection collection);

    @Post("json")
    public void shareSensor(ShareSensorEntity message);

    @Delete
    public void remove();

    @Get
    public String getStringData();

    @Post("json")
    public void write(ActionEntity action);
}
