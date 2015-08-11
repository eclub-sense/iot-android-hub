package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;

import com.eclubprague.iot.android.driothub.cloud.SensorRegistrator;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

/**
 * Created by Dat on 10.8.2015.
 */
public class SensorRegistrationTask extends AsyncTask<Sensor, Void, Void> {

    private User user;
    public SensorRegistrationTask(User user) {
        this.user = user;
    }

    @Override
    protected Void doInBackground(Sensor... sensors) {
        ClientResource cr = new ClientResource("http://147.32.107.139:8080/sensor_registration");
        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user.getUsername(), user.getPassword());
        SensorRegistrator sr = cr.wrap(SensorRegistrator.class);
        sr.store(sensors[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
