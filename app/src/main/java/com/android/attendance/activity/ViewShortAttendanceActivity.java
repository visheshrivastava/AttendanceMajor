package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class ViewShortAttendanceActivity extends Activity {

    private ListView shortAttendanceListView;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_short_attendance);

        shortAttendanceListView = (ListView) findViewById(R.id.shortAttendanceListView);

        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");

        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DBAdapter dbAdapter = new DBAdapter(this);
        ArrayList<AttendanceBean> shortAttendanceBeanList = dbAdapter.getShortAttendanceByDateRange(startDate, endDate);

        ArrayList<String> studentList = new ArrayList<>();
        for (AttendanceBean attendanceBean : shortAttendanceBeanList) {
            StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
            float attendancePercentage = attendanceBean.getAttendance_session_id() / 100f;
            studentList.add(String.format("%s %s - %.2f%%", 
                studentBean.getStudent_firstname(), 
                studentBean.getStudent_lastname(), 
                attendancePercentage));
        }

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        shortAttendanceListView.setAdapter(listAdapter);
    }
}
