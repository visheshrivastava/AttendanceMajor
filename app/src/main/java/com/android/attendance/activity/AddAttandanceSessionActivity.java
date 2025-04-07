package com.android.attendance.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.FacultyBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;
import com.android.attendance.util.SubjectManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AddAttandanceSessionActivity extends Activity {

	private ImageButton date;
	private Calendar cal;
	private int day;
	private int month;
	private int dyear;
	private EditText dateEditText;
	Button submit;
	Button viewAttendance;
	Button viewTotalAttendance;
	Spinner spinnerbranch,spinneryear,spinnerSubject;
	String branch = "IT";
	String year = "1Y";
	String subject = null;

	private String[] branchString = new String[] { "IT"};
	private String[] yearString = new String[] {"1Y","2Y","3Y","4Y"};

	AttendanceSessionBean attendanceSessionBean;

	// Add these as class variables
	private Button viewShortAttendance;
	private EditText startDateEditText;
	private EditText endDateEditText;

	private static final int READ_REQUEST_CODE = 42;

	private TextView startDateLabel;
	private TextView endDateLabel;

	private Button viewShortAttendanceButton;

	private String currentSession;

	private Button logoutButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_attandance);

		// Get the current session from intent
		currentSession = getIntent().getStringExtra("session");
		if (currentSession == null) {
			Toast.makeText(this, "No session selected", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// Initialize UI elements
		spinnerbranch = (Spinner)findViewById(R.id.spinner1);
		spinneryear = (Spinner)findViewById(R.id.spinneryear);
		spinnerSubject = (Spinner)findViewById(R.id.spinnerSE);
		date = (ImageButton) findViewById(R.id.DateImageButton);
		dateEditText = (EditText) findViewById(R.id.DateEditText);
		submit = (Button)findViewById(R.id.buttonsubmit);
		viewAttendance = (Button)findViewById(R.id.viewAttendancebutton);
		logoutButton = (Button)findViewById(R.id.buttonlogout);

		// Set up branch spinner
		ArrayAdapter<String> adapter_branch = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branchString);
		adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerbranch.setAdapter(adapter_branch);

		// Set up year spinner
		ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearString);
		adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinneryear.setAdapter(adapter_year);

		// Initialize date picker
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		dyear = cal.get(Calendar.YEAR);
		dateEditText.setText(day + "/" + (month + 1) + "/" + dyear);

		// Set up listeners
		spinnerbranch.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				branch = parent.getItemAtPosition(position).toString();
				updateSubjectSpinner();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		spinneryear.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				year = parent.getItemAtPosition(position).toString();
				updateSubjectSpinner();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(0);
			}
		});

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (subject == null) {
					Toast.makeText(AddAttandanceSessionActivity.this, 
						"Please select a subject", Toast.LENGTH_SHORT).show();
					return;
				}

				AttendanceSessionBean attendanceSessionBean = new AttendanceSessionBean();
				FacultyBean bean = ((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).getFacultyBean();

				attendanceSessionBean.setAttendance_session_faculty_id(bean.getFaculty_id());
				attendanceSessionBean.setAttendance_session_department(branch);
				attendanceSessionBean.setAttendance_session_class(year);
				attendanceSessionBean.setAttendance_session_date(dateEditText.getText().toString());
				attendanceSessionBean.setAttendance_session_subject(subject);

				DBAdapter dbAdapter = new DBAdapter(AddAttandanceSessionActivity.this);
				long sessionId = dbAdapter.addAttendanceSession(attendanceSessionBean, currentSession);

				if (sessionId != -1) {
					ArrayList<StudentBean> studentBeanList = dbAdapter.getAllStudentByBranchYear(branch, year);
					
					if (studentBeanList.isEmpty()) {
						Toast.makeText(AddAttandanceSessionActivity.this, 
							"No students found for " + branch + " " + year, Toast.LENGTH_SHORT).show();
						return;
					}

					((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext())
						.setStudentBeanList(studentBeanList);

					Intent intent = new Intent(AddAttandanceSessionActivity.this, AddAttendanceActivity.class);
					intent.putExtra("sessionId", sessionId);
					intent.putExtra("session", currentSession);
					startActivity(intent);
				} else {
					Toast.makeText(AddAttandanceSessionActivity.this, 
						"Failed to create attendance session", Toast.LENGTH_SHORT).show();
				}
			}
		});

		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutConfirmationDialog();
			}
		});

		// Initialize subject spinner with empty adapter
		ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{});
		emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSubject.setAdapter(emptyAdapter);

		// Update subject spinner for initial values
		updateSubjectSpinner();
	}

	private void updateSubjectSpinner() {
		if (branch == null || year == null) {
			return;
		}

		// Get faculty ID
		FacultyBean facultyBean = ((ApplicationContext)getApplicationContext()).getFacultyBean();
		if (facultyBean == null) {
			Toast.makeText(this, "Faculty information not found", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		int facultyId = facultyBean.getFaculty_id();
		
		// Get assigned subjects for this faculty
		DBAdapter dbAdapter = new DBAdapter(this);
		ArrayList<String> assignedSubjects = dbAdapter.getAssignedSubjectsForFaculty(facultyId);
		
		// Filter subjects based on selected year
		ArrayList<String> filteredSubjects = new ArrayList<>();
		for (String subject : assignedSubjects) {
			if (subject.contains(year)) {
				filteredSubjects.add(subject.split("\\s+\\(")[0]); // Remove the year part
			}
		}
		
		if (filteredSubjects.isEmpty()) {
			Toast.makeText(this, "No subjects assigned for this year", Toast.LENGTH_SHORT).show();
			ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{});
			emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerSubject.setAdapter(emptyAdapter);
			subject = null;
			return;
		}
		
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
			android.R.layout.simple_spinner_item, filteredSubjects);
		subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSubject.setAdapter(subjectAdapter);

		// Add listener for subject spinner
		spinnerSubject.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				subject = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				subject = null;
			}
		});

		// Select first subject by default
		if (!filteredSubjects.isEmpty()) {
			subject = filteredSubjects.get(0);
			spinnerSubject.setSelection(0);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new DatePickerDialog(this, mDateSetListener, dyear, month, day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
		}
	};

	private void showLogoutConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Logout");
		builder.setMessage("Are you sure you want to logout?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(AddAttandanceSessionActivity.this, MainActivity.class);
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

	@Override
	public void onBackPressed() {
		// Don't do anything when back is pressed
		// This prevents accidental logout
	}
}
