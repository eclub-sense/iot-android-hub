package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;

/**
 * Created by Dat on 3.8.2015.
 */
public class HubRegisterProxyTask extends AsyncTask<Hub, Void, Void> {
    @Override
    protected Void doInBackground(Hub... users) {


        try{



        } catch(Exception e) {
            Log.e("HubRegisterProxyTask:", e.toString());
        }


        return null;
    }
}
