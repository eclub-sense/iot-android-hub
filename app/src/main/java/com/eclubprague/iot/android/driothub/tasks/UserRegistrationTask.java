package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.user.User;

import java.lang.ref.WeakReference;

/**
 * Created by Dat on 6.8.2015.
 */
public class UserRegistrationTask extends AsyncTask<User, Void, Boolean> {

    public interface TaskDelegate {
        public void onUserRegistrationTaskCompleted();
    }

    private WeakReference<TaskDelegate> delegateRef;

    public UserRegistrationTask(TaskDelegate delegate) {
        delegateRef = new WeakReference<TaskDelegate>(delegate);
    }

    @Override
    protected Boolean doInBackground(User... users) {

        if(users.length == 0) return false;

        /*
        * TODO UserRegistrationTask
        * Cloud-side not yet implemented
        */

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(!success) {
            Log.e("LOGIN","Failed to log in");
            return;
        }
        delegateRef.get().onUserRegistrationTaskCompleted();
    }
}
