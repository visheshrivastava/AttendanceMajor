package com.android.attendance.activity;

import java.util.ArrayList;

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

	ArrayList<StudentBean> studentBeanList;
	private ListView listView ;  
	private ArrayAdapter<String> listAdapter;
	int sessionId=0;
	String status="P";
	Button attendanceSubmit;
	DBAdapter dbAdapter = new DBAdapter(this);
	private Button submitButton;
	private ArrayList<Boolean> attendanceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.__listview_main);

		sessionId = getIntent().getExtras().getInt("sessionId");
		
		listView = (ListView)findViewById(R.id.listview);
		final ArrayList<String> studentList = new ArrayList<String>();
		attendanceList = new ArrayList<Boolean>();

		studentBeanList = ((ApplicationContext)AddAttendanceActivity.this.getApplicationContext()).getStudentBeanList();

		for(StudentBean studentBean : studentBeanList) {
			String users = studentBean.getStudent_firstname() + "," + studentBean.getStudent_lastname();
			studentList.add(users);
			attendanceList.add(false);  // Initially all students are marked present (unchecked)
			Log.d("users: ", users); 
		}

		listAdapter = new ArrayAdapter<String>(this, R.layout.add_student_attendance, R.id.labelA, studentList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.studentCheckBox);
				checkBox.setChecked(attendanceList.get(position));
				
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						attendanceList.set(position, checkBox.isChecked());
					}
				});
				
				return view;
			}
		};
		listView.setAdapter(listAdapter);

		submitButton = (Button)findViewById(R.id.buttonSubmitAttendance);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAttendance();
			}
		});
	}

	private void submitAttendance() {
		for (int i = 0; i < studentBeanList.size(); i++) {
			StudentBean studentBean = studentBeanList.get(i);
			boolean isAbsent = attendanceList.get(i);
			
			AttendanceBean attendanceBean = new AttendanceBean();
			attendanceBean.setAttendance_session_id(sessionId);
			attendanceBean.setAttendance_student_id(studentBean.getStudent_id());
			attendanceBean.setAttendance_status(isAbsent ? "A" : "P");  // Changed this line
			
			dbAdapter.addNewAttendance(attendanceBean);
		}
		
		Toast.makeText(this, "Attendance submitted successfully", Toast.LENGTH_SHORT).show();
		finish();  // Close the activity after submitting
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
