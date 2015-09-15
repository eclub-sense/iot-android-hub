package com.eclubprague.iot.android.driothub;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by paulos on 27. 8. 2015.
 */
public class HubApplication extends Application implements BootstrapNotifier, RangeNotifier {
    private static final String TAG = ".HubApplication";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;

    private Set<Beacon> detectedBeacons = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        buildOngoingNotification();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // EDDYSTONE beacons
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        beaconManager.setRangeNotifier(this);
        beaconManager.setBackgroundScanPeriod(5000);
        beaconManager.setBackgroundBetweenScanPeriod(3000);

        // wake up the app when any beacon is seen (we can specify specific id filers in the parameters below)
        Region region = new Region("driothub.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        Log.d(TAG, "Started monitoring for target region");
    }



    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
        try {
            Region region2 = new Region(region.getUniqueId(), region.getId1(), region.getId2(), region.getId3());
            BeaconManager.getInstanceForApplication(this).startRangingBeaconsInRegion(region2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "Got a didExitRegion call");
        try {
            BeaconManager.getInstanceForApplication(this)
                    .stopRangingBeaconsInRegion(new Region(region.getUniqueId(), region.getId1(), region.getId2(), region.getId3()));
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
        detectedBeacons.clear();
        for(Beacon beacon : collection) {
            Log.d(TAG, "Beacon in range: " + beacon.getBluetoothAddress() + ", " + beacon.getId1() + "," + beacon.getId2());
            detectedBeacons.add(beacon);
        }

        modifyNotificationForBeacons(detectedBeacons);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        destroyOngoingNotification();
    }

    public void buildOngoingNotification() {
        Notification notification = new Notification.Builder(this)
                    .setContentTitle("Beacons in range")
                    .setContentText("They will show here").setSmallIcon(R.drawable.notification_template_icon_bg)
                    .setOngoing(true)
                    .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void modifyNotificationForBeacons(Collection<Beacon> beacons) {
        StringBuilder sb = new StringBuilder();
        for(Beacon b : beacons) {
            sb.append(String.format("%.2f m - %s%n", b.getDistance(), b.getBluetoothAddress()));
        }
        if(sb.toString().isEmpty()){
            sb.append("No beacons in range");
        }

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Beacons in range")
                .setContentText(sb.toString()).setSmallIcon(R.drawable.notification_template_icon_bg)
                .setOngoing(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void destroyOngoingNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
