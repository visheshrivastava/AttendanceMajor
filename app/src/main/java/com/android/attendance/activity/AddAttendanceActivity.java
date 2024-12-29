package com.android.attendance.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;  // Add this import
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

public class AddAttendanceActivity extends Activity {

	private ListView listView;
	private ArrayList<StudentBean> studentBeanList;
	private ArrayAdapter<String> listAdapter;
	private Button submitButton;
	private String currentSession;
	private long sessionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_attendance);

		currentSession = getIntent().getStringExtra("session");
		sessionId = getIntent().getLongExtra("sessionId", -1);

		if (currentSession == null || sessionId == -1) {
			Toast.makeText(this, "Invalid session information", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		listView = (ListView) findViewById(R.id.listview);
		if (listView == null) {
			Toast.makeText(this, "Error initializing view", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		submitButton = (Button) findViewById(R.id.buttonsubmit);

		studentBeanList = ((ApplicationContext) getApplicationContext()).getStudentBeanList();
		if (studentBeanList == null) {
			Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// Sort studentBeanList by enrollment number
		Collections.sort(studentBeanList, new Comparator<StudentBean>() {
			@Override
			public int compare(StudentBean s1, StudentBean s2) {
				String e1 = s1.getStudent_enrollment() != null ? s1.getStudent_enrollment() : "";
				String e2 = s2.getStudent_enrollment() != null ? s2.getStudent_enrollment() : "";
				return e1.compareTo(e2);
			}
		});

		final ArrayList<String> studentList = new ArrayList<String>();
		for (StudentBean studentBean : studentBeanList) {
			String users = String.format("%s %s (%s)",
				studentBean.getStudent_firstname(),
				studentBean.getStudent_lastname(),
				studentBean.getStudent_enrollment() != null ? 
					studentBean.getStudent_enrollment() : "No Enrollment"
			);
			studentList.add(users);
		}

		listAdapter = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_multiple_choice, studentList);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(listAdapter);

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitAttendance();
			}
		});
	}

	private void submitAttendance() {
		final ArrayList<AttendanceBean> attendanceBeanList = new ArrayList<AttendanceBean>();

		final int len = studentBeanList.size();
		for (int i = 0; i < len; i++) {
			AttendanceBean attendanceBean = new AttendanceBean();
			attendanceBean.setAttendance_session_id(sessionId);
			String enrollment = studentBeanList.get(i).getStudent_enrollment();
			
			if (enrollment == null || enrollment.trim().isEmpty()) {
				Log.e("AddAttendance", "Student at position " + i + " has no enrollment number");
				Toast.makeText(AddAttendanceActivity.this, 
					"Error: Some students are missing enrollment numbers", 
					Toast.LENGTH_SHORT).show();
				continue;
			}

			attendanceBean.setAttendance_student_id(enrollment);
			attendanceBean.setAttendance_status(listView.isItemChecked(i) ? "A" : "P");
			attendanceBeanList.add(attendanceBean);
		}

		if (!attendanceBeanList.isEmpty()) {
			DBAdapter dbAdapter = new DBAdapter(AddAttendanceActivity.this);
			for (AttendanceBean attendanceBean : attendanceBeanList) {
				dbAdapter.addAttendanceWithSession(attendanceBean, currentSession);
			}
			Toast.makeText(AddAttendanceActivity.this, 
				"Attendance saved successfully", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			Toast.makeText(AddAttendanceActivity.this, 
				"No valid attendance records to save", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
