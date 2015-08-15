package com.eclubprague.iot.android.driothub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;
import com.eclubprague.iot.android.driothub.cloud.sensors.supports.DataNameValuePair;

import java.lang.ref.WeakReference;
//import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dat on 13.8.2015.
 */
public class SensorDialog extends AlertDialog.Builder {

    private Context context;
    //private ArrayList<Sensor> sensorRef;
    private Sensor sensor;
    private LinearLayout layout;

    private int progress;

    public SensorDialog(Context context, /*final ArrayList<Sensor> sensorRef*/ Sensor sensor) {
        super(context);
        this.context = context;
        //this.sensorRef = sensorRef;
        this.sensor = sensor;


        layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < /*this.sensorRef.get(0)*/this.sensor.getMeasured().size(); i++) {
            TextView tv = new TextView(this.context);
            tv.setText(/*this.sensorRef.get(0)*/this.sensor.getMeasured().get(i).getName() + " : "
                    + this./*sensorRef.get(0)*/sensor.getMeasured().get(i).getValue());
            tv.setPadding(5, 5, 5, 5);
            layout.addView(tv);
        }

        SeekBar seekBar = new SeekBar(this.context);
        seekBar.setMax(10);
        seekBar.setProgress(this./*sensorRef.get(0)*/sensor.getSeconds());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SensorDialog.this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(SensorDialog.this.progress == 0) {
                    Toast.makeText(SensorDialog.this.getContext(), "Stop sending data?", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SensorDialog.this.getContext(), "Send data every " +
                            SensorDialog.this.progress + " s?", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(seekBar);

        this.setView(layout);

        this.setTitle(this./*sensorRef.get(0)*/sensor.getStringType());
        //this.setMessage(sensor.toString());

        this.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopTimerTask();
                dialog.dismiss();
            }
        });

        this.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopTimerTask();
                /*sensorRef.get(0)*/SensorDialog.this.sensor.setSeconds(progress);
                /*sensorRef.get(0)*/SensorDialog.this.sensor.getTimer().setPeriod(progress);
                /*Toast.makeText(SensorDialog.this.getContext(),
                        "Timer: " + Integer.toString(progress), Toast.LENGTH_SHORT).show();*/
                dialog.dismiss();
            }
        });

        startTimer();
        this.create();
        this.show();
    }

    //---------------------------------------------------------------





    //----------------------------------------------------------------
    // TIMER TASK
    // DO SOME WORKS PERIODICALLY
    //----------------------------------------------------------------

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();


    private void startTimer() {
        if(timer != null) return;
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 3000ms the TimerTask will run every 2000ms
        timer.schedule(timerTask, 3000, 2000); //
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        for (int i = 0; i < SensorDialog.this./*sensorRef.get(0)*/sensor.getMeasured().size(); i++) {
                            ((TextView)(layout.getChildAt(i)))
                                    .setText(SensorDialog.this./*sensorRef.get(0)*/sensor.getMeasured().get(i).getName()
                                            + " : "
                                            + SensorDialog.this./*sensorRef.get(0)*/sensor.getMeasured().get(i).getValue());
                        }

                        layout.invalidate();

                    }
                });
            }
        };
    }
}