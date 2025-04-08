package com.android.attendance.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;
import com.android.attendance.adapter.ColoredAttendanceAdapter;

public class EditAttendanceActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private String currentSession;
    private AttendanceSessionBean sessionBean;
    private DBAdapter dbAdapter;
    private ArrayList<AttendanceBean> attendanceBeanList;
    private ArrayList<StudentBean> studentBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendance_list);

        // Get session bean and current session from intent
        sessionBean = (AttendanceSessionBean) getIntent().getSerializableExtra("sessionBean");
        currentSession = getIntent().getStringExtra("session");
        
        if (sessionBean == null || currentSession == null) {
            Toast.makeText(this, "Invalid session data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        listView = findViewById(R.id.listview);
        TextView dateHeaderTextView = findViewById(R.id.dateHeaderTextView);
        dbAdapter = new DBAdapter(this);
        
        // Set header with date and subject
        dateHeaderTextView.setText(String.format(Locale.getDefault(),
            "Edit Attendance - %s\nSubject: %s",
            sessionBean.getAttendance_session_date(),
            sessionBean.getAttendance_session_subject()));

        // Get all students for this branch and year
        studentBeanList = dbAdapter.getAllStudentByBranchYear(
            sessionBean.getAttendance_session_department(),
            sessionBean.getAttendance_session_class()
        );

        // Get attendance records for this session
        attendanceBeanList = dbAdapter.getAttendanceBySessionIDAndSession(
            sessionBean.getAttendance_session_id(),
            currentSession
        );

        // Create a map of student IDs to attendance status
        ArrayList<String> attendanceList = new ArrayList<>();
        attendanceList.add("Edit Attendance\n");

        // Sort attendance records by student enrollment
        Collections.sort(attendanceBeanList, new Comparator<AttendanceBean>() {
            @Override
            public int compare(AttendanceBean a1, AttendanceBean a2) {
                return a1.getAttendance_student_id().compareTo(a2.getAttendance_student_id());
            }
        });

        // Add each attendance record
        for (AttendanceBean attendance : attendanceBeanList) {
            StudentBean student = dbAdapter.getStudentById(attendance.getAttendance_student_id());
            if (student != null) {
                String attendanceInfo = String.format(Locale.getDefault(),
                    "%s %s (%s) | %s",
                    student.getStudent_firstname(),
                    student.getStudent_lastname(),
                    student.getStudent_enrollment(),
                    attendance.getAttendance_status());

                attendanceList.add(attendanceInfo);
            }
        }

        // Set up the list adapter with color coding
        listAdapter = new ColoredAttendanceAdapter(this,
            R.layout.view_attendance_list_per_student,
            R.id.labelAttendancePerStudent,
            attendanceList);
        listView.setAdapter(listAdapter);

        // Handle item clicks to toggle attendance status
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return; // Skip header

                AttendanceBean attendance = attendanceBeanList.get(position - 1);
                String currentStatus = attendance.getAttendance_status();

                // Toggle status
                String newStatus = currentStatus.equals("P") ? "A" : "P";
                attendance.setAttendance_status(newStatus);
                
                // Update attendance record
                dbAdapter.updateAttendanceWithSession(attendance, currentSession);

                // Update the list
                StudentBean student = dbAdapter.getStudentById(attendance.getAttendance_student_id());
                String attendanceInfo = String.format(Locale.getDefault(),
                    "%s %s (%s) | %s",
                    student.getStudent_firstname(),
                    student.getStudent_lastname(),
                    student.getStudent_enrollment(),
                    newStatus);

                attendanceList.set(position, attendanceInfo);
                listAdapter.notifyDataSetChanged();
            }
        });

        // Add back button
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
