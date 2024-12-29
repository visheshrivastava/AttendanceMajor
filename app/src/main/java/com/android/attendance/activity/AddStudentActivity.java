package com.android.attendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AddStudentActivity extends Activity {

	private static final String TAG = "CSV_IMPORT";
	private static final int READ_REQUEST_CODE = 2;

	Button registerButton;
	Button importButton;
	EditText textFirstName;
	EditText textLastName;
	EditText textPhone;
	EditText textaddress;
	Spinner spinnerdept;
	Spinner spinneryear;
	EditText textEnrollment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addstudent);

		Log.e(TAG, "AddStudentActivity onCreate");

		textFirstName = (EditText) findViewById(R.id.editTextFirstName);
		textLastName = (EditText) findViewById(R.id.editTextLastName);
		textPhone = (EditText) findViewById(R.id.editTextPhone);
		textaddress = (EditText) findViewById(R.id.editTextaddr);
		spinnerdept = (Spinner) findViewById(R.id.spinnerdept);
		spinneryear = (Spinner) findViewById(R.id.spinneryear);
		textEnrollment = (EditText) findViewById(R.id.editTextEnrollment);
		registerButton = (Button) findViewById(R.id.RegisterButton);
		importButton = (Button) findViewById(R.id.ImportButton);

		if (importButton == null) {
			Log.e(TAG, "Import button not found in layout");
		} else {
			Log.e(TAG, "Import button initialized");
		}

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addStudent();
			}
		});

		importButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "Import button clicked");
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("text/*");
					startActivityForResult(intent, READ_REQUEST_CODE);
					Log.e(TAG, "File picker intent started");
				} catch (Exception e) {
					Log.e(TAG, "Error starting file picker: " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private void addStudent() {
		String firstName = textFirstName.getText().toString();
		String lastName = textLastName.getText().toString();
		String phone = textPhone.getText().toString();
		String address = textaddress.getText().toString();
		String department = spinnerdept.getSelectedItem().toString();
		String year = spinneryear.getSelectedItem().toString();
		String enrollment = textEnrollment.getText().toString().trim();

		if (validateFields(firstName, lastName, phone, address, department, year, enrollment)) {
			StudentBean studentBean = new StudentBean();
			studentBean.setStudent_firstname(firstName);
			studentBean.setStudent_lastname(lastName);
			studentBean.setStudent_mobilenumber(phone);
			studentBean.setStudent_address(address);
			studentBean.setStudent_department(department);
			studentBean.setStudent_class(year);
			studentBean.setStudent_enrollment(enrollment);

			DBAdapter dbAdapter = new DBAdapter(this);
			if (dbAdapter.isEnrollmentExists(enrollment)) {
				Toast.makeText(this, "Enrollment number already exists!", Toast.LENGTH_SHORT).show();
				return;
			}
			dbAdapter.addStudent(studentBean);

			Toast.makeText(getApplicationContext(), "Student added successfully", Toast.LENGTH_SHORT).show();
			clearFields();
		}
	}

	private boolean validateFields(String... fields) {
		for (String field : fields) {
			if (field.isEmpty()) {
				Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private void clearFields() {
		textFirstName.setText("");
		textLastName.setText("");
		textPhone.setText("");
		textaddress.setText("");
		spinnerdept.setSelection(0);
		spinneryear.setSelection(0);
		textEnrollment.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult called - RequestCode: " + requestCode + ", ResultCode: " + resultCode);
		
		if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				Log.e(TAG, "Selected file URI: " + uri.toString());
				importCSV(uri);
			} else {
				Log.e(TAG, "No data received from file picker");
			}
		} else {
			Log.e(TAG, "File selection cancelled or failed");
		}
	}

	private void importCSV(Uri uri) {
		Log.e(TAG, "Starting CSV import process");
		try {
			InputStream inputStream = getContentResolver().openInputStream(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			ArrayList<StudentBean> students = new ArrayList<>();
			int lineCount = 0;

			Log.e(TAG, "Reading CSV file");
			while ((line = reader.readLine()) != null) {
				lineCount++;
				Log.e(TAG, "Reading line " + lineCount + ": " + line);
				Log.d(TAG, "============= Processing Line " + lineCount + " =============");
				Log.d(TAG, "Raw line: [" + line + "]");
				Log.d(TAG, "Line length: " + line.length());

				// Split and process the line
				String[] row = line.split(",");
				Log.d(TAG, "Number of fields after split: " + row.length);

				// Print each field with its index
				for (int i = 0; i < row.length; i++) {
					String field = row[i].trim();
					Log.d(TAG, "Field[" + i + "]: [" + field + "] Length: " + field.length());
				}

				if (row.length == 7) {
					StudentBean student = new StudentBean();
					
					// Set and verify each field individually
					student.setStudent_firstname(row[0].trim());
					Log.d(TAG, "First Name - Set: [" + row[0].trim() + 
						"] Get: [" + student.getStudent_firstname() + "]");

					student.setStudent_lastname(row[1].trim());
					Log.d(TAG, "Last Name - Set: [" + row[1].trim() + 
						"] Get: [" + student.getStudent_lastname() + "]");

					student.setStudent_mobilenumber(row[2].trim());
					Log.d(TAG, "Mobile - Set: [" + row[2].trim() + 
						"] Get: [" + student.getStudent_mobilenumber() + "]");

					student.setStudent_address(row[3].trim());
					Log.d(TAG, "Address - Set: [" + row[3].trim() + 
						"] Get: [" + student.getStudent_address() + "]");

					student.setStudent_department(row[4].trim());
					Log.d(TAG, "Department - Set: [" + row[4].trim() + 
						"] Get: [" + student.getStudent_department() + "]");

					student.setStudent_class(row[5].trim());
					Log.d(TAG, "Class - Set: [" + row[5].trim() + 
						"] Get: [" + student.getStudent_class() + "]");

					student.setStudent_enrollment(row[6].trim());
					Log.d(TAG, "Enrollment - Set: [" + row[6].trim() + 
						"] Get: [" + student.getStudent_enrollment() + "]");

					// Verify all fields before adding to list
					Log.d(TAG, "Final verification of student object:");
					Log.d(TAG, "First Name: [" + student.getStudent_firstname() + "]");
					Log.d(TAG, "Last Name: [" + student.getStudent_lastname() + "]");
					Log.d(TAG, "Mobile: [" + student.getStudent_mobilenumber() + "]");
					Log.d(TAG, "Address: [" + student.getStudent_address() + "]");
					Log.d(TAG, "Department: [" + student.getStudent_department() + "]");
					Log.d(TAG, "Class: [" + student.getStudent_class() + "]");
					Log.d(TAG, "Enrollment: [" + student.getStudent_enrollment() + "]");

					if (student.getStudent_enrollment() != null && !student.getStudent_enrollment().isEmpty()) {
						students.add(student);
						Log.d(TAG, "Added student to import list. Current size: " + students.size());
					} else {
						Log.e(TAG, "Skipped student due to missing enrollment number");
					}
				} else {
					Log.e(TAG, "Invalid number of fields: " + row.length);
				}
			}
			reader.close();

			// Database insertion
			Log.e(TAG, "============= Starting Database Insertion =============");
			Log.e(TAG, "Total students to import: " + students.size());

			DBAdapter dbAdapter = new DBAdapter(this);
			int successCount = 0;

			for (StudentBean student : students) {
				Log.e(TAG, "Inserting student: " + 
					student.getStudent_firstname() + " " + 
					student.getStudent_lastname() + " (" + 
					student.getStudent_enrollment() + ")");

				long result = dbAdapter.addStudent(student);
				if (result != -1) {
					successCount++;
					Log.e(TAG, "Successfully inserted student with enrollment: " + 
						student.getStudent_enrollment());
				} else {
					Log.e(TAG, "Failed to insert student with enrollment: " + 
						student.getStudent_enrollment());
				}
			}

			final String summary = "Imported " + successCount + " out of " + students.size() + " students";
			Log.e(TAG, "============= Import Complete =============");
			Log.e(TAG, summary);

			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), summary, Toast.LENGTH_LONG).show();
				}
			});

		} catch (Exception e) {
			Log.e(TAG, "Error importing CSV: " + e.getMessage());
			e.printStackTrace();
			final String error = "Import error: " + e.getMessage();
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
				}
			});
		}
	}
}
