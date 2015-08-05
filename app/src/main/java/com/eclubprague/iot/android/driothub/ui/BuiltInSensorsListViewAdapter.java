package com.eclubprague.iot.android.driothub.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eclubprague.iot.android.driothub.R;
import com.eclubprague.iot.android.driothub.cloud.sensors.Sensor;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class BuiltInSensorsListViewAdapter extends ArrayAdapter<Sensor> {

    public BuiltInSensorsListViewAdapter(Context context, int resource, List<Sensor> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_img_twolines_pure, parent, false);
        }

        Sensor s = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.firstLine_pure);
        textView.setText(Integer.toString(s.getType()));
        TextView textView2 = (TextView) convertView.findViewById(R.id.secondLine_pure);
        textView2.setText(s.printData());

        return convertView;
    }
}
