package com.android.attendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class ViewFacultyActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<FacultyBean> facultyList;
	private DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_faculty_list);

		listView = findViewById(R.id.listView);
		dbAdapter = new DBAdapter(this);
		
		loadFacultyList();

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				FacultyBean selectedFaculty = facultyList.get(position);
				showDeleteConfirmationDialog(selectedFaculty);
				return true;
			}
		});
	}

	private void loadFacultyList() {
		facultyList = dbAdapter.getAllFaculty();

		Log.d("ViewFacultyActivity", "Faculty list size: " + facultyList.size());

		ArrayList<String> facultyStringList = new ArrayList<>();
		for (FacultyBean faculty : facultyList) {
			String facultyName = faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname();
			facultyStringList.add(facultyName);
			Log.d("ViewFacultyActivity", "Added faculty: " + facultyName);
		}

		listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, facultyStringList);
		listView.setAdapter(listAdapter);
	}

	private void showDeleteConfirmationDialog(final FacultyBean faculty) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Faculty");
		builder.setMessage("Are you sure you want to delete " + faculty.getFaculty_firstname() + " " + faculty.getFaculty_lastname() + "?");
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteFaculty(faculty);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	private void deleteFaculty(FacultyBean faculty) {
		boolean success = dbAdapter.deleteFaculty(faculty.getFaculty_id());
		if (success) {
			Toast.makeText(this, "Faculty deleted successfully", Toast.LENGTH_SHORT).show();
			loadFacultyList(); // Reload the list
		} else {
			Toast.makeText(this, "Failed to delete faculty", Toast.LENGTH_SHORT).show();
		}
	}
}
