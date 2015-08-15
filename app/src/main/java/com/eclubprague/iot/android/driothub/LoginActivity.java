package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.user.User;
import com.eclubprague.iot.android.driothub.services.BuiltInSensorsProviderService;
import com.eclubprague.iot.android.driothub.tasks.LoginTask;
import com.eclubprague.iot.android.driothub.tasks.RegisterTask;
import com.eclubprague.iot.android.driothub.tasks.UserRegistrationTask;

/**
 * Created by Dat on 10.8.2015.
 */
public class LoginActivity extends Activity implements LoginTask.TaskDelegate,
        RegisterTask.TaskDelegate, UserRegistrationTask.TaskDelegate {

    private EditText un,pw;
    private Button b_login;
    private Button b_register;

    private String username = "DAT";
    private String password = "567";

    private boolean loggedIn = false;

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
        if(loggedIn) {
            //TODO start settings activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        un=(EditText)findViewById(R.id.et_un);
        pw=(EditText)findViewById(R.id.et_pw);
        b_login =(Button)findViewById(R.id.btn_login);
        b_register =(Button)findViewById(R.id.btn_register);

        b_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                username = un.getText().toString();
                password = pw.getText().toString();

                if(username.length() < 3 || password.length() < 3) {
                    Toast.makeText(LoginActivity.this, "Credentials min. lenght: 3", Toast.LENGTH_SHORT).show();
                    return;
                }

                new LoginTask(LoginActivity.this).execute(new User(username, password));
            }
        });

        b_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Not yet supported", Toast.LENGTH_SHORT).show();
                /*username = un.getText().toString();
                password = pw.getText().toString();

                if(username.length() < 3 || password.length() < 3) {
                    Toast.makeText(LoginActivity.this, "Credentials min. lenght: 3", Toast.LENGTH_SHORT).show();
                    return;
                }

                new RegisterTask(LoginActivity.this).execute(new User(username, password));*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }





    //-------------------------------------------------------------------
    //Task Delegates Overrides
    //-------------------------------------------------------------------


    @Override
    public void onLoginCompleted(boolean success) {
        if(!success) {
            Toast.makeText(this, "No such account", Toast.LENGTH_SHORT).show();
            return;
        }
        startService();
    }

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
