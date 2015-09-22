package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;

import com.eclubprague.iot.android.driothub.cloud.RegisteredSensors;
import com.eclubprague.iot.android.driothub.cloud.gsonmods.GsonCustomConverter;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.SensorPaginatedCollection;

import org.restlet.data.ChallengeScheme;
import org.restlet.engine.Engine;
import org.restlet.resource.ClientResource;

import java.lang.ref.WeakReference;

/**
 * Refreshes sensor list from the server based on hub ID.
 */
public class RefreshSensorsTask extends AsyncTask<Integer, Void, SensorPaginatedCollection> {
    private final WeakReference<RefreshSensorsCallbacks> callbacks;

    private String USERNAME;
    private String PASSWORD;

    public RefreshSensorsTask(RefreshSensorsCallbacks callbacks, String username, String password) {
        this.callbacks = new WeakReference<RefreshSensorsCallbacks>(callbacks);
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    public interface RefreshSensorsCallbacks {
        void handleSensorsRefreshed(String hubId, SensorPaginatedCollection sensorsCollection);
        void handleSensorsRefreshFailed(String hubId);
    }

    @Override
    protected SensorPaginatedCollection doInBackground(Integer... types) {
        // TODO: actually use the hub ID

        Engine.getInstance().getRegisteredConverters().clear();
        Engine.getInstance().getRegisteredConverters().add(new GsonCustomConverter());

        try {
            // try connection
            ClientResource cr = new ClientResource("http://147.32.107.139:8080/registered_sensors");
            cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, USERNAME, PASSWORD);
            RegisteredSensors rs = cr.wrap(RegisteredSensors.class);

            return rs.retrieve(types[0]);
        } catch(Throwable thr) {
            thr.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(SensorPaginatedCollection sensorPaginatedCollection) {
        super.onPostExecute(sensorPaginatedCollection);

        RefreshSensorsCallbacks rsc = callbacks.get();
        if(rsc != null) {
            if(sensorPaginatedCollection != null)
                rsc.handleSensorsRefreshed(null, sensorPaginatedCollection);
            else
                rsc.handleSensorsRefreshFailed(null);
        }
    }
}
