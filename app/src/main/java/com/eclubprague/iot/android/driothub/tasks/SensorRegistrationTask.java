package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.SensorRegistrator;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.user.User;

import org.apache.commons.codec.binary.Base64;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

/**
 * Created by Dat on 10.8.2015.
 */
public class SensorRegistrationTask extends AsyncTask<Sensor, Void, Void> {

    private String token;
    private String email;

    public SensorRegistrationTask(String token, String email) {
        this.token = token;
        this.email = email;
    }

    @Override
    protected Void doInBackground(Sensor... sensors) {
        try {

            Log.e("TOKEN", token);
            ClientResource resource = new ClientResource("http://147.32.107.139:8080/sensor_registration");
            resource.setQueryValue("id_token", token);

            SensorRegistrator sr = resource.wrap(SensorRegistrator.class);
            sr.store(sensors[0]);
        } catch (Exception e) {
            Log.e("SensorReg", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
