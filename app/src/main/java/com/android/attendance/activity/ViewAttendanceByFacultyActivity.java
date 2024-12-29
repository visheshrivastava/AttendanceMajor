package com.android.attendance.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
		
		// Set header
		dateHeaderTextView.setText("Subject: " + selectedSubject);

		ArrayList<AttendanceBean> attendanceBeanList = 
			((ApplicationContext) getApplicationContext()).getAttendanceBeanList();

		// Sort attendance by date first, then by enrollment number
		Collections.sort(attendanceBeanList, new Comparator<AttendanceBean>() {
			@Override
			public int compare(AttendanceBean a1, AttendanceBean a2) {
				// First compare by date
				int dateCompare = a1.getAttendance_session_date().compareTo(a2.getAttendance_session_date());
				if (dateCompare != 0) {
					return dateCompare;
				}
				// If same date, compare by enrollment number
				return a1.getAttendance_student_id().compareTo(a2.getAttendance_student_id());
			}
		});

		ArrayList<String> attendanceList = new ArrayList<String>();
		String currentDate = null;

		if (attendanceBeanList != null && !attendanceBeanList.isEmpty()) {
			DBAdapter dbAdapter = new DBAdapter(this);
			
			for (AttendanceBean attendanceBean : attendanceBeanList) {
				// Add date header if it's a new date
				if (currentDate == null || !currentDate.equals(attendanceBean.getAttendance_session_date())) {
					currentDate = attendanceBean.getAttendance_session_date();
					attendanceList.add("\nDate: " + currentDate + "\n");
				}
				
				StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
				String status = attendanceBean.getAttendance_status();
				String attendanceInfo = String.format("%s %s (%s) | %s",
					studentBean.getStudent_firstname(),
						studentBean.getStudent_lastname(),
						studentBean.getStudent_enrollment(),
						status);
				attendanceList.add(attendanceInfo);
			}
		} else {
			attendanceList.add("No attendance records found");
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
