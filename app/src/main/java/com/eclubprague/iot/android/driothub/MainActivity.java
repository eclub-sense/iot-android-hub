package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.*;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.services.LocationListenerService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.mozilla.javascript.*;
import org.mozilla.javascript.Context;

public class MainActivity extends ActionBarActivity {

    private boolean bound = false;
    private LocationListenerService locationListenerService;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationListenerService.LocationListenerBinder binder = (LocationListenerService.LocationListenerBinder) service;
            locationListenerService = binder.getService();
            binder.setServiceContext(MainActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    void doit(String code)
    {
        // Create an execution environment.
        Context cx = Context.enter();

        // Turn compilation off.
        cx.setOptimizationLevel(-1);

        try
        {
            // Initialize a variable scope with bindnings for
            // standard objects (Object, Function, etc.)
            Scriptable scope = cx.initStandardObjects();

            // Set a global variable that holds the activity instance.
            ScriptableObject.putProperty(
                    scope, "TheActivity", Context.javaToJS(this, scope));

            // Evaluate the script.
            cx.evaluateString(scope, code, "doit:", 1, null);
        }
        finally
        {
            Context.exit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LocationListenerService.class);
        //startService(intent);
        bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_get_current_gps) {
            showLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLocation() {
        String msg = "connected: " + Boolean.toString(locationListenerService.getConnected()) +
                ", latitude: " + Double.toString(locationListenerService.getLatitude()) +
                ", longitude: " + Double.toString(locationListenerService.getLongitude());

        doit(
                "var widgets = Packages.android.widget;\n" +
                        "var view = new widgets.TextView(TheActivity);\n" +
                        "TheActivity.setContentView(view);\n" +
                        "var text = '" +
                        msg +
                        "';\n" +
                        "view.append(text);"
        );

        //Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
}
