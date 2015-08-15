package com.eclubprague.iot.android.driothub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;

import java.util.List;

/**
 * Created by Dat on 16.8.2015.
 */
public class SensorsTimerDialog extends AlertDialog.Builder {

    private Context context;
    private LinearLayout layout;

    private List<Sensor> sensors;

    private int progress;

    public SensorsTimerDialog(Context context, List<Sensor> sensors) {
        super(context);
        this.context = context;
        //this.sensorRef = sensorRef;
        this.sensors = sensors;


        layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        SeekBar seekBar = new SeekBar(this.context);
        seekBar.setMax(10);
        seekBar.setProgress(5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SensorsTimerDialog.this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (SensorsTimerDialog.this.progress == 0) {
                    Toast.makeText(SensorsTimerDialog.this.getContext(), "Stop sending data?", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SensorsTimerDialog.this.getContext(), "Send data every " +
                            SensorsTimerDialog.this.progress + " s?", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(seekBar);

        this.setView(layout);

        this.setTitle("Timer");
        this.setMessage("Set timer for all sensors");

        this.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        this.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i = 0; i < SensorsTimerDialog.this.sensors.size(); i++) {
                    SensorsTimerDialog.this.sensors.get(i).setSeconds(progress);
                    SensorsTimerDialog.this.sensors.get(i).getTimer().setPeriod(progress);
                }
                dialog.dismiss();
            }
        });

        this.create();
        this.show();
    }
}