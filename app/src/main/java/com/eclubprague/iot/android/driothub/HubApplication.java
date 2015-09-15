package com.eclubprague.iot.android.driothub;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by paulos on 27. 8. 2015.
 */
public class HubApplication extends Application implements BootstrapNotifier {
    private static final String TAG = ".HubApplication";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.eclubprague.iot.android.driothub.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {
        // nothing yet
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        // nothing yet
    }
}
