package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.eclubprague.iot.android.driothub.services.BuiltInSensorsProviderService;
import com.eclubprague.iot.android.driothub.tasks.LoginTask;
import com.eclubprague.iot.android.driothub.tasks.RegisterTask;
import com.eclubprague.iot.android.driothub.tasks.UserRegistrationTask;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

/**
 * Created by Dat on 10.8.2015.
 */
public class LoginActivity extends Activity implements
        RegisterTask.TaskDelegate, UserRegistrationTask.TaskDelegate {

    private String username = "DAT";
    private String password = "567";

    private boolean loggedIn = false;

    private GitkitClient client;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BuiltInSensorsProviderService.BuiltInSensorsProviderBinder binder =
                    (BuiltInSensorsProviderService.BuiltInSensorsProviderBinder) service;
            binder.getService().initService(username, password);
            LoginActivity.this.finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    //-------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (loggedIn) {
            //TODO start settings activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        setContentView(R.layout.activity_login);

        client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
            // Implement the onSignIn method of GitkitClient.SignInCallbacks interface.
            // This method is called when the sign-in process succeeds. A Gitkit IdToken and the signed
            // in account information are passed to the callback.
            @Override
            public void onSignIn(IdToken idToken, GitkitUser user) {
                Log.i("LoginActivity", "Logged in as " + user.getDisplayName());

                //showProfilePage(idToken, user);
                // Now use the idToken to create a session for your user.
                // To do so, you should exchange the idToken for either a Session Token or Cookie
                // from your server.
                // Finally, save the Session Token or Cookie to maintain your user's session.
                startService();
            }
            // Implement the onSignInFailed method of GitkitClient.SignInCallbacks interface.
            // This method is called when the sign-in process fails.
            @Override
            public void onSignInFailed() {
                Toast.makeText(LoginActivity.this, "No such account", Toast.LENGTH_SHORT).show();
                return;
            }
        }).build();

    }

    public void signInWithGit(View view) {
        if (view.getId() == R.id.sign_in) {
            client.startSignIn();
        }
    }

    // Step 3: Override the onActivityResult method.
    // When a result is returned to this activity, it is maybe intended for GitkitClient. Call
    // GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
    // the method returns true to indicate the result has been consumed.
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (!client.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }


    }

    // Step 4: Override the onNewIntent method.
    // When the app is invoked with an intent, it is possible that the intent is for GitkitClient.
    // Call GitkitClient.handleIntent to check it. If the intent is for GitkitClient, the method
    // returns true to indicate the intent has been consumed.

    @Override
    protected void onNewIntent(Intent intent) {
        if (!client.handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    //-------------------------------------------------------------------
    //Task Delegates Overrides
    //-------------------------------------------------------------------

    @Override
    public void onRegisterCompleted(boolean success) {
        if(!success) {
            Toast.makeText(LoginActivity.this, "Such account already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        new UserRegistrationTask(this).execute(new User(username, password));
    }

    @Override
    public void onUserRegistrationTaskCompleted(boolean success) {
        if(!success) {
            Toast.makeText(LoginActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            return;
        }
        startService();
    }


    private void startService() {
        //TODO start service
        Intent intent = new Intent(this, BuiltInSensorsProviderService.class);
        startService(intent);
        bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE);
        //TODO start Settings Activity
        Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent2);
        loggedIn = true;
    }
}
