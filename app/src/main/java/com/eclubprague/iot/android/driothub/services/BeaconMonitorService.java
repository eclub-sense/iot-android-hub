package com.eclubprague.iot.android.driothub.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.eclubprague.iot.android.driothub.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by paulos on 17. 9. 2015.
 */
public class BeaconMonitorService extends Service implements RangeNotifier, BeaconConsumer {

    protected final String TAG = "BeaconMonitorService";
    protected Region region;

    private Set<Beacon> detectedBeacons = new HashSet<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        buildOngoingNotification();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // EDDYSTONE beacons
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        beaconManager.setRangeNotifier(this);
        beaconManager.bind(this);

        Log.d(TAG, "Started ranging for target region");
    }

    @Override
    public void onDestroy() {
        if(region != null) {
            try {
                BeaconManager.getInstanceForApplication(this)
                        .stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        destroyOngoingNotification();
        Log.d(TAG, "Finished ranging for target region");
    }

    @Override
    public void onBeaconServiceConnect() {
        // wake up the app when any beacon is seen (we can specify specific id filers in the parameters below)
        region = new Region("driothub.beacons", null, null, null);
        try {
            BeaconManager.getInstanceForApplication(this).setBackgroundScanPeriod(3000);
            BeaconManager.getInstanceForApplication(this).setBackgroundBetweenScanPeriod(2500);
            BeaconManager.getInstanceForApplication(this)
                    .startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        Log.d(TAG, "Got a didRangeBeaconsInRegion call");
        detectedBeacons.clear();
        for(Beacon beacon : collection) {
            Log.d(TAG, "Beacon in range: " + beacon.getBluetoothAddress() + ", " + beacon.getId1() + "," + beacon.getId2());
            detectedBeacons.add(beacon);

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction("com.eclubprague.iot.android.driothub.BeaconsAnnouncement");
            intent.putExtra("beacon_mac", beacon.getBluetoothAddress());
            intent.putExtra("beacon_rssi", beacon.getRssi());
            sendBroadcast(intent);
        }

        modifyNotificationForBeacons(detectedBeacons);
    }


    /* Notifications in the action center
    ---------------- */

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
