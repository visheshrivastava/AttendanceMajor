package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;
import com.android.attendance.adapter.ColoredAttendanceAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EditAttendanceActivity extends Activity {

    private ListView attendanceListView;
    private ArrayAdapter<String> listAdapter;
    private Button updateAttendanceButton;
    private AttendanceSessionBean sessionBean;
    private ArrayList<StudentBean> studentBeanList;
    private ArrayList<AttendanceBean> attendanceBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);

        attendanceListView = findViewById(R.id.attendanceListView);
        updateAttendanceButton = findViewById(R.id.updateAttendanceButton);
        
        // Get the session bean from intent
        sessionBean = (AttendanceSessionBean) getIntent().getSerializableExtra("sessionBean");
        if (sessionBean == null) {
            Toast.makeText(this, "Error: No session information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get attendance records for this session
        DBAdapter dbAdapter = new DBAdapter(this);
        attendanceBeanList = dbAdapter.getAttendanceBySessionID(sessionBean);
        
        // Get student list
        studentBeanList = new ArrayList<>();
        for (AttendanceBean attendance : attendanceBeanList) {
            StudentBean student = dbAdapter.getStudentById(attendance.getAttendance_student_id());
            studentBeanList.add(student);
        }

        // Sort studentBeanList by enrollment number
        Collections.sort(studentBeanList, new Comparator<StudentBean>() {
            @Override
            public int compare(StudentBean s1, StudentBean s2) {
                String e1 = s1.getStudent_enrollment() != null ? s1.getStudent_enrollment() : "";
                String e2 = s2.getStudent_enrollment() != null ? s2.getStudent_enrollment() : "";
                return e1.compareTo(e2);
            }
        });

        // Create list items with checkboxes
        ArrayList<String> studentList = new ArrayList<>();
        for (int i = 0; i < studentBeanList.size(); i++) {
            StudentBean student = studentBeanList.get(i);
            AttendanceBean attendance = attendanceBeanList.get(i);
            String status = attendance.getAttendance_status();
            String studentInfo = String.format("%s %s (%s) | %s",
                student.getStudent_firstname(),
                student.getStudent_lastname(),
                student.getStudent_enrollment(),
                status);
            studentList.add(studentInfo);
        }

        // Set up ListView with multiple choice
        listAdapter = new ColoredAttendanceAdapter(this, 
            android.R.layout.simple_list_item_multiple_choice,
            android.R.id.text1,
            studentList);
        attendanceListView.setAdapter(listAdapter);
        attendanceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set initial checkbox states
        for (int i = 0; i < attendanceBeanList.size(); i++) {
            attendanceListView.setItemChecked(i, 
                attendanceBeanList.get(i).getAttendance_status().equals("P"));
        }

        // Handle update button click
        updateAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAdapter dbAdapter = new DBAdapter(EditAttendanceActivity.this);
                
                // Update attendance status for each student
                for (int i = 0; i < studentBeanList.size(); i++) {
                    AttendanceBean attendance = attendanceBeanList.get(i);
                    attendance.setAttendance_status(attendanceListView.isItemChecked(i) ? "P" : "A");
                    dbAdapter.updateAttendance(attendance);
                }

                Toast.makeText(EditAttendanceActivity.this, 
                    "Attendance updated successfully", 
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
