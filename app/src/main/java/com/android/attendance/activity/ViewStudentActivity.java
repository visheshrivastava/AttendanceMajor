package com.android.attendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;
import android.util.Log;
import java.util.Collections;
import java.util.Comparator;

public class ViewStudentActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<StudentBean> studentBeanList;
	private DBAdapter dbAdapter;
	private Spinner spinnerBranch, spinnerYear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_student_list);

		listView = (ListView) findViewById(R.id.listview);
		spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);
		spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
		dbAdapter = new DBAdapter(this);
		
		// Set up branch spinner
		ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
				R.array.branches_array, android.R.layout.simple_spinner_item);
		branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBranch.setAdapter(branchAdapter);

		// Set up year spinner
		ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
				R.array.years_array, android.R.layout.simple_spinner_item);
		yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerYear.setAdapter(yearAdapter);

		spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				loadStudentList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				loadStudentList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				StudentBean selectedStudent = studentBeanList.get(position);
				showDeleteConfirmationDialog(selectedStudent);
				return true;
			}
		});

		loadStudentList();
	}

	private void loadStudentList() {
		String selectedBranch = spinnerBranch.getSelectedItem().toString();
		String selectedYear = spinnerYear.getSelectedItem().toString();

		Log.d("ViewStudentActivity", "Loading students for Branch: " + selectedBranch + ", Year: " + selectedYear);

		studentBeanList = dbAdapter.getAllStudentByBranchYear(selectedBranch, selectedYear);

		// Sort studentBeanList by enrollment number
		Collections.sort(studentBeanList, new Comparator<StudentBean>() {
			@Override
			public int compare(StudentBean s1, StudentBean s2) {
				String e1 = s1.getStudent_enrollment() != null ? s1.getStudent_enrollment() : "";
				String e2 = s2.getStudent_enrollment() != null ? s2.getStudent_enrollment() : "";
				return e1.compareTo(e2);
			}
		});

		ArrayList<String> studentList = new ArrayList<>();
		for (StudentBean studentBean : studentBeanList) {
			String studentInfo = String.format("%s %s (%s)",
				studentBean.getStudent_firstname(),
					studentBean.getStudent_lastname(),
					studentBean.getStudent_enrollment() != null ? 
						studentBean.getStudent_enrollment() : "No Enrollment"
			);
			studentList.add(studentInfo);
		}

		listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
		listView.setAdapter(listAdapter);

		if (studentList.isEmpty()) {
			Toast.makeText(this, "No students found for selected branch and year", Toast.LENGTH_SHORT).show();
		}
	}

	private void showDeleteConfirmationDialog(final StudentBean student) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Student");
		builder.setMessage("Are you sure you want to delete " + student.getStudent_firstname() + " " + student.getStudent_lastname() + "?");
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteStudent(student);
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	private void deleteStudent(StudentBean student) {
		try {
			dbAdapter.deleteStudent(student.getStudent_enrollment());
			Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
			loadStudentList();
		} catch (Exception e) {
			Toast.makeText(this, "Failed to delete student", Toast.LENGTH_SHORT).show();
			Log.e("ViewStudentActivity", "Error deleting student", e);
		}
	}

	private void searchStudents(String branch, String year, String enrollmentPattern) {
		studentBeanList = dbAdapter.getStudentsByBranchYearEnrollment(branch, year, enrollmentPattern);
		updateStudentList();
	}

	private void updateStudentList() {
		// Sort studentBeanList by enrollment number
		Collections.sort(studentBeanList, new Comparator<StudentBean>() {
			@Override
			public int compare(StudentBean s1, StudentBean s2) {
				String e1 = s1.getStudent_enrollment() != null ? s1.getStudent_enrollment() : "";
				String e2 = s2.getStudent_enrollment() != null ? s2.getStudent_enrollment() : "";
				return e1.compareTo(e2);
			}
		});

		ArrayList<String> studentList = new ArrayList<>();
		for (StudentBean studentBean : studentBeanList) {
			String studentInfo = String.format("%s %s (%s)",
				studentBean.getStudent_firstname(),
					studentBean.getStudent_lastname(),
					studentBean.getStudent_enrollment() != null ? 
						studentBean.getStudent_enrollment() : "No Enrollment"
			);
			studentList.add(studentInfo);
		}

		listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
		listView.setAdapter(listAdapter);

		if (studentList.isEmpty()) {
			Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
		}
	}
}
