package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

public class FacultyRegistrationActivity extends Activity {
    private EditText firstNameEditText, lastNameEditText, phoneEditText, addressEditText, usernameEditText, passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_registration);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFaculty();
            }
        });
    }

    private void registerFaculty() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(FacultyRegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FacultyBean facultyBean = new FacultyBean();
        facultyBean.setFaculty_firstname(firstName);
        facultyBean.setFaculty_lastname(lastName);
        facultyBean.setFaculty_mobilenumber(phone);
        facultyBean.setFaculty_address(address);
        facultyBean.setFaculty_username(username);
        facultyBean.setFaculty_password(password);

        DBAdapter dbAdapter = new DBAdapter(FacultyRegistrationActivity.this);
        long result = dbAdapter.addFacultyRegistrationRequest(facultyBean);

        if (result != -1) {
            Log.d("FacultyRegistration", "Faculty registration request added: " + firstName + " " + lastName);
            Toast.makeText(FacultyRegistrationActivity.this, "Registration request sent to admin", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.e("FacultyRegistration", "Failed to add faculty registration request");
            Toast.makeText(FacultyRegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
