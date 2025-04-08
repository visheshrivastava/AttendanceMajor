package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.db.DBAdapter;
import com.android.attendance.util.SubjectManager;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;
import java.util.List;

public class AssignFacultySubjectActivity extends Activity {
    private Spinner facultySpinner, departmentSpinner, yearSpinner, subjectSpinner;
    private Button assignButton;
    private DBAdapter dbAdapter;
    private ArrayList<FacultyBean> facultyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_faculty_subject);

        dbAdapter = new DBAdapter(this);
        facultySpinner = findViewById(R.id.facultySpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        assignButton = findViewById(R.id.assignButton);

        // Load faculty list
        facultyList = dbAdapter.getAllFaculty();
        ArrayList<String> facultyNames = new ArrayList<>();
        for (FacultyBean faculty : facultyList) {
            facultyNames.add(faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname());
        }
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facultyNames);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyAdapter);

        // Set up department spinner
        String[] departments = {"IT"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        // Set up year spinner
        String[] years = {"2Y", "3Y"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Update subject spinner when department or year changes
        departmentSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateSubjectSpinner();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        yearSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateSubjectSpinner();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Initial subject spinner update
        updateSubjectSpinner();

        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignSubjectToFaculty();
            }
        });
    }

    private void updateSubjectSpinner() {
        String department = departmentSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();
        
        List<String> subjects = SubjectManager.getSubjectsForDepartmentAndYear(department, year);
        
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);
    }

    private void assignSubjectToFaculty() {
        int selectedPosition = facultySpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= facultyList.size()) {
            Toast.makeText(this, "Please select a faculty member", Toast.LENGTH_SHORT).show();
            return;
        }

        FacultyBean selectedFaculty = facultyList.get(selectedPosition);
        String department = departmentSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();
        String subject = subjectSpinner.getSelectedItem().toString();

        boolean success = dbAdapter.assignFacultySubject(selectedFaculty.getFaculty_id(), year, subject);
        if (success) {
            Toast.makeText(this, "Subject and year assigned successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to assign subject and year", Toast.LENGTH_SHORT).show();
        }
    }
} 