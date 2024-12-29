package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.attendance.adapter.ColoredAttendanceAdapter;
import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.android.attendance.util.SubjectManager;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewAllAttendanceActivity extends Activity {
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private TextView dateHeaderTextView;
    private Spinner spinnerBranch, spinnerYear, spinnerSubject;
    private String selectedSubject = "All Subjects";  // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_attendance);

        listView = findViewById(R.id.listview);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        dateHeaderTextView = findViewById(R.id.dateHeaderTextView);

        // Set up spinners
        setupSpinners();

        // Load initial data
        loadAttendanceData();
    }

    private void setupSpinners() {
        // Set up branch spinner
        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
            R.array.branches_array, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        // Set up year spinner
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
            R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Set up subject spinner
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add("All Subjects");  // Add default option
        subjects.addAll(SubjectManager.getSubjectsForDepartmentAndYear(
            spinnerBranch.getSelectedItem().toString(),
            spinnerYear.getSelectedItem().toString()));

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        // Add listeners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == spinnerSubject) {
                    selectedSubject = parent.getItemAtPosition(position).toString();
                } else if (parent == spinnerBranch || parent == spinnerYear) {
                    // Update subject spinner when branch or year changes
                    ArrayList<String> newSubjects = new ArrayList<>();
                    newSubjects.add("All Subjects");
                    newSubjects.addAll(SubjectManager.getSubjectsForDepartmentAndYear(
                        spinnerBranch.getSelectedItem().toString(),
                        spinnerYear.getSelectedItem().toString()));
                    
                    ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                        ViewAllAttendanceActivity.this,
                        android.R.layout.simple_spinner_item,
                        newSubjects);
                    newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(newAdapter);
                }
                loadAttendanceData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerBranch.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
        spinnerSubject.setOnItemSelectedListener(listener);
    }

    private void loadAttendanceData() {
        String branch = spinnerBranch.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();

        DBAdapter dbAdapter = new DBAdapter(this);
        ArrayList<AttendanceBean> allAttendance = dbAdapter.getAllAttendanceByBranchYear(branch, year);

        // Filter by subject if not "All Subjects"
        if (!selectedSubject.equals("All Subjects")) {
            ArrayList<AttendanceBean> filteredAttendance = new ArrayList<>();
            for (AttendanceBean attendance : allAttendance) {
                if (attendance.getSubject() != null && 
                    attendance.getSubject().equals(selectedSubject)) {
                    filteredAttendance.add(attendance);
                }
            }
            allAttendance = filteredAttendance;
        }

        // Sort by date
        Collections.sort(allAttendance, new Comparator<AttendanceBean>() {
            @Override
            public int compare(AttendanceBean a1, AttendanceBean a2) {
                int dateCompare = a1.getAttendance_session_date()
                    .compareTo(a2.getAttendance_session_date());
                if (dateCompare != 0) return dateCompare;
                return a1.getFaculty_name().compareTo(a2.getFaculty_name());
            }
        });

        ArrayList<String> attendanceList = new ArrayList<>();
        String currentDate = null;

        for (AttendanceBean attendance : allAttendance) {
            if (currentDate == null || !currentDate.equals(attendance.getAttendance_session_date())) {
                currentDate = attendance.getAttendance_session_date();
                attendanceList.add("\nDate: " + currentDate + "\n");
            }

            StudentBean student = dbAdapter.getStudentById(attendance.getAttendance_student_id());
            String attendanceInfo = String.format("%s %s (%s) | %s | %s | %s",
                student.getStudent_firstname(),
                student.getStudent_lastname(),
                student.getStudent_enrollment(),
                attendance.getFaculty_name(),
                attendance.getSubject(),
                attendance.getAttendance_status());
            attendanceList.add(attendanceInfo);
        }

        listAdapter = new ColoredAttendanceAdapter(this,
            R.layout.view_attendance_list_per_student,
            R.id.labelAttendancePerStudent,
            attendanceList);
        listView.setAdapter(listAdapter);

        if (attendanceList.isEmpty()) {
            Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
        }
    }
} 