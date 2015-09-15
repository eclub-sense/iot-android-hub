package com.eclubprague.iot.android.driothub;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

/**
 * Created by paulos on 27. 8. 2015.
 */
public class HubApplication extends Application implements BootstrapNotifier, RangeNotifier {
    private static final String TAG = ".HubApplication";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // EDDYSTONE beacons
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        beaconManager.setRangeNotifier(this);
        beaconManager.setForegroundScanPeriod(5000);
        beaconManager.setBackgroundScanPeriod(5000);
        beaconManager.setBackgroundBetweenScanPeriod(3000);
        beaconManager.setForegroundBetweenScanPeriod(3000);

        Region region = new Region("com.eclubprague.iot.android.driothub.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region region) {
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        try {
            BeaconManager.getInstanceForApplication(this).startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        try {
            BeaconManager.getInstanceForApplication(this).stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        // nothing yet
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        Log.d(TAG, "Got a didRangeBeaconsInRegion call");
        for(Beacon beacon : collection) {
            Log.d(TAG, "Beacon in range: " + beacon.getBluetoothAddress() + ", " + beacon.getId1() + "," + beacon.getId2());
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
