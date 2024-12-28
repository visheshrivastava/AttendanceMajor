package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class ViewStudentByBranchYear extends Activity {

	ArrayList<StudentBean> studentBeanList;
	private ListView listView ;  
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_student_by_branch_year);

		listView=(ListView)findViewById(R.id.listview);
		TextView titleTextView = (TextView) findViewById(R.id.titleTextView);

		DBAdapter dbAdapter = new DBAdapter(this);
		String branch = getIntent().getStringExtra("branch");
		String year = getIntent().getStringExtra("year");

		studentBeanList = dbAdapter.getAllStudentByBranchYear(branch, year);

		ArrayList<String> studentList = new ArrayList<String>();
		for (StudentBean studentBean : studentBeanList) {
			String studentInfo = studentBean.getStudent_firstname() + " " +
								 studentBean.getStudent_lastname() + " - " +
								 studentBean.getStudent_enrollment();
			studentList.add(studentInfo);
		}

		listAdapter = new ArrayAdapter<String>(this, R.layout.student_list_item, R.id.studentNameTextView, studentList);
		listView.setAdapter(listAdapter);

		titleTextView.setText("Students - " + branch + " (" + year + ")");
	}
}
