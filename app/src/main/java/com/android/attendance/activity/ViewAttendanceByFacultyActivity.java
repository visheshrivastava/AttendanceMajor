package com.android.attendance.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
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

public class ViewAttendanceByFacultyActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private String currentSession;
	private TextView dateHeaderTextView;
	private DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_attendance_list);

		currentSession = getIntent().getStringExtra("session");
		String selectedSubject = getIntent().getStringExtra("subject");
		
		if (currentSession == null) {
				Toast.makeText(this, "No session selected", Toast.LENGTH_SHORT).show();
				finish();
				return;
		}

		listView = findViewById(R.id.listview);
		dateHeaderTextView = findViewById(R.id.dateHeaderTextView);
		dbAdapter = new DBAdapter(this);
		
		// Get attendance session bean from intent
		ArrayList<AttendanceBean> attendanceBeanList = 
			((ApplicationContext)getApplicationContext()).getAttendanceBeanList();
		
		if (attendanceBeanList == null || attendanceBeanList.isEmpty()) {
			Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show();
			return;
		}

		// Set header
		dateHeaderTextView.setText("Subject: " + selectedSubject);

		ArrayList<String> attendanceList = new ArrayList<String>();
		
		// Get unique students
		Set<String> uniqueStudents = new HashSet<>();
		for(AttendanceBean attendance : attendanceBeanList) {
			uniqueStudents.add(attendance.getAttendance_student_id());
		}

		Log.d("ViewAttendance", "Found " + uniqueStudents.size() + " unique students");

		// After getting unique students, create a list of StudentBean objects for sorting
		ArrayList<StudentBean> sortedStudents = new ArrayList<>();
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

}
