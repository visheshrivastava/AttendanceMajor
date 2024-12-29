package com.android.attendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class ViewFacultyRegistrationsActivity extends Activity {
    private ListView registrationsListView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<FacultyBean> pendingRegistrations;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_faculty_registrations);

        registrationsListView = findViewById(R.id.registrationsListView);
        dbAdapter = new DBAdapter(this);
        
        loadPendingRegistrations();

        registrationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FacultyBean selectedFaculty = pendingRegistrations.get(position);
                showApprovalDialog(selectedFaculty);
            }
        });
    }

    private void loadPendingRegistrations() {
        pendingRegistrations = dbAdapter.getPendingFacultyRegistrations();

        if (pendingRegistrations.isEmpty()) {
            Toast.makeText(this, "No pending registrations found", Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> registrationsList = new ArrayList<>();
        for (FacultyBean faculty : pendingRegistrations) {
            String facultyInfo = faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname() + 
                                 " - " + faculty.getFaculty_subject();
            registrationsList.add(facultyInfo);
        }

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registrationsList);
        registrationsListView.setAdapter(listAdapter);
    }

    private void showApprovalDialog(final FacultyBean faculty) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Approve Faculty Registration");
        builder.setMessage("Do you want to approve " + faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname() + "?");
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                approveFacultyRegistration(faculty);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void approveFacultyRegistration(FacultyBean faculty) {
        boolean success = dbAdapter.approveFacultyRegistration(faculty.getFaculty_id());
        if (success) {
            Toast.makeText(this, "Faculty approved successfully", Toast.LENGTH_SHORT).show();
            loadPendingRegistrations(); // Reload the list
        } else {
            Toast.makeText(this, "Failed to approve faculty", Toast.LENGTH_SHORT).show();
        }
    }
}
