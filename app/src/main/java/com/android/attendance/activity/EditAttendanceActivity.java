package com.android.attendance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.StudentBean;
import com.android.attendance.db.DBAdapter;
import com.example.androidattendancesystem.R;

import java.util.ArrayList;

public class EditAttendanceActivity extends Activity {

    private ListView attendanceListView;
    private ArrayAdapter<String> listAdapter;
    private Button updateAttendanceButton;
    private AttendanceSessionBean sessionBean;
    private ArrayList<StudentBean> studentBeanList;
    private ArrayList<AttendanceBean> attendanceBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);

        sessionBean = (AttendanceSessionBean) getIntent().getSerializableExtra("sessionBean");
        if (sessionBean == null) {
            Toast.makeText(this, "Invalid session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        attendanceListView = (ListView) findViewById(R.id.attendanceListView);
        updateAttendanceButton = (Button) findViewById(R.id.updateAttendanceButton);

        DBAdapter dbAdapter = new DBAdapter(this);
        attendanceBeanList = dbAdapter.getAttendanceBySessionID(sessionBean);
        studentBeanList = dbAdapter.getAllStudent();

        final ArrayList<String> studentList = new ArrayList<>();
        for (AttendanceBean attendanceBean : attendanceBeanList) {
            StudentBean studentBean = dbAdapter.getStudentById(attendanceBean.getAttendance_student_id());
            String status = attendanceBean.getAttendance_status().equals("P") ? "Present" : "Absent";
            studentList.add(studentBean.getStudent_firstname() + " " + studentBean.getStudent_lastname() + " - " + status);
        }

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, studentList);
        attendanceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        attendanceListView.setAdapter(listAdapter);

        for (int i = 0; i < attendanceBeanList.size(); i++) {
            attendanceListView.setItemChecked(i, attendanceBeanList.get(i).getAttendance_status().equals("P"));
        }

        updateAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < attendanceBeanList.size(); i++) {
                    AttendanceBean attendanceBean = attendanceBeanList.get(i);
                    attendanceBean.setAttendance_status(attendanceListView.isItemChecked(i) ? "P" : "A");
                    dbAdapter.updateAttendance(attendanceBean);
                }
                Toast.makeText(EditAttendanceActivity.this, "Attendance updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
