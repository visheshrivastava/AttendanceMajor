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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_faculty_registrations);

        registrationsListView = findViewById(R.id.registrationsListView);
        DBAdapter dbAdapter = new DBAdapter(this);
        
        // Ensure the table exists
        dbAdapter.ensureFacultyRegistrationTableExists();
        
        pendingRegistrations = dbAdapter.getPendingFacultyRegistrations();

        if (pendingRegistrations.isEmpty()) {
            Toast.makeText(this, "No pending registrations found", Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> registrationsList = new ArrayList<>();
        for (FacultyBean faculty : pendingRegistrations) {
            registrationsList.add(faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname() + " - " + faculty.getFaculty_username());
        }

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registrationsList);
        registrationsListView.setAdapter(listAdapter);

        registrationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showApprovalDialog(pendingRegistrations.get(position));
            }
        });
    }

    private void showApprovalDialog(final FacultyBean faculty) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewFacultyRegistrationsActivity.this);
        builder.setTitle("Approve Faculty Registration");
        builder.setMessage("Do you want to approve " + faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname() + "?");
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBAdapter dbAdapter = new DBAdapter(ViewFacultyRegistrationsActivity.this);
                dbAdapter.approveFacultyRegistration(faculty.getFaculty_id());
                Toast.makeText(ViewFacultyRegistrationsActivity.this, "Faculty approved", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
