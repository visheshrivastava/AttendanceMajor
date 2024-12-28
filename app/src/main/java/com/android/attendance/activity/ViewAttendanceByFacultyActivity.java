package com.android.attendance.activity;

import java.util.ArrayList;

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

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.FacultyBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

public class ViewAttendanceByFacultyActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private String currentSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_attendance_list);

		// Get current session
		currentSession = getIntent().getStringExtra("session");
		if (currentSession == null) {
			Toast.makeText(this, "No session selected", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		listView = (ListView) findViewById(R.id.listview);
		ArrayList<AttendanceBean> attendanceBeanList = ((ApplicationContext) getApplicationContext()).getAttendanceBeanList();

		ArrayList<String> attendanceList = new ArrayList<String>();
		attendanceList.add("StudentName | Status");

		if (attendanceBeanList != null && !attendanceBeanList.isEmpty()) {
			for (AttendanceBean attendanceBean : attendanceBeanList) {
				DBAdapter dbAdapter = new DBAdapter(this);
				StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
				
				String status = attendanceBean.getAttendance_status().equals("P") ? "Present" : "Absent";
				String attendanceInfo = studentBean.getStudent_firstname() + " " + 
									 studentBean.getStudent_lastname() + " (" +
									 studentBean.getStudent_enrollment() + ") | " + status;
				attendanceList.add(attendanceInfo);
			}
		} else {
			attendanceList.add("No attendance records found");
		}

		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attendanceList);
		listView.setAdapter(listAdapter);

		// Add long press to edit attendance
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (position > 0 && position < attendanceBeanList.size() + 1) {
					AttendanceBean selectedAttendance = attendanceBeanList.get(position - 1);
					showEditDialog(selectedAttendance);
				}
				return true;
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

}
