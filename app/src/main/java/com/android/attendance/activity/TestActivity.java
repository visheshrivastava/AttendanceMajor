//package com.android.attendance.activity;
//
//import java.util.ArrayList;
//
//import com.android.attendance.bean.AttendanceBean;
//import com.android.attendance.bean.AttendanceSessionBean;
//import com.android.attendance.bean.FacultyBean;
//import com.android.attendance.bean.StudentBean;
//import com.android.attendance.db.DBAdapter;
//import com.example.androidattendancesystem.R;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.Toast;
//
//public class TestActivity extends Activity {
//
//	Button submit;
//	private String currentSession;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.test_main);
//
//		submit=(Button)findViewById(R.id.button1);
//
//		// Get the current session from intent
//		currentSession = getIntent().getStringExtra("session");
//		if (currentSession == null) {
//			// If no session provided, get it from SessionManager
//			SessionManager sessionManager = new SessionManager(this);
//			currentSession = sessionManager.getSession();
//		}
//
//		if (currentSession == null) {
//			Toast.makeText(this, "No session selected", Toast.LENGTH_SHORT).show();
//			finish();
//			return;
//		}
//
//		submit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				DBAdapter dbAdapter = new DBAdapter(TestActivity.this);
//				AttendanceSessionBean attendanceSessionBean = new AttendanceSessionBean();
//
//				FacultyBean bean = ((ApplicationContext)TestActivity.this.getApplicationContext()).getFacultyBean();
//
//				attendanceSessionBean.setAttendance_session_faculty_id(bean.getFaculty_id());
//				attendanceSessionBean.setAttendance_session_department("IT");
//				attendanceSessionBean.setAttendance_session_class("BE");
//				attendanceSessionBean.setAttendance_session_date("06/04/2016");
//				attendanceSessionBean.setAttendance_session_subject("DataBase");
//
//				long sessionId = dbAdapter.addAttendanceSession(attendanceSessionBean, currentSession);
//
//				if (sessionId != -1) {
//					ArrayList<StudentBean> studentBeanList = dbAdapter.getAllStudentByBranchYear("IT", "BE");
//					((ApplicationContext)TestActivity.this.getApplicationContext()).setStudentBeanList(studentBeanList);
//
//					Intent intent = new Intent(TestActivity.this, AddAttendanceActivity.class);
//					intent.putExtra("sessionId", sessionId);
//					intent.putExtra("session", currentSession);
//					startActivity(intent);
//				} else {
//					Toast.makeText(TestActivity.this,
//						"Failed to create attendance session", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//
//
//
//
//
//
//
//
//
//	}
//
//
//
//}
