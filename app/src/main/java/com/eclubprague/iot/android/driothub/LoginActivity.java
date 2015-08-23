package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.services.BuiltInSensorsProviderService;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

/**
 * Created by Dat on 10.8.2015.
 */
public class LoginActivity extends Activity  {

    private String token = "";
    private String email = "";

    private GitkitClient client;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BuiltInSensorsProviderService.BuiltInSensorsProviderBinder binder =
                    (BuiltInSensorsProviderService.BuiltInSensorsProviderBinder) service;
            binder.getService().initService(token, email);
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
        setContentView(R.layout.activity_login);

        client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {

            @Override
            public void onSignIn(IdToken idToken, GitkitUser user) {
                Log.i("LoginActivity", "Logged in as " + user.getDisplayName());
                token = idToken.getTokenString();
                Log.i("TOKEN", token);
                email = idToken.getEmail();
                Log.i("EMAIL", email);

                startService();
            }

            @Override
            public void onSignInFailed() {
                Toast.makeText(LoginActivity.this, "SIGN IN FAILED", Toast.LENGTH_SHORT).show();
                return;
            }
        }).build();

    }

    public void signInWithGit(View view) {
        if (view.getId() == R.id.sign_in) {
            client.startSignIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (!client.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!client.handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    private void startService() {
        //TODO start service
        Intent intent = new Intent(this, BuiltInSensorsProviderService.class);
        intent.putExtra("token", token);
        intent.putExtra("email", email);
        startService(intent);
        bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE);
        //TODO start Settings Activity
        Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent2);
        //this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}