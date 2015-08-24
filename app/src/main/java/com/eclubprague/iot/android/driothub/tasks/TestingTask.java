package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.SensorRegistrator;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

/**
 * Created by Dat on 22.8.2015.
 */
public class TestingTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        try {
            ClientResource cr = new ClientResource("http://147.32.107.139:8080/registered_sensors");
            //?access_token="+ params[0]

            cr.setChallengeResponse(ChallengeScheme.HTTP_OAUTH, params[0], null);

            SensorRegistrator sr = cr.wrap(SensorRegistrator.class);

            return sr.retrieve_test();
        } catch (Exception e) {
            Log.e("RegedSensorsTask", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String message) {
        if(message == null) {
            Log.e("MESSAGE","NULL");
            return;
        }
        Log.e("MESSAGE", message);
    }
}
