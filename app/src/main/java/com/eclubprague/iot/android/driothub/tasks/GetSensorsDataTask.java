package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.RegisteredSensors;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.cloud_entities.AllSensors;

import org.restlet.resource.ClientResource;

import java.util.ArrayList;

/**
 * Created by Dat on 10.8.2015.
 */
public class GetSensorsDataTask extends AsyncTask<Integer, Void, AllSensors> {

    public interface TaskDelegate {
        public void onGetSensorsDataTaskCompleted(AllSensors message);
    }

    private ArrayList<TaskDelegate> delegateRef;
    private String token;
    public GetSensorsDataTask(ArrayList<TaskDelegate> delegateRef, String token) {
        this.delegateRef = delegateRef;
        this.token = token;

    }

    @Override
    protected AllSensors doInBackground(Integer... params) {

        try {
            ClientResource resource = new ClientResource("http://mlha-139.sin.cvut.cz:8080/registered_sensors");
            resource.setQueryValue("access_token", token);

            RegisteredSensors sr = resource.wrap(RegisteredSensors.class);
            return sr.retrieve_AllSensors(params[0]);
        } catch (Exception e) {
            Log.e("RegedSensorsTask", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(AllSensors message) {
        if(message == null) {
            Log.e("MESSAGE","NULL");
            return;
        }
        Log.e("allsensors", message.toString());
        delegateRef.get(0).onGetSensorsDataTaskCompleted(message);
    }
}
