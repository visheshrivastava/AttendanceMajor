package com.android.attendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.attendance.bean.FacultyBean;
import com.android.attendance.context.ApplicationContext;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

public class LoginActivity extends Activity {

	private Spinner spinnerloginas, spinnerSession;
	private String userrole;
	private String selectedSession;
	private EditText username;
	private EditText password;
	private Button login, registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		 spinnerloginas = (Spinner) findViewById(R.id.spinnerloginas);
		 spinnerSession = (Spinner) findViewById(R.id.spinnerSession);
		 username = (EditText) findViewById(R.id.editTextusername);
		 password = (EditText) findViewById(R.id.editTextpassword);
		 login = (Button) findViewById(R.id.buttonlogin);
		 registerButton = (Button) findViewById(R.id.registerButton);

		// Set up role spinner
		String[] userRoles = new String[]{"admin", "faculty"};
		ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, userRoles);
		roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerloginas.setAdapter(roleAdapter);

		// Set up session spinner
		String[] sessions = new String[]{
			"2025-26 Odd Semester",
			"2025-26 Even Semester",
			"2026-27 Odd Semester",
			"2026-27 Even Semester"
		};
		ArrayAdapter<String> sessionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sessions);
		sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSession.setAdapter(sessionAdapter);

		spinnerloginas.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
				userrole = spinnerloginas.getSelectedItem().toString();
				if (userrole.equals("faculty")) {
					registerButton.setVisibility(View.VISIBLE);
				} else {
					registerButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerSession.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedSession = spinnerSession.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, FacultyRegistrationActivity.class);
				intent.putExtra("session", selectedSession);
				startActivity(intent);
			}
		});

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String user_name = username.getText().toString();
				String pass_word = password.getText().toString();

				if (userrole.equals("admin")) {
					if (user_name.equals("admin") && pass_word.equals("admin123")) {
						Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
						intent.putExtra("role", "admin");
						intent.putExtra("session", selectedSession);
						startActivity(intent);
						Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
					}
				} else if (userrole.equals("faculty")) {
					DBAdapter dbAdapter = new DBAdapter(LoginActivity.this);
					FacultyBean facultyBean = dbAdapter.validateFaculty(user_name, pass_word);
					if (facultyBean != null) {
						Intent intent = new Intent(LoginActivity.this, AddAttandanceSessionActivity.class);
						intent.putExtra("role", "faculty");
						intent.putExtra("session", selectedSession);
						((ApplicationContext)getApplicationContext()).setFacultyBean(facultyBean);
						startActivity(intent);
						Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
