package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.services.BeaconMonitorService;
import com.eclubprague.iot.android.driothub.services.BuiltInSensorsProviderService;
import com.eclubprague.iot.android.driothub.ui.BuiltInSensorsListViewAdapter;
import com.eclubprague.iot.android.driothub.ui.SensorDialog;
import com.eclubprague.iot.android.driothub.ui.SensorsTimerDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
* UI class that shows data received from BuiltInSensorsProviderService
* The UI exists only for testing and debugging purposes and will be removed in the end
*/

public class MainActivity extends ActionBarActivity {

    private BuiltInSensorsProviderService builtInSensorsProviderService;

    private String email;
    private String accessToken;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to Service, cast the IBinder and get Service instance
            BuiltInSensorsProviderService.BuiltInSensorsProviderBinder binder =
                    (BuiltInSensorsProviderService.BuiltInSensorsProviderBinder) service;

            binder.getService().initService(accessToken, email);

            /*
            * Pass this MainActivity instance to Service as reference so that the Service
            * can update the UI. This part of code, including UI, is not crucial for the
            * run of the Service, they serve only for testing purposes and will be removed
            * in the end.
            */
            MainActivity.this.builtInSensorsProviderService = binder.getService();
            MainActivity.this.showSensors();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        this.accessToken = this.getIntent().getStringExtra("token");
        this.email = this.getIntent().getStringExtra("email");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the Service
        Intent intent = new Intent(this, BuiltInSensorsProviderService.class);
        intent.putExtra("token", accessToken);
        intent.putExtra("email", email);
        bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE);
        startService(intent);

        // Beacon service
        Intent intent2 = new Intent(this, BeaconMonitorService.class);
        intent2.putExtra("token", accessToken);
        intent2.putExtra("email", email);
        //bindService(intent2, null, android.content.Context.BIND_AUTO_CREATE);
        startService(intent2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the Service
//        if (bound) {
//            unbindService(connection);
//            bound = false;
//        }
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

        if(id == R.id.action_stop_service) {
            try {
                unbindService(connection);
                builtInSensorsProviderService.stopSelf();
            } catch (Exception e) {
                Log.e("STOP",e.toString());
            }
            this.finish();
            return true;
        }

        if(id == R.id.action_register_sensors) {
            builtInSensorsProviderService.registerSensors();
        }

        if(id == R.id.action_test) {
            builtInSensorsProviderService.test();
        }

        if(id == R.id.action_timer) {
            //Toast.makeText(this, "Set update interval for all sensors: not yet implemented", Toast.LENGTH_SHORT).show();
            try{
                new SensorsTimerDialog(this, builtInSensorsProviderService.getBuiltInSensors());
            }catch (Exception e) {
                Log.e("SensorsTimer", e.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------

    private ListView listView;

    private void showSensors() {
        listView = (ListView)findViewById(R.id.builtins_list);
        BuiltInSensorsListViewAdapter adapter = new BuiltInSensorsListViewAdapter(
                this, android.R.layout.simple_list_item_1, builtInSensorsProviderService.getBuiltInSensors()
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            /*ArrayList<Sensor> sensorRef = new ArrayList<>();
            sensorRef.add((Sensor) parent.getAdapter().getItem(position));*/
                //new BuiltInSensorInfoDialog(rootView.getContext(), (Sensor) parent.getAdapter().getItem(position));

                new SensorDialog(MainActivity.this, /*sensorRef*/ (Sensor) parent.getAdapter().getItem(position));
            }
        });
    }

    //----------------------------------------------------------------

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }



}
