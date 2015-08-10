package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.*;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.services.BuiltInSensorsProviderService;
import com.eclubprague.iot.android.driothub.ui.BuiltInSensorsListViewAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


/*
* UI class that shows data received from BuiltInSensorsProviderService
* The UI exists only for testing and debugging purposes and will be removed in the end
*/

public class MainActivity extends ActionBarActivity {

    private boolean bound = false;

    private String USERNAME = "DAT";
    private String PASSWORD = "567";

    private BuiltInSensorsProviderService builtInSensorsProviderService;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to Service, cast the IBinder and get Service instance
            BuiltInSensorsProviderService.BuiltInSensorsProviderBinder binder =
                    (BuiltInSensorsProviderService.BuiltInSensorsProviderBinder) service;

            /*
            * Pass this MainActivity instance to Service as reference so that the Service
            * can update the UI. This part of code, including UI, is not crucial for the
            * run of the Service, they serve only for testing purposes and will be removed
            * in the end.
            */
            binder.getService().initService(MainActivity.this, USERNAME, PASSWORD);

            //set Service bounded to true
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
        listView = (ListView) findViewById(R.id.builtins_list);
//        USERNAME = getIntent().getStringExtra(LoginActivity.USERNAME);
//        PASSWORD = getIntent().getStringExtra(LoginActivity.PASSWORD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the Service
        Intent intent = new Intent(this, BuiltInSensorsProviderService.class);
        bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the Service
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------
    // UI elements, will be removed in the end
    //-------------------------------------------------

    static ListView listView;

    public void actualizeListView(List<Sensor> sensors) {
        BuiltInSensorsListViewAdapter adapter = new BuiltInSensorsListViewAdapter(this,
                android.R.layout.simple_list_item_1, sensors);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        listView.invalidate();
    }

}
