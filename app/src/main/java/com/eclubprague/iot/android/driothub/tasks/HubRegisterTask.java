package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;

import com.eclubprague.iot.android.driothub.cloud.gsonmods.GsonCustomConverter;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.HubRegistrator;

import org.restlet.engine.Engine;
import org.restlet.resource.ClientResource;

import java.lang.ref.WeakReference;

/**
 * Created by Dat on 3.8.2015.
 */
public class HubRegisterTask extends AsyncTask<Hub, Void, HubRegisterTask.HubRegisterResult> {
    private final WeakReference<HubRegisterCallbacks> callbacks;

    public HubRegisterTask(HubRegisterCallbacks callbacks) {
        this.callbacks = new WeakReference<HubRegisterCallbacks>(callbacks);
    }

    public HubRegisterTask() {
        callbacks = null;
    }

    public interface HubRegisterCallbacks {
        void handleHubRegistered(HubRegisterResult result);
    }

    public class HubRegisterResult {
        public HubRegisterResult(boolean success, Hub hub) {
            this.success = success;
            this.hub = hub;
        }

        boolean success;
        Hub hub;

        public boolean isSuccess() {
            return success;
        }

        public Hub getHub() {
            return hub;
        }
    }

    @Override
    protected HubRegisterResult doInBackground(Hub... hubs) {
        Engine.getInstance().getRegisteredConverters().add(new GsonCustomConverter());

        try {
            // try connection
            ClientResource cr = new ClientResource("http://192.168.201.222:8080/hub_registration");
            HubRegistrator rs = cr.wrap(HubRegistrator.class);

            rs.store(hubs[0]);
        } catch(Throwable thr) {
            thr.printStackTrace();
        }

        return new HubRegisterResult(false, hubs[0]);
    }

    @Override
    protected void onPostExecute(HubRegisterResult result) {
        super.onPostExecute(result);
//        if(callbacks == null) return;
//        HubRegisterCallbacks rsc = callbacks.get();
//        if(rsc != null) {
//            rsc.handleHubRegistered(result);
//        }
    }
}
