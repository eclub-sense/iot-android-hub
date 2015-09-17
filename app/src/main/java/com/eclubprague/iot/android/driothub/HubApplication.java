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
public class HubApplication extends Application {
    private static final String TAG = ".HubApplication";
    private BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }
}
