package com.eclubprague.iot.android.driothub.cloud;

import com.eclubprague.iot.android.driothub.cloud.user.User;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * Created by Dat on 3.8.2015.
 */
public interface UserRegistrator {
    @Get("json")
    public User retrieve();

    @Post("json")
    public void store(User user);

    @Delete
    public void remove();
}