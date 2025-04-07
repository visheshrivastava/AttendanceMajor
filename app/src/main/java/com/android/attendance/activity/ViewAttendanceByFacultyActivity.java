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

		// Add separator
		attendanceList.add("\nDetailed Attendance\n");

		// Sort attendance by date first, then by enrollment number
		Collections.sort(attendanceBeanList, new Comparator<AttendanceBean>() {
			@Override
			public int compare(AttendanceBean a1, AttendanceBean a2) {
				int dateCompare = a2.getAttendance_session_date().compareTo(a1.getAttendance_session_date());
				if (dateCompare != 0) return dateCompare;
				return a1.getAttendance_student_id().compareTo(a2.getAttendance_student_id());
			}
		});

		// Add detailed attendance
		String currentDate = null;
		for(AttendanceBean attendanceBean : attendanceBeanList) {
			if (currentDate == null || !currentDate.equals(attendanceBean.getAttendance_session_date())) {
				currentDate = attendanceBean.getAttendance_session_date();
				attendanceList.add("\nDate: " + currentDate + "\n");
			}

			StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
			String status = attendanceBean.getAttendance_status();
			String attendanceInfo = String.format(Locale.getDefault(),
				"%s %s (%s) | %s",
				studentBean.getStudent_firstname(),
				studentBean.getStudent_lastname(),
				studentBean.getStudent_enrollment(),
				status);
			
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
				if (checkPermission()) {
					exportToCSV();
				} else {
					requestPermission();
				}
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

	private boolean checkPermission() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			int result = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			return result == PackageManager.PERMISSION_GRANTED;
		}
		return true;
	}

	private void requestPermission() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			// Check if we already have permission
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				// Show explanation if needed
				if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Storage Permission Needed");
					builder.setMessage("This permission is required to save the attendance CSV file.");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							requestPermissions(
								new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
								PERMISSION_REQUEST_CODE
							);
						}
					});
					builder.show();
				} else {
					// No explanation needed, request the permission
					requestPermissions(
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSION_REQUEST_CODE
					);
				}
			} else {
				exportToCSV();
			}
		} else {
			exportToCSV();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				exportToCSV();
			} else {
				Toast.makeText(this, 
					"Storage permission is required to export attendance", 
					Toast.LENGTH_LONG).show();
				
				// Show settings dialog if permission was permanently denied
				if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Permission Required");
					builder.setMessage("Storage permission is required but has been permanently denied. " +
									"Please enable it in Settings.");
					builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Open app settings
							Intent intent = new Intent();
							intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
	}

	private void exportToCSV() {
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", 
				Locale.getDefault()).format(new Date());
			String fileName = "attendance_" + selectedSubject + "_" + timeStamp + ".csv";

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
				values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
				values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

				Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
				if (uri != null) {
					try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
						 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {

						// Write summary section
						writer.write("Attendance Summary for " + selectedSubject + "\n\n");
						writer.write("Name,Enrollment,Present,Total,Percentage\n");

						// Get unique students and their attendance counts
						for (StudentBean student : sortedStudents) {
							int[] totalCounts = dbAdapter.getTotalAttendanceCount(
								student.getStudent_enrollment(), 
								selectedSubject, 
								currentSession
							);
							
							double percentage = totalCounts[1] > 0 ? 
								(totalCounts[0] * 100.0 / totalCounts[1]) : 0;

							writer.write(String.format("%s %s,%s,%d,%d,%.1f%%\n",
								student.getStudent_firstname(),
								student.getStudent_lastname(),
								student.getStudent_enrollment(),
								totalCounts[0],
								totalCounts[1],
								percentage));
						}

						// Write detailed section
						writer.write("\nDetailed Attendance\n");
						writer.write("Date,Name,Enrollment,Status\n");

						for (AttendanceBean attendance : attendanceBeanList) {
							StudentBean student = dbAdapter.getStudentById(
								attendance.getAttendance_student_id());
							
							writer.write(String.format("%s,%s %s,%s,%s\n",
								attendance.getAttendance_session_date(),
								student.getStudent_firstname(),
								student.getStudent_lastname(),
								student.getStudent_enrollment(),
								attendance.getAttendance_status()));
						}

						writer.flush();
						Toast.makeText(this, "File exported to Downloads/" + fileName, 
							Toast.LENGTH_LONG).show();
					}
				}
			} else {
				// For older Android versions
				File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File file = new File(downloadsDir, fileName);

				try (FileWriter writer = new FileWriter(file)) {
					// Write summary section
					writer.append("Attendance Summary for " + selectedSubject + "\n\n");
					writer.append("Name,Enrollment,Present,Total,Percentage\n");

					// Get unique students and their attendance counts
					for (StudentBean student : sortedStudents) {
						int[] totalCounts = dbAdapter.getTotalAttendanceCount(
							student.getStudent_enrollment(), 
							selectedSubject, 
							currentSession
						);
						
						double percentage = totalCounts[1] > 0 ? 
							(totalCounts[0] * 100.0 / totalCounts[1]) : 0;

						writer.append(String.format("%s %s,%s,%d,%d,%.1f%%\n",
							student.getStudent_firstname(),
							student.getStudent_lastname(),
							student.getStudent_enrollment(),
							totalCounts[0],
							totalCounts[1],
							percentage));
					}

					// Write detailed section
					writer.append("\nDetailed Attendance\n");
					writer.append("Date,Name,Enrollment,Status\n");

					for (AttendanceBean attendance : attendanceBeanList) {
						StudentBean student = dbAdapter.getStudentById(
							attendance.getAttendance_student_id());
						
						writer.append(String.format("%s,%s %s,%s,%s\n",
							attendance.getAttendance_session_date(),
							student.getStudent_firstname(),
							student.getStudent_lastname(),
							student.getStudent_enrollment(),
							attendance.getAttendance_status()));
					}

					writer.flush();
					Toast.makeText(this, "Exported to " + file.getAbsolutePath(), 
						Toast.LENGTH_LONG).show();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Export failed: " + e.getMessage(), 
				Toast.LENGTH_SHORT).show();
		}
	}

}
