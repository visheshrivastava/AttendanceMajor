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
	String subject = "SC";

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

		spinnerbranch=(Spinner)findViewById(R.id.spinner1);
		spinneryear=(Spinner)findViewById(R.id.spinneryear);
		spinnerSubject=(Spinner)findViewById(R.id.spinnerSE);

		ArrayAdapter<String> adapter_branch = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, branchString);
		adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerbranch.setAdapter(adapter_branch);

		ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearString);
		adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinneryear.setAdapter(adapter_year);

		AdapterView.OnItemSelectedListener selectionListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (parent == spinnerbranch) {
					branch = parent.getItemAtPosition(position).toString();
				} else if (parent == spinneryear) {
					year = parent.getItemAtPosition(position).toString();
				}
				updateSubjectSpinner();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};

		spinnerbranch.setOnItemSelectedListener(selectionListener);
		spinneryear.setOnItemSelectedListener(selectionListener);

		date = (ImageButton) findViewById(R.id.DateImageButton);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		dyear = cal.get(Calendar.YEAR);
		dateEditText = (EditText) findViewById(R.id.DateEditText);
		date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(0);

			}
		});

		submit=(Button)findViewById(R.id.buttonsubmit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AttendanceSessionBean attendanceSessionBean = new AttendanceSessionBean();
				FacultyBean bean=((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).getFacultyBean();

				attendanceSessionBean.setAttendance_session_faculty_id(bean.getFaculty_id());
				attendanceSessionBean.setAttendance_session_department(branch);
				attendanceSessionBean.setAttendance_session_class(year);
				attendanceSessionBean.setAttendance_session_date(dateEditText.getText().toString());
				attendanceSessionBean.setAttendance_session_subject(subject);

				DBAdapter dbAdapter = new DBAdapter(AddAttandanceSessionActivity.this);
				long sessionId = dbAdapter.addAttendanceSession(attendanceSessionBean, currentSession);

				if (sessionId != -1) {
					ArrayList<StudentBean> studentBeanList = dbAdapter.getAllStudentByBranchYear(branch, year);
					
					// Add validation before setting the student list
					if (studentBeanList.isEmpty()) {
						Toast.makeText(AddAttandanceSessionActivity.this, 
							"No students found for " + branch + " " + year, Toast.LENGTH_SHORT).show();
						return;
					}

					// Validate that students have enrollment numbers
					boolean hasInvalidStudents = false;
					for (StudentBean student : studentBeanList) {
						if (student.getStudent_enrollment() == null || student.getStudent_enrollment().trim().isEmpty()) {
							Log.e("AddAttandance", "Student " + student.getStudent_firstname() + " " + 
								student.getStudent_lastname() + " has no enrollment number");
							hasInvalidStudents = true;
						}
					}

					if (hasInvalidStudents) {
						Toast.makeText(AddAttandanceSessionActivity.this,
							"Some students are missing enrollment numbers. Please update student records first.",
							Toast.LENGTH_LONG).show();
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

		viewAttendance=(Button)findViewById(R.id.viewAttendancebutton);
		viewAttendance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (branch == null || year == null || subject == null) {
					Toast.makeText(AddAttandanceSessionActivity.this, 
						"Please select all fields", Toast.LENGTH_SHORT).show();
					return;
				}

				DBAdapter dbAdapter = new DBAdapter(AddAttandanceSessionActivity.this);
				
				// Get all sessions for this faculty, branch, year, and subject
				ArrayList<AttendanceSessionBean> sessions = dbAdapter.getAttendanceSessionsByFacultyAndSubject(
					((ApplicationContext)getApplicationContext()).getFacultyBean().getFaculty_id(),
					branch, year, subject, currentSession);

				if (sessions.isEmpty()) {
					Toast.makeText(AddAttandanceSessionActivity.this,
						"No attendance records found for " + subject, 
						Toast.LENGTH_SHORT).show();
					return;
				}

				// Get attendance for all sessions
				ArrayList<AttendanceBean> allAttendance = new ArrayList<>();
				for (AttendanceSessionBean session : sessions) {
					ArrayList<AttendanceBean> sessionAttendance = 
						dbAdapter.getAttendanceBySessionIDAndSession(session.getAttendance_session_id(), currentSession);
					// Add date information to each attendance record
					for (AttendanceBean attendance : sessionAttendance) {
						attendance.setAttendance_session_date(session.getAttendance_session_date());
					}
					allAttendance.addAll(sessionAttendance);
				}

				((ApplicationContext)getApplicationContext()).setAttendanceBeanList(allAttendance);
				
				Intent intent = new Intent(AddAttandanceSessionActivity.this, ViewAttendanceByFacultyActivity.class);
				intent.putExtra("session", currentSession);
				intent.putExtra("subject", subject);
				startActivity(intent);
			}
		});

		// Add these lines after initializing other UI elements
		viewShortAttendance = (Button)findViewById(R.id.viewShortAttendanceButton);
		startDateEditText = (EditText)findViewById(R.id.startDateEditText);
		endDateEditText = (EditText)findViewById(R.id.endDateEditText);

		startDateLabel = findViewById(R.id.startDateLabel);
		endDateLabel = findViewById(R.id.endDateLabel);
		viewShortAttendanceButton = findViewById(R.id.viewShortAttendanceButton);

		// Add click listeners for start and end date
		startDateEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(startDateEditText);
			}
		});

		endDateEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(endDateEditText);
			}
		});

		viewShortAttendanceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (startDateLabel.getVisibility() == View.GONE) {
					// Show date range selection
					startDateLabel.setVisibility(View.VISIBLE);
					endDateLabel.setVisibility(View.VISIBLE);
					startDateEditText.setVisibility(View.VISIBLE);
					endDateEditText.setVisibility(View.VISIBLE);
					viewShortAttendanceButton.setText("Submit");
				} else {
					// Process short attendance view
					String startDate = startDateEditText.getText().toString();
					String endDate = endDateEditText.getText().toString();

					if (startDate.isEmpty() || endDate.isEmpty()) {
						Toast.makeText(AddAttandanceSessionActivity.this, "Please select start and end dates", Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent(AddAttandanceSessionActivity.this, ViewShortAttendanceActivity.class);
					intent.putExtra("startDate", startDate);
					intent.putExtra("endDate", endDate);
					startActivity(intent);

					// Hide date range selection after processing
					startDateLabel.setVisibility(View.GONE);
					endDateLabel.setVisibility(View.GONE);
					startDateEditText.setVisibility(View.GONE);
					endDateEditText.setVisibility(View.GONE);
					viewShortAttendanceButton.setText("View Short Attendance");
				}
			}
		});

		Button importCsvButton = (Button) findViewById(R.id.importCsvButton);
		importCsvButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("text/*");
				startActivityForResult(intent, READ_REQUEST_CODE);
			}
		});

		Button editAttendanceButton = (Button) findViewById(R.id.editAttendanceButton);
		editAttendanceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editAttendanceForDate();
			}
		});

		spinnerSubject.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				subject = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		logoutButton = (Button) findViewById(R.id.buttonlogout);
		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutConfirmationDialog();
			}
		});
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, datePickerListener, dyear, month, day);
	}
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
							  int selectedMonth, int selectedDay) {
			dateEditText.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
					+ selectedYear);
		}
	};

	private void showDatePickerDialog(final EditText dateEditText) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog datePickerDialog = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
					}
				},
				year, month, day);
		datePickerDialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();
				readCsvFile(uri);
			}
		}
	}

	private void readCsvFile(Uri uri) {
		try {
			InputStream inputStream = getContentResolver().openInputStream(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			DBAdapter dbAdapter = new DBAdapter(this);
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				lineNumber++;
				String[] studentData = line.split(",");
				if (studentData.length >= 7) {
					StudentBean studentBean = new StudentBean();
					studentBean.setStudent_firstname(studentData[0].trim());
					studentBean.setStudent_lastname(studentData[1].trim());
					studentBean.setStudent_mobilenumber(studentData[2].trim());
					studentBean.setStudent_address(studentData[3].trim());
					studentBean.setStudent_department(studentData[4].trim());
					studentBean.setStudent_class(studentData[5].trim());
					studentBean.setStudent_enrollment(studentData[6].trim());

					if (studentBean.getStudent_enrollment() == null || 
						studentBean.getStudent_enrollment().trim().isEmpty()) {
						Log.e("CSV_IMPORT", "Missing enrollment number at line " + lineNumber);
						continue;
					}

					dbAdapter.addStudent(studentBean);
				} else {
					Log.e("CSV_IMPORT", "Invalid data format at line " + lineNumber + 
						". Expected 7 columns, got " + studentData.length);
				}
			}
			reader.close();
			Toast.makeText(this, "Students imported successfully", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error importing students: " + e.getMessage(), 
				Toast.LENGTH_LONG).show();
		}
	}

	private void editAttendanceForDate() {
		String selectedDate = dateEditText.getText().toString();
		if (TextUtils.isEmpty(selectedDate)) {
			Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
			return;
		}

		DBAdapter dbAdapter = new DBAdapter(this);
		
		// First get the attendance session for this date and faculty
		String query = "SELECT * FROM attendance_session_table WHERE " +
					  "attendance_session_faculty_id = ? AND " +
					  "attendance_session_department = ? AND " +
					  "attendance_session_class = ? AND " +
					  "attendance_session_subject = ? AND " +
					  "attendance_session_date = ? AND " +
					  "session = ?";

		FacultyBean facultyBean = ((ApplicationContext)getApplicationContext()).getFacultyBean();
		
		AttendanceSessionBean sessionBean = new AttendanceSessionBean();
		sessionBean.setAttendance_session_faculty_id(facultyBean.getFaculty_id());
		sessionBean.setAttendance_session_department(branch);
		sessionBean.setAttendance_session_class(year);
		sessionBean.setAttendance_session_date(selectedDate);
		sessionBean.setAttendance_session_subject(subject);

		// Get attendance records for this session
		ArrayList<AttendanceSessionBean> sessions = dbAdapter.getAttendanceSessionsByFacultyAndSubject(
			facultyBean.getFaculty_id(),
			branch,
			year,
			subject,
			currentSession
		);

		// Find the session for the selected date
		AttendanceSessionBean targetSession = null;
		for (AttendanceSessionBean session : sessions) {
			if (session.getAttendance_session_date().equals(selectedDate)) {
				targetSession = session;
				break;
			}
		}

		if (targetSession == null) {
			Toast.makeText(this, "No attendance record found for the selected date", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(AddAttandanceSessionActivity.this, EditAttendanceActivity.class);
		intent.putExtra("sessionBean", targetSession);
		startActivity(intent);
	}

	private void updateSubjectSpinner() {
		String selectedBranch = spinnerbranch.getSelectedItem().toString();
		String selectedYear = spinneryear.getSelectedItem().toString();
		
		List<String> subjects = SubjectManager.getSubjectsForDepartmentAndYear(selectedBranch, selectedYear);
		
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
			android.R.layout.simple_spinner_item, subjects);
			subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerSubject.setAdapter(subjectAdapter);
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

}
