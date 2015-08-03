package com.eclubprague.iot.android.driothub.cloud;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Created by Dat on 3.8.2015.
 */
public interface HubRegistrator {
    @Get("json")
    public Hub retrieve();

    @Post("json")
    public void store(Hub hub);

    @Delete
    public void remove();
}