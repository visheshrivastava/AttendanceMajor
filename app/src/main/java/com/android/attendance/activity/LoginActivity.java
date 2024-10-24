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

	Button login;
	EditText username,password;
	Spinner spinnerloginas;
	String userrole;
	private String[] userRoleString = new String[] { "admin", "faculty"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		login =(Button)findViewById(R.id.buttonlogin);
		username=(EditText)findViewById(R.id.editTextusername);
		password=(EditText)findViewById(R.id.editTextpassword);
		spinnerloginas=(Spinner)findViewById(R.id.spinnerloginas);

		spinnerloginas.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				// Check if view is not null before setting text color
				if (view != null && view instanceof TextView) {
					((TextView) view).setTextColor(Color.WHITE);
				}
				userrole = spinnerloginas.getSelectedItem().toString();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		ArrayAdapter<String> adapter_role = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userRoleString) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK); // Change this color as needed
				return view;
			}

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTextColor(Color.BLACK);
				return view;
			}
		};
		adapter_role.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerloginas.setAdapter(adapter_role);

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(userrole.equals("admin"))
				{

					String user_name = username.getText().toString();
					String pass_word = password.getText().toString();

					if (TextUtils.isEmpty(user_name)) 
					{
						username.setError("Invalid User Name");
					}
					else if(TextUtils.isEmpty(pass_word))
					{
						password.setError("enter password");
					}
					else
					{
						Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
						intent.putExtra("role", userrole);
						startActivity(intent);
						Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
					}
				}
				
				else
				{
					String user_name = username.getText().toString();
					String pass_word = password.getText().toString();

					if (TextUtils.isEmpty(user_name)) 
					{
						username.setError("Invalid User Name");
					}
					else if(TextUtils.isEmpty(pass_word))
					{
						password.setError("enter password");
					}
					DBAdapter dbAdapter = new DBAdapter(LoginActivity.this);
					FacultyBean facultyBean = dbAdapter.validateFaculty(user_name, pass_word);
					
					if(facultyBean!=null)
					{
						Intent intent = new Intent(LoginActivity.this,AddAttandanceSessionActivity.class);
						startActivity(intent);
						((ApplicationContext)LoginActivity.this.getApplicationContext()).setFacultyBean(facultyBean);
						Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
					}
				}

				if (userrole.equals("faculty")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setTitle("Faculty Login");
					builder.setMessage("Are you an existing user?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Proceed with existing login flow
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Redirect to faculty registration
							Intent intent = new Intent(LoginActivity.this, FacultyRegistrationActivity.class);
							startActivity(intent);
						}
					});
					builder.show();
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
