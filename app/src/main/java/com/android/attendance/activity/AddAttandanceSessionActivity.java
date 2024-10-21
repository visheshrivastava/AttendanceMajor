package com.android.attendance.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.FacultyBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
	String year = "SE";
	String subject = "SC";

	private String[] branchString = new String[] { "IT"};
	private String[] yearString = new String[] {"1Y","2Y","3Y"};

	private String[] subjectFinal = new String[] {"M3","DS","M4","CN","M5","NS"};
	AttendanceSessionBean attendanceSessionBean;

	// Add these as class variables
	private Button viewShortAttendance;
	private EditText startDateEditText;
	private EditText endDateEditText;

	private static final int READ_REQUEST_CODE = 42;

	private TextView startDateLabel;
	private TextView endDateLabel;

	private Button viewShortAttendanceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_attandance);


		spinnerbranch=(Spinner)findViewById(R.id.spinner1);
		spinneryear=(Spinner)findViewById(R.id.spinneryear);
		spinnerSubject=(Spinner)findViewById(R.id.spinnerSE);

		ArrayAdapter<String> adapter_branch = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, branchString);
		adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerbranch.setAdapter(adapter_branch);
		spinnerbranch.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				((TextView) arg0.getChildAt(0)).setTextColor(Color.WHITE);
				branch =(String) spinnerbranch.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		///......................spinner2
		ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearString);
		adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinneryear.setAdapter(adapter_year);
		spinneryear.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				((TextView) arg0.getChildAt(0)).setTextColor(Color.WHITE);
				year =(String) spinneryear.getSelectedItem();
				Toast.makeText(getApplicationContext(), "year:"+year, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		ArrayAdapter<String> adapter_subject = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectFinal);
		adapter_subject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSubject.setAdapter(adapter_subject);
		spinnerSubject.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				((TextView) arg0.getChildAt(0)).setTextColor(Color.WHITE);
				subject =(String) spinnerSubject.getSelectedItem();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});


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
				int sessionId=	dbAdapter.addAttendanceSession(attendanceSessionBean);

				ArrayList<StudentBean> studentBeanList=dbAdapter.getAllStudentByBranchYear(branch, year);
				((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).setStudentBeanList(studentBeanList);


				Intent intent = new Intent(AddAttandanceSessionActivity.this,AddAttendanceActivity.class);
				intent.putExtra("sessionId", sessionId);
				startActivity(intent);
			}
		});

		viewAttendance=(Button)findViewById(R.id.viewAttendancebutton);
		viewAttendance.setOnClickListener(new OnClickListener() {

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

				ArrayList<AttendanceBean> attendanceBeanList = dbAdapter.getAttendanceBySessionID(attendanceSessionBean);
				((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).setAttendanceBeanList(attendanceBeanList);

				Intent intent = new Intent(AddAttandanceSessionActivity.this,ViewAttendanceByFacultyActivity.class);
				startActivity(intent);

			}
		});

		viewTotalAttendance=(Button)findViewById(R.id.viewTotalAttendanceButton);
		viewTotalAttendance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AttendanceSessionBean attendanceSessionBean = new AttendanceSessionBean();
				FacultyBean bean=((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).getFacultyBean();

				attendanceSessionBean.setAttendance_session_faculty_id(bean.getFaculty_id());
				attendanceSessionBean.setAttendance_session_department(branch);
				attendanceSessionBean.setAttendance_session_class(year);
				attendanceSessionBean.setAttendance_session_subject(subject);

				DBAdapter dbAdapter = new DBAdapter(AddAttandanceSessionActivity.this);

				ArrayList<AttendanceBean> attendanceBeanList = dbAdapter.getTotalAttendanceBySessionID(attendanceSessionBean);
				((ApplicationContext)AddAttandanceSessionActivity.this.getApplicationContext()).setAttendanceBeanList(attendanceBeanList);

				Intent intent = new Intent(AddAttandanceSessionActivity.this,ViewAttendanceByFacultyActivity.class);
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

			while ((line = reader.readLine()) != null) {
				String[] studentData = line.split(",");
				if (studentData.length >= 6) {
					StudentBean studentBean = new StudentBean();
					studentBean.setStudent_firstname(studentData[0].trim());
					studentBean.setStudent_lastname(studentData[1].trim());
					studentBean.setStudent_mobilenumber(studentData[2].trim());
					studentBean.setStudent_address(studentData[3].trim());
					studentBean.setStudent_department(studentData[4].trim());
					studentBean.setStudent_class(studentData[5].trim());

					dbAdapter.addStudent(studentBean);
				}
			}
			reader.close();
			Toast.makeText(this, "Students imported successfully", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error importing students: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void editAttendanceForDate() {
		String selectedDate = dateEditText.getText().toString();
		if (TextUtils.isEmpty(selectedDate)) {
			Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
			return;
		}

		AttendanceSessionBean sessionBean = new AttendanceSessionBean();
		FacultyBean facultyBean = ((ApplicationContext)getApplicationContext()).getFacultyBean();

		sessionBean.setAttendance_session_faculty_id(facultyBean.getFaculty_id());
		sessionBean.setAttendance_session_department(branch);
		sessionBean.setAttendance_session_class(year);
		sessionBean.setAttendance_session_date(selectedDate);
		sessionBean.setAttendance_session_subject(subject);

		DBAdapter dbAdapter = new DBAdapter(this);
		ArrayList<AttendanceBean> attendanceBeanList = dbAdapter.getAttendanceBySessionID(sessionBean);

		if (attendanceBeanList.isEmpty()) {
			Toast.makeText(this, "No attendance record found for the selected date", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(AddAttandanceSessionActivity.this, EditAttendanceActivity.class);
		intent.putExtra("sessionBean", sessionBean);
		startActivity(intent);
	}

}
