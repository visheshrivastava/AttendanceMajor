package com.android.attendance.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ColoredAttendanceAdapter extends ArrayAdapter<String> {
    public ColoredAttendanceAdapter(Context context, int resource, int textViewResourceId, List<String> items) {
        super(context, resource, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        String text = getItem(position);
        
        if (text.contains("Date:") || text.contains("Subject:")) {
            view.setText(text);
            view.setTextColor(Color.BLACK);
            view.setTextSize(16);
            return view;
        }

        String[] parts = text.split(" \\| ");
        if (parts.length == 2) {
            String studentInfo = parts[0];
            String status = parts[1].trim();

            view.setText(studentInfo + " | ");
            if (status.equals("P")) {
                view.append("P");
                view.setTextColor(Color.rgb(0, 150, 0));  // Dark Green
            } else if (status.equals("A")) {
                view.append("A");
                view.setTextColor(Color.rgb(200, 0, 0));  // Dark Red
            }
        } else {
            view.setText(text);
            view.setTextColor(Color.BLACK);
        }
        
        return view;
    }
} 