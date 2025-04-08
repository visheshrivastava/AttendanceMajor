package com.android.attendance.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.FacultyBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;
import com.android.attendance.adapter.ColoredAttendanceAdapter;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.net.Uri;
import android.content.ContentValues;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.os.Build;
import android.provider.Settings;

public class ViewAttendanceByFacultyActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private String currentSession;
	private TextView dateHeaderTextView;
	private DBAdapter dbAdapter;
	private Button exportButton;
	private String selectedSubject;
	private static final int PERMISSION_REQUEST_CODE = 123;
	private ArrayList<StudentBean> sortedStudents;
	private ArrayList<AttendanceBean> attendanceBeanList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_attendance_list);

		currentSession = getIntent().getStringExtra("session");
		selectedSubject = getIntent().getStringExtra("subject");
		
		if (currentSession == null) {
				Toast.makeText(this, "No session selected", Toast.LENGTH_SHORT).show();
				finish();
				return;
		}

		listView = findViewById(R.id.listview);
		dateHeaderTextView = findViewById(R.id.dateHeaderTextView);
		dbAdapter = new DBAdapter(this);
		
		// Get attendance session bean from intent
		attendanceBeanList = ((ApplicationContext)getApplicationContext()).getAttendanceBeanList();
		
		if (attendanceBeanList == null || attendanceBeanList.isEmpty()) {
			Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show();
			return;
		}

		// Set header
		dateHeaderTextView.setText("Subject: " + selectedSubject);

		ArrayList<String> attendanceList = new ArrayList<String>();
		
		// Get unique students and create sorted list
		Set<String> uniqueStudents = new HashSet<>();
		for(AttendanceBean attendance : attendanceBeanList) {
			uniqueStudents.add(attendance.getAttendance_student_id());
		}

		Log.d("ViewAttendance", "Found " + uniqueStudents.size() + " unique students");

		// Create sorted students list
		sortedStudents = new ArrayList<>();
		for(String studentId : uniqueStudents) {
			sortedStudents.add(dbAdapter.getStudentById(studentId));
		}

		// Sort students by enrollment number
		Collections.sort(sortedStudents, new Comparator<StudentBean>() {
			@Override
			public int compare(StudentBean s1, StudentBean s2) {
				return s1.getStudent_enrollment().compareTo(s2.getStudent_enrollment());
			}
		});

		// First add the summary section
		attendanceList.add("Attendance Summary\n");

		// Add summary for each student (now in sorted order)
		for(StudentBean student : sortedStudents) {
			int[] totalCounts = dbAdapter.getTotalAttendanceCount(
				student.getStudent_enrollment(), 
				selectedSubject, 
				currentSession
			);
			
			Log.d("ViewAttendance", String.format(Locale.getDefault(), 
				"Student: %s, ID: %s, Subject: %s, Session: %s", 
				student.getStudent_firstname(),
				student.getStudent_enrollment(),
				selectedSubject,
				currentSession));
			Log.d("ViewAttendance", "Present: " + totalCounts[0] + ", Total: " + totalCounts[1]);

			String summaryInfo = String.format(Locale.getDefault(),
					"%s %s (%s) | %d/%d  (%.1f%%)",
					student.getStudent_firstname(),
					student.getStudent_lastname(),
					student.getStudent_enrollment(),
					totalCounts[0],
					totalCounts[1],
					(totalCounts[1] > 0 ? (totalCounts[0] * 100.0 / totalCounts[1]) : 0));

			attendanceList.add(summaryInfo);
		}

		// Add detailed attendance
		attendanceList.add("\nDetailed Attendance\n");

		// Sort attendance by date first, then by enrollment number
		Collections.sort(attendanceBeanList, new Comparator<AttendanceBean>() {
			@Override
			public int compare(AttendanceBean a1, AttendanceBean a2) {
				String date1 = a1.getAttendance_session_date();
				String date2 = a2.getAttendance_session_date();

				// Handle null values
				if (date1 == null) date1 = "";
				if (date2 == null) date2 = "";

				// Sort in descending order (most recent first)
				return date2.compareTo(date1);
			}
		});

		String currentDate = null;
		for(AttendanceBean attendanceBean : attendanceBeanList) {
			String attendanceDate = attendanceBean.getAttendance_session_date();
			String studentId = attendanceBean.getAttendance_student_id();
			
			if (attendanceDate == null || studentId == null) {
				continue; // Skip invalid records
			}

			if (currentDate == null || !currentDate.equals(attendanceDate)) {
				currentDate = attendanceDate;
				attendanceList.add("\nDate: " + currentDate);
			}

			StudentBean studentBean = dbAdapter.getStudentById(studentId);
			if (studentBean == null) {
				continue; // Skip if student not found
			}

			String firstName = studentBean.getStudent_firstname();
			String lastName = studentBean.getStudent_lastname();
			String enrollment = studentBean.getStudent_enrollment();
			String status = attendanceBean.getAttendance_status();

			// Handle null values
			if (firstName == null) firstName = "";
			if (lastName == null) lastName = "";
			if (enrollment == null) enrollment = "No Enrollment";
			if (status == null) status = "N/A";

			String attendanceInfo = String.format(Locale.getDefault(),
				"  %s %s (%s) | %s",  // Added indentation for better readability
				firstName,
				lastName,
				enrollment,
				status);  // Keep original status (P/A) for color coding
			
			attendanceList.add(attendanceInfo);
		}

		listAdapter = new ColoredAttendanceAdapter(this,
			R.layout.view_attendance_list_per_student,
			R.id.labelAttendancePerStudent,
			attendanceList);
		listView.setAdapter(listAdapter);

		exportButton = findViewById(R.id.exportButton);
		exportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkPermissions();
			}
		});
	}

	private void showEditDialog(final AttendanceBean attendance) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Edit Attendance");
		
		final String[] options = {"Present", "Absent"};
		int currentChoice = attendance.getAttendance_status().equals("P") ? 0 : 1;

		builder.setSingleChoiceItems(options, currentChoice, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newStatus = (which == 0) ? "P" : "A";
				attendance.setAttendance_status(newStatus);
				
				DBAdapter dbAdapter = new DBAdapter(ViewAttendanceByFacultyActivity.this);
				dbAdapter.updateAttendanceWithSession(attendance, currentSession);

				dialog.dismiss();
				recreate(); // Refresh the activity to show updated data
			}
		});

		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void checkPermissions() {
		Log.d("ExportCSV", "Checking permissions");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			// For Android 11 and above
			if (Environment.isExternalStorageManager()) {
				Log.d("ExportCSV", "Storage permission already granted");
				exportToCSV();
			} else {
				Log.d("ExportCSV", "Requesting storage permission");
				try {
					Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
					startActivityForResult(intent, 2296);
				} catch (Exception e) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
					startActivityForResult(intent, 2296);
				}
			}
		} else {
			// For Android 10 and below
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED) {
				Log.d("ExportCSV", "Storage permission already granted");
				exportToCSV();
			} else {
				Log.d("ExportCSV", "Requesting storage permission");
				ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					PERMISSION_REQUEST_CODE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2296) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				if (Environment.isExternalStorageManager()) {
					Log.d("ExportCSV", "Storage permission granted");
					exportToCSV();
				} else {
					Log.e("ExportCSV", "Storage permission denied");
					Toast.makeText(this, "Storage permission is required to export attendance", 
						Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d("ExportCSV", "Permission result received");
		
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d("ExportCSV", "Storage permission granted");
				exportToCSV();
			} else {
				Log.e("ExportCSV", "Storage permission denied");
				Toast.makeText(this, "Storage permission is required to export attendance", 
					Toast.LENGTH_LONG).show();
				
				// Show explanation and settings option
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Permission Required");
				builder.setMessage("Storage permission is required to save the attendance CSV file. " +
					"Please grant the permission in Settings.");
				builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", getPackageName(), null);
						intent.setData(uri);
						startActivity(intent);
					}
				});
				builder.setNegativeButton("Cancel", null);
				builder.show();
			}
		}
	}

	private void exportToCSV() {
		Log.d("ExportCSV", "Starting export process");
		
		if (attendanceBeanList == null || attendanceBeanList.isEmpty()) {
			Log.e("ExportCSV", "No attendance data to export");
			Toast.makeText(this, "No attendance data to export", Toast.LENGTH_SHORT).show();
			return;
		}

		Log.d("ExportCSV", "Found " + attendanceBeanList.size() + " attendance records");

		try {
			// Create CSV file
			String fileName = "Attendance_" + selectedSubject + "_" + 
				new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
			
			Log.d("ExportCSV", "Creating file at: " + file.getAbsolutePath());
			
			FileWriter writer = new FileWriter(file);
			
			// Write header
			writer.append("Subject," + selectedSubject + "\n");
			writer.append("Session," + currentSession + "\n\n");
			
			// Write Summary Section
			writer.append("ATTENDANCE SUMMARY\n");
			writer.append("\"Student Name\",\"Enrollment\",\"Present\",\"Total\",\"Percentage\"\n");
			Log.d("ExportCSV", "Wrote summary header");

			// Write summary data
			for(StudentBean student : sortedStudents) {
				int[] totalCounts = dbAdapter.getTotalAttendanceCount(
					student.getStudent_enrollment(), 
					selectedSubject, 
					currentSession
				);
				
				float percentage = (totalCounts[1] > 0) ? (totalCounts[0] * 100.0f / totalCounts[1]) : 0;
				
				String summaryLine = String.format(Locale.getDefault(), "\"%s %s\",\"%s\",\"%d\",\"%d\",\"%.1f%%\"\n",
					student.getStudent_firstname(),
					student.getStudent_lastname(),
					student.getStudent_enrollment(),
					totalCounts[0],
					totalCounts[1],
					percentage);
				
				writer.append(summaryLine);
				Log.d("ExportCSV", "Wrote summary line: " + summaryLine.trim());
			}

			// Write Detailed Attendance Section
			writer.append("\nDETAILED ATTENDANCE\n");
			writer.append("\"Date\",\"Student Name\",\"Enrollment\",\"Status\"\n");
			Log.d("ExportCSV", "Wrote detailed attendance header");

			// Sort attendance by date first, then by enrollment number
			Collections.sort(attendanceBeanList, new Comparator<AttendanceBean>() {
				@Override
				public int compare(AttendanceBean a1, AttendanceBean a2) {
					String date1 = a1.getAttendance_session_date();
					String date2 = a2.getAttendance_session_date();

					if (date1 == null) date1 = "";
					if (date2 == null) date2 = "";

					return date2.compareTo(date1);
				}
			});

			// Write detailed attendance data
			for (AttendanceBean attendance : attendanceBeanList) {
				StudentBean student = dbAdapter.getStudentById(attendance.getAttendance_student_id());
				if (student != null) {
					String line = String.format(Locale.getDefault(), "\"%s\",\"%s %s\",\"%s\",\"%s\"\n",
						attendance.getAttendance_session_date(),
						student.getStudent_firstname(),
						student.getStudent_lastname(),
						student.getStudent_enrollment(),
						attendance.getAttendance_status());
					
					writer.append(line);
					Log.d("ExportCSV", "Wrote detailed line: " + line.trim());
				} else {
					Log.w("ExportCSV", "Student not found for ID: " + attendance.getAttendance_student_id());
				}
			}

			writer.flush();
			writer.close();
			
			Log.d("ExportCSV", "File written successfully");
			Toast.makeText(this, "Attendance exported to " + fileName, Toast.LENGTH_LONG).show();
			
		} catch (IOException e) {
			Log.e("ExportCSV", "Error exporting to CSV", e);
			Toast.makeText(this, "Error exporting attendance: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
