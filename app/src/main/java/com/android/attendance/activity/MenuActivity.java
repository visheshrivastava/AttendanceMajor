package com.android.attendance.activity;

import java.util.ArrayList;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MenuActivity extends Activity {

	Button addStudent;
	Button addFaculty;
	Button viewStudent;
	Button viewFaculty;
	Button logout;
	Button attendancePerStudent;

	String userRole;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		addStudent = (Button)findViewById(R.id.buttonaddstudent);
		addFaculty = (Button)findViewById(R.id.buttonaddfaculty);
		viewStudent = (Button)findViewById(R.id.buttonViewstudent);
		viewFaculty = (Button)findViewById(R.id.buttonviewfaculty);
		logout = (Button)findViewById(R.id.buttonlogout);
		attendancePerStudent = (Button)findViewById(R.id.attendancePerStudentButton);

		// Get user role from intent
		userRole = getIntent().getStringExtra("role");

		// Configure visibility based on role
		if ("admin".equals(userRole)) {
			// Admin panel configuration
			addStudent.setVisibility(View.GONE);
			addFaculty.setVisibility(View.GONE);
			attendancePerStudent.setVisibility(View.GONE);
			
			// Show faculty registrations button for admin
			Button viewFacultyRegistrationsButton = findViewById(R.id.viewFacultyRegistrationsButton);
			if (viewFacultyRegistrationsButton != null) {
				viewFacultyRegistrationsButton.setVisibility(View.VISIBLE);
				viewFacultyRegistrationsButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this, ViewFacultyRegistrationsActivity.class);
						startActivity(intent);
					}
				});
			}

			Button viewAllAttendanceButton = (Button) findViewById(R.id.viewAllAttendanceButton);
			if (viewAllAttendanceButton != null) {
				viewAllAttendanceButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this, ViewAllAttendanceActivity.class);
						startActivity(intent);
					}
				});
			}
		} else {
			// Faculty panel configuration
			addFaculty.setVisibility(View.GONE);
			viewFaculty.setVisibility(View.GONE);
			Button viewFacultyRegistrationsButton = findViewById(R.id.viewFacultyRegistrationsButton);
			if (viewFacultyRegistrationsButton != null) {
				viewFacultyRegistrationsButton.setVisibility(View.GONE);
			}
		}

		// Set up click listeners
		addStudent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, AddStudentActivity.class);
				startActivity(intent);
			}
		});
		
		viewStudent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, ViewStudentActivity.class);
				startActivity(intent);
			}
		});

		viewFaculty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, ViewFacultyActivity.class);
				startActivity(intent);
			}
		});

		attendancePerStudent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DBAdapter dbAdapter = new DBAdapter(MenuActivity.this);
				ArrayList<AttendanceBean> attendanceBeanList = dbAdapter.getAllAttendanceByStudent();
				((ApplicationContext)MenuActivity.this.getApplicationContext()).setAttendanceBeanList(attendanceBeanList);
				Intent intent = new Intent(MenuActivity.this, ViewAttendancePerStudentActivity.class);
				startActivity(intent);
			}
		});

		// Logout button click listener
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutConfirmationDialog();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// Don't do anything when back is pressed
		// This prevents accidental logout
	}

	private void showLogoutConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Logout");
		builder.setMessage("Are you sure you want to logout?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Perform logout
				Intent intent = new Intent(MenuActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
							  Intent.FLAG_ACTIVITY_NEW_TASK | 
							  Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}

}
