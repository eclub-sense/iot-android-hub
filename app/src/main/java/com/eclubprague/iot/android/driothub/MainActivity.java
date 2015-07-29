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
import com.eclubprague.iot.android.driothub.services.LocationListenerService;
import com.eclubprague.iot.android.driothub.ui.BuiltInSensorsListViewAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import io.jxcore.node.jxcore;

public class MainActivity extends ActionBarActivity {

    private boolean bound = false;
    //private LocationListenerService locationListenerService;

    private BuiltInSensorsProviderService builtInSensorsProviderService;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BuiltInSensorsProviderService.BuiltInSensorsProviderBinder binder =
                    (BuiltInSensorsProviderService.BuiltInSensorsProviderBinder) service;
            List<MainActivity> activityList = new ArrayList<>();
            activityList.add(MainActivity.this);
            //binder.setServiceActivity(activityList);
            /*builtInSensorsProviderService = */binder.getService().initBuiltInSensorsCollection(activityList);
//            binder.setServiceContext(MainActivity.this);
//            List<MainActivity> activityList = new ArrayList<>();
//            activityList.add(MainActivity.this);
//            binder.setServiceActivity(activityList);
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

        // Tak to zkusme.
        jxcore.Prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BuiltInSensorsProviderService.class);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------
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
