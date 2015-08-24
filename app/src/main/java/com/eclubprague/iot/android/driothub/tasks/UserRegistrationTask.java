package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.UserRegistrator;
import com.eclubprague.iot.android.driothub.cloud.user.User;

import org.restlet.resource.ClientResource;

import java.lang.ref.WeakReference;

/**
 * Created by Dat on 6.8.2015.
 */
public class UserRegistrationTask extends AsyncTask<User, Void, Boolean> {

    public interface TaskDelegate {
        public void onUserRegistrationTaskCompleted(boolean success);
    }

    //private WeakReference<TaskDelegate> delegateRef;
    private TaskDelegate delegate;

    public UserRegistrationTask(TaskDelegate delegate) {
        //delegateRef = new WeakReference<TaskDelegate>(delegate);
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(User... users) {

        if(users.length == 0) return false;

        /*
        * TODO UserRegistrationTask
        * Cloud-side not yet implemented
        */
        try {
            ClientResource cr = new ClientResource("http://147.32.107.139:8080/user_registration");
            UserRegistrator sr = cr.wrap(UserRegistrator.class);
            sr.store(users[0]);
        } catch (Exception e) {
            Log.e("UserRegTask", e.toString());
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        /*delegateRef.get()*/delegate.onUserRegistrationTaskCompleted(success);
    }
}
