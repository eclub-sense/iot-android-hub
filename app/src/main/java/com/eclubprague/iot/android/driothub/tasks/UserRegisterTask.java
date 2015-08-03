package com.eclubprague.iot.android.driothub.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.eclubprague.iot.android.driothub.cloud.UserRegistrator;
import com.eclubprague.iot.android.driothub.cloud.gsonmods.GsonCustomConverter;
import com.eclubprague.iot.android.driothub.cloud.hubs.Hub;
import com.eclubprague.iot.android.driothub.cloud.user.User;

import org.restlet.engine.Engine;
import org.restlet.resource.ClientResource;

import java.lang.ref.WeakReference;

/**
 * Created by Dat on 3.8.2015.
 */
public class UserRegisterTask extends AsyncTask<User, Void, UserRegisterTask.UserRegisterResult> {
    private final WeakReference<UserRegisterCallbacks> callbacks;

    public UserRegisterTask(UserRegisterCallbacks callbacks) {
        this.callbacks = new WeakReference<UserRegisterCallbacks>(callbacks);
    }

    public interface UserRegisterCallbacks {
        void handleUserRegistered(UserRegisterResult result);
    }

    public class UserRegisterResult {
        public UserRegisterResult(boolean success, User user) {
            this.success = success;
            this.user = user;
        }

        boolean success;
        User user;

        public boolean isSuccess() {
            return success;
        }

        public User getUser() {
            return user;
        }
    }

    @Override
    protected UserRegisterResult doInBackground(User... users) {
        Engine.getInstance().getRegisteredConverters().add(new GsonCustomConverter());

        try {
            // try connection
            ClientResource cr = new ClientResource("http://192.168.201.222:8080/user_registration");
            UserRegistrator rs = cr.wrap(UserRegistrator.class);

            rs.store(users[0]);
        } catch(Throwable thr) {
            thr.printStackTrace();
        }

        return new UserRegisterResult(false, users[0]);
    }

    @Override
    protected void onPostExecute(UserRegisterResult result) {
        super.onPostExecute(result);

        UserRegisterCallbacks rsc = callbacks.get();
        if(rsc != null) {
            rsc.handleUserRegistered(result);
            Log.e("UserTask:", result.getUser().toString());
            new HubRegisterTask().execute(new Hub("2309", result.getUser()));
        }
    }
}
