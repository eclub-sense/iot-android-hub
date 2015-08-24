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
            byte[] valueDecoded= Base64.decodeBase64(token.getBytes());

            String tokenString = new String(valueDecoded, "UTF-8");

            Log.e("TOKEN", token);
            Log.e("TOKENSTRING", tokenString);

            //ClientResource cr = new ClientResource("http://147.32.107.139:8080/sensor_registration?access_token="
            //+ token);
            ClientResource resource = new ClientResource("http://147.32.107.139:8080/sensor_registration");

            //ChallengeResponse response = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
            //response.setIdentifier(email);
            //response.setRawValue(token);

            //cr.setChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER, email, null);
            //resource.setChallengeResponse(response);
            resource.setQueryValue("id_token", token);

            SensorRegistrator sr = resource.wrap(SensorRegistrator.class);
            //Log.e("LOG",sr.retrieve_test());
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
