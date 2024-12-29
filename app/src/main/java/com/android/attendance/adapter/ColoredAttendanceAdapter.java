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
        
        // Headers and summary section should be black
        if (text.startsWith("Date:") || 
            text.startsWith("Attendance Summary") || 
            text.startsWith("\nDetailed Attendance")) {
            view.setText(text);
            view.setTextColor(Color.BLACK);
            view.setTextSize(16);
            return view;
        }

        // Split the text at the last pipe symbol
        int lastPipeIndex = text.lastIndexOf("|");
        if (lastPipeIndex != -1) {
            String info = text.substring(0, lastPipeIndex + 1);
            String status = text.substring(lastPipeIndex + 1).trim();
            
            // If it contains percentage, it's a summary line - keep it black
            if (status.contains("%")) {
                view.setText(text);
                view.setTextColor(Color.BLACK);
                return view;
            }
            
            // Otherwise it's a detailed attendance entry - apply color
            view.setText(info + " ");
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