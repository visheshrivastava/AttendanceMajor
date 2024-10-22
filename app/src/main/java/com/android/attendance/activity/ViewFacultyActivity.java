package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class ViewFacultyActivity extends Activity {

	private ListView facultyListView;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_faculty);

		facultyListView = findViewById(R.id.facultyListView);
		
		DBAdapter dbAdapter = new DBAdapter(this);
		ArrayList<FacultyBean> facultyList = dbAdapter.getAllFaculty();

		ArrayList<String> facultyStringList = new ArrayList<>();
		for (FacultyBean faculty : facultyList) {
			facultyStringList.add(faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname());
		}

		listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, facultyStringList);
		facultyListView.setAdapter(listAdapter);
	}
}
