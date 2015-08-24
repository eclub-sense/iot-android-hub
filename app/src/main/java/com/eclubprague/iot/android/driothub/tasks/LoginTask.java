package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.LoginActivity;
import com.eclubprague.iot.android.driothub.cloud.RegisteredSensors;
import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.gcm.Task;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import java.lang.ref.WeakReference;

/**
 * Created by Dat on 12.8.2015.
 */
public class LoginTask extends AsyncTask<String, Void, String> {

    public interface TaskDelegate {
        public void onLoginCompleted(String token);
    }

    /*private WeakReference<TaskDelegate> delegateRef;*/
    private TaskDelegate delegate;

    public LoginTask(TaskDelegate delegate) {
        //delegateRef = new WeakReference<TaskDelegate>(delegate);
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String ... emails) {

        try {
            return GoogleAuthUtil.getToken((LoginActivity) delegate, emails[0], "audience:server:client_id:443649814858-lvc1abj9ccnudm6l199f23ddqapgo1u3.apps.googleusercontent.com");
        } catch (Exception e) {
            Log.e("RegedSensorsTask", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {
        /*delegateRef.get()*/
        if(token != null)delegate.onLoginCompleted(token);
    }
}

