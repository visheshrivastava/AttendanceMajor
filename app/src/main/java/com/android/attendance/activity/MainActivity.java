package com.android.attendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.androidattendancesystem.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Instead of setting a content view, we'll directly start the LoginActivity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish(); // This closes the MainActivity so the user can't navigate back to it
	}
}
