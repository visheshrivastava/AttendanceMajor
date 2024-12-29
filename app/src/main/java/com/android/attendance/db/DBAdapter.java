package com.android.attendance.db;

import java.util.ArrayList;

import com.android.attendance.bean.AttendanceBean;
import com.android.attendance.bean.AttendanceSessionBean;
import com.android.attendance.bean.FacultyBean;
import com.android.attendance.bean.StudentBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;

public class DBAdapter extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 13; // Increment from 12 to 13

	// Database Name
	private static final String DATABASE_NAME = "Attendance.db";

	// Contacts table name
	private static final String FACULTY_INFO_TABLE = "faculty_table";
	private static final String STUDENT_INFO_TABLE = "student_table";
	private static final String ATTENDANCE_SESSION_TABLE = "attendance_session_table";
	private static final String ATTENDANCE_TABLE = "attendance_table";
	private static final String FACULTY_REGISTRATION_TABLE = "faculty_registration";


	// Contacts Table Columns names
	private static final String KEY_FACULTY_ID = "faculty_id";
	private static final String KEY_FACULTY_FIRSTNAME = "faculty_firstname";
	private static final String KEY_FACULTY_LASTNAME = "faculty_Lastname";
	private static final String KEY_FACULTY_MO_NO = "faculty_mobilenumber";
	private static final String KEY_FACULTY_ADDRESS = "faculty_address";
	private static final String KEY_FACULTY_USERNAME = "faculty_username";
	private static final String KEY_FACULTY_PASSWORD = "faculty_password";
	private static final String KEY_FACULTY_EMAIL = "faculty_email";
	private static final String KEY_FACULTY_SUBJECT = "faculty_subject";

	private static final String KEY_STUDENT_ID = "student_enrollment";
	private static final String KEY_STUDENT_FIRSTNAME = "student_firstname";
	private static final String KEY_STUDENT_LASTNAME = "student_lastname";
	private static final String KEY_STUDENT_MO_NO = "student_mobilenumber";
	private static final String KEY_STUDENT_ADDRESS = "student_address";
	private static final String KEY_STUDENT_DEPARTMENT = "student_department";
	private static final String KEY_STUDENT_CLASS = "student_class";
	private static final String KEY_STUDENT_ENROLLMENT = "student_enrollment";

	private static final String KEY_ATTENDANCE_SESSION_ID = "attendance_session_id";
	private static final String KEY_ATTENDANCE_SESSION_FACULTY_ID = "attendance_session_faculty_id";
	private static final String KEY_ATTENDANCE_SESSION_DEPARTMENT = "attendance_session_department";
	private static final String KEY_ATTENDANCE_SESSION_CLASS = "attendance_session_class";
	private static final String KEY_ATTENDANCE_SESSION_DATE = "attendance_session_date";
	private static final String KEY_ATTENDANCE_SESSION_SUBJECT = "attendance_session_subject";

	private static final String KEY_SESSION_ID = "attendance_session_id";
	private static final String KEY_ATTENDANCE_STUDENT_ID = "attendance_student_id";
	private static final String KEY_ATTENDANCE_STATUS = "attendance_status";
	private static final String KEY_FACULTY_REG_ID = "faculty_reg_id";
	private static final String KEY_FACULTY_REG_STATUS = "faculty_reg_status";
	private static final String KEY_FACULTY_MOBILENUMBER = "faculty_mobilenumber";

	// Add session column to attendance tables
	private static final String KEY_SESSION = "session";


	public DBAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override

	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop all existing tables
		db.execSQL("DROP TABLE IF EXISTS " + STUDENT_INFO_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + FACULTY_INFO_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_SESSION_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + FACULTY_REGISTRATION_TABLE);

		// Recreate all tables
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		// Student table
		String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + STUDENT_INFO_TABLE + "("
				+ KEY_STUDENT_ENROLLMENT + " TEXT PRIMARY KEY, "
				+ KEY_STUDENT_FIRSTNAME + " TEXT, "
				+ KEY_STUDENT_LASTNAME + " TEXT, "
				+ KEY_STUDENT_MO_NO + " TEXT, "
				+ KEY_STUDENT_ADDRESS + " TEXT, "
				+ KEY_STUDENT_DEPARTMENT + " TEXT, "
				+ KEY_STUDENT_CLASS + " TEXT" + ")";
		db.execSQL(CREATE_STUDENT_TABLE);

		// Faculty table
		String CREATE_FACULTY_TABLE = "CREATE TABLE IF NOT EXISTS " + FACULTY_INFO_TABLE + "("
				+ KEY_FACULTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_FACULTY_FIRSTNAME + " TEXT, "
				+ KEY_FACULTY_LASTNAME + " TEXT, "
				+ KEY_FACULTY_MO_NO + " TEXT, "
				+ KEY_FACULTY_ADDRESS + " TEXT, "
				+ KEY_FACULTY_USERNAME + " TEXT, "
				+ KEY_FACULTY_PASSWORD + " TEXT, "
				+ KEY_FACULTY_SUBJECT + " TEXT" + ")";
		db.execSQL(CREATE_FACULTY_TABLE);

		// Attendance session table
		String CREATE_ATTENDANCE_SESSION_TABLE = "CREATE TABLE " + ATTENDANCE_SESSION_TABLE + "("
				+ KEY_ATTENDANCE_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_ATTENDANCE_SESSION_FACULTY_ID + " INTEGER, "
				+ KEY_ATTENDANCE_SESSION_DEPARTMENT + " TEXT, "
				+ KEY_ATTENDANCE_SESSION_CLASS + " TEXT, "
				+ KEY_ATTENDANCE_SESSION_DATE + " DATE, "
				+ KEY_ATTENDANCE_SESSION_SUBJECT + " TEXT, "
				+ KEY_SESSION + " TEXT" + ")";
		db.execSQL(CREATE_ATTENDANCE_SESSION_TABLE);

		// Attendance table
		String CREATE_ATTENDANCE_TABLE = "CREATE TABLE " + ATTENDANCE_TABLE + "("
				+ KEY_SESSION_ID + " INTEGER, "
				+ KEY_ATTENDANCE_STUDENT_ID + " TEXT, "
				+ KEY_ATTENDANCE_STATUS + " TEXT, "
				+ KEY_SESSION + " TEXT" + ")";
		db.execSQL(CREATE_ATTENDANCE_TABLE);

		// Faculty registration table
		String CREATE_FACULTY_REGISTRATION_TABLE = "CREATE TABLE IF NOT EXISTS " + FACULTY_REGISTRATION_TABLE + "("
				+ KEY_FACULTY_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_FACULTY_FIRSTNAME + " TEXT, "
				+ KEY_FACULTY_LASTNAME + " TEXT, "
				+ KEY_FACULTY_MO_NO + " TEXT, "
				+ KEY_FACULTY_ADDRESS + " TEXT, "
				+ KEY_FACULTY_USERNAME + " TEXT, "
				+ KEY_FACULTY_PASSWORD + " TEXT, "
				+ KEY_FACULTY_SUBJECT + " TEXT, "
				+ KEY_FACULTY_REG_STATUS + " TEXT" + ")";
		db.execSQL(CREATE_FACULTY_REGISTRATION_TABLE);
	}

	//facult crud
	public void addFaculty(FacultyBean facultyBean) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "INSERT INTO faculty_table (faculty_firstname,faculty_Lastname,faculty_mobilenumber,faculty_address,faculty_username,faculty_password) values ('"+ 
				facultyBean.getFaculty_firstname()+"', '"+
				facultyBean.getFaculty_lastname()+"', '"+
				facultyBean.getFaculty_mobilenumber()+"', '"+
				facultyBean.getFaculty_address()+"', '"+
				facultyBean.getFaculty_username()+"', '"+
				facultyBean.getFaculty_password()+"')";
		Log.d("query", query);
		db.execSQL(query);
		db.close();
	}

	public FacultyBean validateFaculty(String userName,String password)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		String query = "SELECT * FROM faculty_table where faculty_username='"+userName+"' and faculty_password='"+password+"'";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			
				FacultyBean facultyBean = new FacultyBean();
				facultyBean.setFaculty_id(Integer.parseInt(cursor.getString(0)));
				facultyBean.setFaculty_firstname(cursor.getString(1));
				cursor.getString(2);
				facultyBean.setFaculty_mobilenumber(cursor.getString(3));
				facultyBean.setFaculty_address(cursor.getString(4));
				facultyBean.setFaculty_username(cursor.getString(5));
				facultyBean.setFaculty_password(cursor.getString(6));
				return facultyBean;
		}
		return null;
	}

	public ArrayList<FacultyBean> getAllFaculty() {
		ArrayList<FacultyBean> list = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + FACULTY_INFO_TABLE;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				FacultyBean faculty = new FacultyBean();
				faculty.setFaculty_id(cursor.getInt(cursor.getColumnIndex(KEY_FACULTY_ID)));
				faculty.setFaculty_firstname(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_FIRSTNAME)));
				faculty.setFaculty_lastname(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_LASTNAME)));
				faculty.setFaculty_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_MO_NO)));
				faculty.setFaculty_address(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_ADDRESS)));
				faculty.setFaculty_username(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_USERNAME)));
				faculty.setFaculty_password(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_PASSWORD)));
				faculty.setFaculty_subject(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_SUBJECT)));
				list.add(faculty);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public boolean deleteFaculty(int facultyId) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			// First delete all attendance sessions by this faculty
			db.delete(ATTENDANCE_SESSION_TABLE, 
				KEY_ATTENDANCE_SESSION_FACULTY_ID + "=?",
				new String[]{String.valueOf(facultyId)});
			
			// Then delete the faculty
			int result = db.delete(FACULTY_INFO_TABLE, 
				KEY_FACULTY_ID + "=?",
				new String[]{String.valueOf(facultyId)});
			
			return result > 0;
		} catch (Exception e) {
			Log.e("DBAdapter", "Error deleting faculty: " + e.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	//student crud
	public long addStudent(StudentBean studentBean) {
		SQLiteDatabase db = this.getWritableDatabase();
		long result = -1;

		try {
			ContentValues values = new ContentValues();
			values.put(KEY_STUDENT_ENROLLMENT, studentBean.getStudent_enrollment());
			values.put(KEY_STUDENT_FIRSTNAME, studentBean.getStudent_firstname());
			values.put(KEY_STUDENT_LASTNAME, studentBean.getStudent_lastname());
			values.put(KEY_STUDENT_MO_NO, studentBean.getStudent_mobilenumber());
			values.put(KEY_STUDENT_ADDRESS, studentBean.getStudent_address());
			values.put(KEY_STUDENT_DEPARTMENT, studentBean.getStudent_department());
			values.put(KEY_STUDENT_CLASS, studentBean.getStudent_class());

			result = db.insertOrThrow(STUDENT_INFO_TABLE, null, values);
			Log.d("DBAdapter", "Added student with enrollment: " + studentBean.getStudent_enrollment());
		} catch (SQLiteConstraintException e) {
			Log.e("DBAdapter", "Error adding student: Enrollment number already exists", e);
		} finally {
			db.close();
		}
		return result;
	}

	public ArrayList<StudentBean> getAllStudent()
	{
		ArrayList<StudentBean> list = new ArrayList<StudentBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM student_table";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			do{
				StudentBean studentBean = new StudentBean();
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ID)));
				studentBean.setStudent_firstname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_FIRSTNAME)));
				studentBean.setStudent_lastname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_LASTNAME)));
				studentBean.setStudent_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_MO_NO)));
				studentBean.setStudent_address(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ADDRESS)));
				studentBean.setStudent_department(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_DEPARTMENT)));
				studentBean.setStudent_class(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_CLASS)));
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
				list.add(studentBean);
			}while(cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<StudentBean> getAllStudentByBranchYear(String branch, String year) {
		ArrayList<StudentBean> list = new ArrayList<StudentBean>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + STUDENT_INFO_TABLE + 
					  " WHERE " + KEY_STUDENT_DEPARTMENT + "=? AND " + KEY_STUDENT_CLASS + "=?";
		Cursor cursor = db.rawQuery(query, new String[]{branch, year});

		if (cursor.moveToFirst()) {
			do {
				StudentBean studentBean = new StudentBean();
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
				studentBean.setStudent_firstname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_FIRSTNAME)));
				studentBean.setStudent_lastname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_LASTNAME)));
				studentBean.setStudent_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_MO_NO)));
				studentBean.setStudent_address(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ADDRESS)));
				studentBean.setStudent_department(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_DEPARTMENT)));
				studentBean.setStudent_class(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_CLASS)));
				
				list.add(studentBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public StudentBean getStudentById(String studentId) {
		StudentBean studentBean = new StudentBean();
		
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM student_table where student_enrollment=?";
		Cursor cursor = db.rawQuery(query, new String[]{studentId});

		if(cursor.moveToFirst()) {
			do {
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
				studentBean.setStudent_firstname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_FIRSTNAME)));
				studentBean.setStudent_lastname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_LASTNAME)));
				studentBean.setStudent_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_MO_NO)));
				studentBean.setStudent_address(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ADDRESS)));
				studentBean.setStudent_department(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_DEPARTMENT)));
				studentBean.setStudent_class(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_CLASS)));
			} while(cursor.moveToNext());
		}
		return studentBean;
	}

	public void deleteStudent(String enrollment) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(STUDENT_INFO_TABLE, KEY_STUDENT_ENROLLMENT + " = ?", new String[] { enrollment });
		db.close();
	}

	//attendance session Table crud
	public long addAttendanceSession(AttendanceSessionBean attendanceSessionBean, String session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_ATTENDANCE_SESSION_FACULTY_ID, attendanceSessionBean.getAttendance_session_faculty_id());
		values.put(KEY_ATTENDANCE_SESSION_DEPARTMENT, attendanceSessionBean.getAttendance_session_department());
		values.put(KEY_ATTENDANCE_SESSION_CLASS, attendanceSessionBean.getAttendance_session_class());
		values.put(KEY_ATTENDANCE_SESSION_DATE, attendanceSessionBean.getAttendance_session_date());
		values.put(KEY_ATTENDANCE_SESSION_SUBJECT, attendanceSessionBean.getAttendance_session_subject());
		values.put(KEY_SESSION, session);

		return db.insert(ATTENDANCE_SESSION_TABLE, null, values);
	}

	public ArrayList<AttendanceSessionBean> getAllAttendanceSession() {
		ArrayList<AttendanceSessionBean> list = new ArrayList<AttendanceSessionBean>();

		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + ATTENDANCE_SESSION_TABLE;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				AttendanceSessionBean sessionBean = new AttendanceSessionBean();
				sessionBean.setAttendance_session_id(cursor.getInt(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_ID)));
				sessionBean.setAttendance_session_faculty_id(cursor.getInt(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_FACULTY_ID)));
				sessionBean.setAttendance_session_department(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_DEPARTMENT)));
				sessionBean.setAttendance_session_class(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_CLASS)));
				sessionBean.setAttendance_session_date(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_DATE)));
				sessionBean.setAttendance_session_subject(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_SUBJECT)));
				list.add(sessionBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void deleteAttendanceSession(int attendanceSessionId) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "DELETE FROM attendance_session_table WHERE attendance_session_id="+attendanceSessionId ;

		Log.d("query", query);
		db.execSQL(query);
		db.close();
	}
	//attendance crud
	public void addNewAttendance(AttendanceBean attendanceBean) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "INSERT INTO attendance_table values ("+ 
				attendanceBean.getAttendance_session_id()+", "+
				attendanceBean.getAttendance_student_id()+", '"+
				attendanceBean.getAttendance_status()+"')";
		Log.d("query", query);
		db.execSQL(query);
		db.close();
	}
	
	
	public ArrayList<AttendanceBean> getAttendanceBySessionID(AttendanceSessionBean sessionBean) {
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + ATTENDANCE_TABLE + 
					  " WHERE " + KEY_SESSION_ID + " = ?";
		
		Cursor cursor = db.rawQuery(query, 
			new String[]{String.valueOf(sessionBean.getAttendance_session_id())});

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_session_id(cursor.getLong(cursor.getColumnIndex(KEY_SESSION_ID)));
				attendanceBean.setAttendance_student_id(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_STUDENT_ID)));
				attendanceBean.setAttendance_status(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_STATUS)));
				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public ArrayList<AttendanceBean> getAttendanceBySessionIDAndSession(long sessionId, String session) {
		ArrayList<AttendanceBean> list = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT a.*, ast.attendance_session_date, ast.attendance_session_subject " +
					  "FROM " + ATTENDANCE_TABLE + " a " +
					  "JOIN " + ATTENDANCE_SESSION_TABLE + " ast " +
					  "ON a.attendance_session_id = ast.attendance_session_id " +
					  "WHERE ast.attendance_session_id = ? AND " +
					  "a." + KEY_SESSION + " = ?";
		
		Log.d("DBAdapter", "Querying attendance with sessionId: " + sessionId + ", session: " + session);
		
		Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId), session});
		Log.d("DBAdapter", "Found " + cursor.getCount() + " records");

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_session_id(cursor.getLong(cursor.getColumnIndex(KEY_SESSION_ID)));
				attendanceBean.setAttendance_student_id(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_STUDENT_ID)));
				attendanceBean.setAttendance_status(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_STATUS)));
				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	public ArrayList<AttendanceBean> getTotalAttendanceBySessionID(AttendanceSessionBean attendanceSessionBean)
	{
		int attendanceSessionId=0;
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM attendance_session_table where attendance_session_faculty_id="+attendanceSessionBean.getAttendance_session_faculty_id()+""
				+" AND attendance_session_department='"+attendanceSessionBean.getAttendance_session_department()+"' AND attendance_session_class='"+attendanceSessionBean.getAttendance_session_class()+"'" +
						" AND attendance_session_subject='"+attendanceSessionBean.getAttendance_session_subject()+"'";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			do{
				attendanceSessionId=(Integer.parseInt(cursor.getString(0)));
				
				String query1="SELECT * FROM attendance_table where attendance_session_id=" + attendanceSessionId+" order by attendance_student_id";
				Cursor cursor1 = db.rawQuery(query1, null);
				if(cursor1.moveToFirst()) 
				{
					do{
						AttendanceBean attendanceBean = new AttendanceBean();
						attendanceBean.setAttendance_session_id(Integer.parseInt(cursor1.getString(0)));
						attendanceBean.setAttendance_student_id(cursor1.getString(1));
						attendanceBean.setAttendance_status(cursor1.getString(2));
						list.add(attendanceBean);

					}while(cursor1.moveToNext());
				}
				
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_session_id(0);
				attendanceBean.setAttendance_status("Date : " + cursor.getString(4));
				list.add(attendanceBean);
				
			}while(cursor.moveToNext());
		}
		
		
		return list;
	}
	
	public ArrayList<AttendanceBean> getAllAttendanceByStudent() {
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT attendance_student_id,count(*) FROM attendance_table where attendance_status='P' group by attendance_student_id";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_student_id(cursor.getString(0));
				attendanceBean.setAttendance_session_id(cursor.getLong(1));
				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<AttendanceBean> getShortAttendanceBySessionID(AttendanceSessionBean attendanceSessionBean, String startDate, String endDate) {
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT a.attendance_student_id, s.student_firstname, s.student_lastname, " +
				"SUM(CASE WHEN a.attendance_status = 'P' THEN 1 ELSE 0 END) as present_count, " +
				"COUNT(*) as total_count " +
				"FROM attendance_table a " +
				"JOIN attendance_session_table ast ON a.attendance_session_id = ast.attendance_session_id " +
				"JOIN student_table s ON a.attendance_student_id = s.student_enrollment " +
				"WHERE ast.attendance_session_faculty_id = ? " +
				"AND ast.attendance_session_department = ? " +
				"AND ast.attendance_session_class = ? " +
				"AND ast.attendance_session_subject = ? " +
				"AND ast.attendance_session_date BETWEEN ? AND ? " +
				"GROUP BY a.attendance_student_id " +
				"HAVING (CAST(present_count AS FLOAT) / total_count) < 0.75";

		Cursor cursor = db.rawQuery(query, new String[]{
				String.valueOf(attendanceSessionBean.getAttendance_session_faculty_id()),
				attendanceSessionBean.getAttendance_session_department(),
				attendanceSessionBean.getAttendance_session_class(),
				attendanceSessionBean.getAttendance_session_subject(),
				startDate,
				endDate
		});

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_student_id(cursor.getString(0));
				attendanceBean.setStudent_firstname(cursor.getString(1));
				attendanceBean.setStudent_lastname(cursor.getString(2));
				float attendancePercentage = cursor.getInt(3) * 100f / cursor.getInt(4);
				attendanceBean.setAttendance_session_id(Math.round(attendancePercentage));
				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void updateAttendance(AttendanceBean attendanceBean) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ATTENDANCE_STATUS, attendanceBean.getAttendance_status());

		db.update(ATTENDANCE_TABLE, values, 
				  KEY_SESSION_ID + " = ? AND " + KEY_ATTENDANCE_STUDENT_ID + " = ?",
				  new String[] { String.valueOf(attendanceBean.getAttendance_session_id()),
								 String.valueOf(attendanceBean.getAttendance_student_id()) });
		db.close();
	}

	public ArrayList<AttendanceBean> getShortAttendanceByDateRange(String startDate, String endDate) {
		ArrayList<AttendanceBean> list = new ArrayList<>();

		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT a.attendance_student_id, " +
				"SUM(CASE WHEN a.attendance_status = 'P' THEN 1 ELSE 0 END) as present_count, " +
				"COUNT(*) as total_count " +
				"FROM attendance_table a " +
				"JOIN attendance_session_table ast ON a.attendance_session_id = ast.attendance_session_id " +
				"WHERE ast.attendance_session_date BETWEEN ? AND ? " +
				"GROUP BY a.attendance_student_id " +
				"HAVING (CAST(present_count AS FLOAT) / total_count) < 0.75";

		Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_student_id(cursor.getString(0));
				int presentCount = cursor.getInt(1);
				int totalCount = cursor.getInt(2);
				float attendancePercentage = (float) presentCount / totalCount * 100;
				attendanceBean.setAttendance_session_id(Math.round(attendancePercentage * 100)); // Store as integer with two decimal places
				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public long addFacultyRegistrationRequest(FacultyBean facultyBean) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FACULTY_FIRSTNAME, facultyBean.getFaculty_firstname());
		values.put(KEY_FACULTY_LASTNAME, facultyBean.getFaculty_lastname());
		values.put(KEY_FACULTY_MO_NO, facultyBean.getFaculty_mobilenumber());
		values.put(KEY_FACULTY_ADDRESS, facultyBean.getFaculty_address());
		values.put(KEY_FACULTY_USERNAME, facultyBean.getFaculty_username());
		values.put(KEY_FACULTY_PASSWORD, facultyBean.getFaculty_password());
		values.put(KEY_FACULTY_SUBJECT, facultyBean.getFaculty_subject());
		values.put(KEY_FACULTY_REG_STATUS, "pending");

		long newRowId = db.insert(FACULTY_REGISTRATION_TABLE, null, values);
		db.close();
		return newRowId;
	}

	public ArrayList<FacultyBean> getPendingFacultyRegistrations() {
		ArrayList<FacultyBean> list = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + FACULTY_REGISTRATION_TABLE + " WHERE " + KEY_FACULTY_REG_STATUS + "='pending'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				FacultyBean faculty = new FacultyBean();
				faculty.setFaculty_id(cursor.getInt(cursor.getColumnIndex(KEY_FACULTY_REG_ID)));
				faculty.setFaculty_firstname(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_FIRSTNAME)));
				faculty.setFaculty_lastname(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_LASTNAME)));
				faculty.setFaculty_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_MO_NO)));
				faculty.setFaculty_address(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_ADDRESS)));
				faculty.setFaculty_username(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_USERNAME)));
				faculty.setFaculty_password(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_PASSWORD)));
				faculty.setFaculty_subject(cursor.getString(cursor.getColumnIndex(KEY_FACULTY_SUBJECT)));
				list.add(faculty);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public boolean approveFacultyRegistration(int facultyId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FACULTY_REG_STATUS, "approved");
		
		int rowsAffected = db.update(FACULTY_REGISTRATION_TABLE, values, KEY_FACULTY_REG_ID + " = ?", 
									 new String[]{String.valueOf(facultyId)});
		
		// If the update was successful, move the faculty to the main faculty table
		if (rowsAffected > 0) {
			String selectQuery = "SELECT * FROM " + FACULTY_REGISTRATION_TABLE + " WHERE " + KEY_FACULTY_REG_ID + " = ?";
			Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(facultyId)});
			
			if (cursor.moveToFirst()) {
				ContentValues facultyValues = new ContentValues();
				facultyValues.put(KEY_FACULTY_FIRSTNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_FIRSTNAME)));
				facultyValues.put(KEY_FACULTY_LASTNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_LASTNAME)));
				facultyValues.put(KEY_FACULTY_MO_NO, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_MO_NO)));
				facultyValues.put(KEY_FACULTY_ADDRESS, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_ADDRESS)));
				facultyValues.put(KEY_FACULTY_USERNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_USERNAME)));
				facultyValues.put(KEY_FACULTY_PASSWORD, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_PASSWORD)));
				facultyValues.put(KEY_FACULTY_SUBJECT, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_SUBJECT)));
				
				db.insert(FACULTY_INFO_TABLE, null, facultyValues);
			}
			cursor.close();
		}
		
		return rowsAffected > 0;
	}

	// Add method to get student by enrollment number
	public StudentBean getStudentByEnrollment(String enrollment) {
		SQLiteDatabase db = this.getReadableDatabase();
		StudentBean studentBean = null;

		String query = "SELECT * FROM " + STUDENT_INFO_TABLE + 
					  " WHERE " + KEY_STUDENT_ENROLLMENT + "=?";
		
		Log.d("DBAdapter", "Searching for enrollment: " + enrollment);
		
		Cursor cursor = db.rawQuery(query, new String[]{enrollment});

		if (cursor.moveToFirst()) {
			studentBean = new StudentBean();
			studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
			studentBean.setStudent_firstname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_FIRSTNAME)));
			studentBean.setStudent_lastname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_LASTNAME)));
			studentBean.setStudent_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_MO_NO)));
			
			Log.d("DBAdapter", "Found student: " + studentBean.getStudent_firstname() + 
							  " with enrollment: " + studentBean.getStudent_enrollment());
		}
		cursor.close();
		return studentBean;
	}

	// Add method to get students by branch, year and enrollment pattern
	public ArrayList<StudentBean> getStudentsByBranchYearEnrollment(String branch, String year, String enrollmentPattern) {
		ArrayList<StudentBean> list = new ArrayList<StudentBean>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + STUDENT_INFO_TABLE + 
					  " WHERE " + KEY_STUDENT_DEPARTMENT + "=? AND " + 
					  KEY_STUDENT_CLASS + "=? AND " +
					  KEY_STUDENT_ENROLLMENT + " LIKE ?";
		
		Log.d("DBAdapter", "Searching with params - Branch: " + branch + 
						  ", Year: " + year + 
						  ", Enrollment Pattern: " + enrollmentPattern);
		
		Cursor cursor = db.rawQuery(query, 
			new String[]{branch, year, "%" + enrollmentPattern + "%"});

		if (cursor.moveToFirst()) {
			do {
				StudentBean studentBean = new StudentBean();
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
				studentBean.setStudent_firstname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_FIRSTNAME)));
				studentBean.setStudent_lastname(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_LASTNAME)));
				studentBean.setStudent_mobilenumber(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_MO_NO)));
				studentBean.setStudent_address(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ADDRESS)));
				studentBean.setStudent_department(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_DEPARTMENT)));
				studentBean.setStudent_class(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_CLASS)));
				studentBean.setStudent_enrollment(cursor.getString(cursor.getColumnIndex(KEY_STUDENT_ENROLLMENT)));
				
				Log.d("DBAdapter", "Found student: " + studentBean.getStudent_firstname() + 
								  " with enrollment: " + studentBean.getStudent_enrollment());
				
				list.add(studentBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public boolean isEnrollmentExists(String enrollment) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT COUNT(*) FROM " + STUDENT_INFO_TABLE + 
					  " WHERE " + KEY_STUDENT_ENROLLMENT + "=?";
		
		Cursor cursor = db.rawQuery(query, new String[]{enrollment});
		
		if (cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			cursor.close();
			return count > 0;
		}
		cursor.close();
		return false;
	}

	public void addAttendanceWithSession(AttendanceBean attendanceBean, String session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_SESSION_ID, attendanceBean.getAttendance_session_id());
		values.put(KEY_ATTENDANCE_STUDENT_ID, attendanceBean.getAttendance_student_id());
		values.put(KEY_ATTENDANCE_STATUS, attendanceBean.getAttendance_status());
		values.put(KEY_SESSION, session);

		Log.d("DBAdapter", "Adding attendance record - SessionID: " + 
			attendanceBean.getAttendance_session_id() + ", StudentID: " + 
			attendanceBean.getAttendance_student_id() + ", Status: " + 
			attendanceBean.getAttendance_status() + ", Session: " + session);

		long result = db.insert(ATTENDANCE_TABLE, null, values);
		Log.d("DBAdapter", "Insert result: " + result);
		
		db.close();
	}

	public void updateAttendanceWithSession(AttendanceBean attendanceBean, String session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_ATTENDANCE_STATUS, attendanceBean.getAttendance_status());

		String whereClause = KEY_SESSION_ID + " = ? AND " + 
						   KEY_ATTENDANCE_STUDENT_ID + " = ? AND " +
						   KEY_SESSION + " = ?";
						   
		String[] whereArgs = new String[] {
			String.valueOf(attendanceBean.getAttendance_session_id()),
			attendanceBean.getAttendance_student_id(),
			session
		};

		db.update(ATTENDANCE_TABLE, values, whereClause, whereArgs);
		db.close();
	}

	public ArrayList<AttendanceSessionBean> getAttendanceSessionsByFacultyAndSubject(
			int facultyId, String branch, String year, String subject, String session) {
		
		ArrayList<AttendanceSessionBean> list = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + ATTENDANCE_SESSION_TABLE + 
					  " WHERE " + KEY_ATTENDANCE_SESSION_FACULTY_ID + " = ? AND " +
					  KEY_ATTENDANCE_SESSION_DEPARTMENT + " = ? AND " +
					  KEY_ATTENDANCE_SESSION_CLASS + " = ? AND " +
					  KEY_ATTENDANCE_SESSION_SUBJECT + " = ? AND " +
					  KEY_SESSION + " = ?";
		
		Log.d("DBAdapter", "Querying sessions for faculty: " + facultyId + 
			", branch: " + branch + ", year: " + year + 
			", subject: " + subject + ", session: " + session);
		
		Cursor cursor = db.rawQuery(query, new String[]{
			String.valueOf(facultyId), branch, year, subject, session
		});
		
		Log.d("DBAdapter", "Found " + cursor.getCount() + " sessions");

		if (cursor.moveToFirst()) {
			do {
				AttendanceSessionBean sessionBean = new AttendanceSessionBean();
				sessionBean.setAttendance_session_id(cursor.getLong(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_ID)));
				sessionBean.setAttendance_session_faculty_id(cursor.getInt(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_FACULTY_ID)));
				sessionBean.setAttendance_session_department(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_DEPARTMENT)));
				sessionBean.setAttendance_session_class(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_CLASS)));
				sessionBean.setAttendance_session_date(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_DATE)));
				sessionBean.setAttendance_session_subject(cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_SUBJECT)));
				
				Log.d("DBAdapter", "Found session with ID: " + sessionBean.getAttendance_session_id());
				
				list.add(sessionBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public ArrayList<AttendanceBean> getAllAttendanceByBranchYear(String branch, String year) {
		ArrayList<AttendanceBean> list = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT a.*, ast.attendance_session_date, ast.attendance_session_subject, " +
					  "f.faculty_firstname, f.faculty_lastname " +
					  "FROM " + ATTENDANCE_TABLE + " a " +
					  "JOIN " + ATTENDANCE_SESSION_TABLE + " ast ON a.attendance_session_id = ast.attendance_session_id " +
					  "JOIN " + FACULTY_INFO_TABLE + " f ON ast.attendance_session_faculty_id = f.faculty_id " +
					  "WHERE ast.attendance_session_department = ? " +
					  "AND ast.attendance_session_class = ? " +
					  "ORDER BY ast.attendance_session_date DESC, ast.attendance_session_subject ASC";
		
		Log.d("DBAdapter", "Query: " + query);
		Log.d("DBAdapter", "Branch: " + branch + ", Year: " + year);
		
		Cursor cursor = db.rawQuery(query, new String[]{branch, year});
		Log.d("DBAdapter", "Found " + cursor.getCount() + " records");

		if (cursor.moveToFirst()) {
			do {
				AttendanceBean attendanceBean = new AttendanceBean();
				
				// Get column indices first
				int sessionIdCol = cursor.getColumnIndex(KEY_SESSION_ID);
				int studentIdCol = cursor.getColumnIndex(KEY_ATTENDANCE_STUDENT_ID);
				int statusCol = cursor.getColumnIndex(KEY_ATTENDANCE_STATUS);
				int dateCol = cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_DATE);
				int subjectCol = cursor.getColumnIndex(KEY_ATTENDANCE_SESSION_SUBJECT);
				int firstNameCol = cursor.getColumnIndex(KEY_FACULTY_FIRSTNAME);
				int lastNameCol = cursor.getColumnIndex(KEY_FACULTY_LASTNAME);

				// Log column indices for debugging
				Log.d("DBAdapter", "Column indices - SessionID: " + sessionIdCol + 
								 ", StudentID: " + studentIdCol +
								 ", Status: " + statusCol +
								 ", Date: " + dateCol +
								 ", Subject: " + subjectCol +
								 ", FirstName: " + firstNameCol +
								 ", LastName: " + lastNameCol);

				// Only set values if column exists
				if (sessionIdCol != -1) 
					attendanceBean.setAttendance_session_id(cursor.getLong(sessionIdCol));
				if (studentIdCol != -1) 
					attendanceBean.setAttendance_student_id(cursor.getString(studentIdCol));
				if (statusCol != -1) 
					attendanceBean.setAttendance_status(cursor.getString(statusCol));
				if (dateCol != -1) 
					attendanceBean.setAttendance_session_date(cursor.getString(dateCol));
				if (subjectCol != -1) 
					attendanceBean.setSubject(cursor.getString(subjectCol));
				
				// Combine faculty name
				String facultyName = "";
				if (firstNameCol != -1) 
					facultyName += cursor.getString(firstNameCol);
				if (lastNameCol != -1) 
					facultyName += " " + cursor.getString(lastNameCol);
				attendanceBean.setFaculty_name(facultyName.trim());

				list.add(attendanceBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public int[] getAttendanceCountForSession(String studentId, long sessionId) {
		SQLiteDatabase db = this.getReadableDatabase();
		int[] counts = new int[2]; // [present_count, total_count]
		
		String query = "SELECT " +
					  "SUM(CASE WHEN a.attendance_status = 'P' THEN 1 ELSE 0 END) as present_count, " +
					  "COUNT(*) as total_count " +
					  "FROM " + ATTENDANCE_TABLE + " a " +
					  "WHERE a.attendance_student_id = ? " +
					  "AND a.attendance_session_id = ?";
		
		Cursor cursor = db.rawQuery(query, new String[]{
			studentId, String.valueOf(sessionId)
		});

		if (cursor.moveToFirst()) {
			counts[0] = cursor.getInt(0); // present_count
			counts[1] = cursor.getInt(1); // total_count
		}
		cursor.close();
		return counts;
	}

	public int[] getTotalAttendanceCount(String studentId, String subject, String session) {
		SQLiteDatabase db = this.getReadableDatabase();
		int[] counts = new int[2]; // [present_count, total_count]
		
		String query = "SELECT " +
					  "SUM(CASE WHEN a.attendance_status = 'P' THEN 1 ELSE 0 END) as present_count, " +
					  "COUNT(*) as total_count " +
					  "FROM " + ATTENDANCE_TABLE + " a " +
					  "JOIN " + ATTENDANCE_SESSION_TABLE + " ast ON a.attendance_session_id = ast.attendance_session_id " +
					  "WHERE a.attendance_student_id = ? " +
					  "AND ast.attendance_session_subject = ? " +
					  "AND a." + KEY_SESSION + " = ?";
		
		Log.d("DBAdapter", "Query: " + query);
		Log.d("DBAdapter", "Parameters - StudentID: " + studentId + 
							", Subject: " + subject + 
							", Session: " + session);
		
		Cursor cursor = db.rawQuery(query, new String[]{studentId, subject, session});
		Log.d("DBAdapter", "Found " + cursor.getCount() + " records");

		if (cursor.moveToFirst()) {
			// Check for null values
			int presentCount = cursor.isNull(0) ? 0 : cursor.getInt(0);
			int totalCount = cursor.isNull(1) ? 0 : cursor.getInt(1);
			
			counts[0] = presentCount;
			counts[1] = totalCount;
			
			Log.d("DBAdapter", "Present: " + counts[0] + ", Total: " + counts[1]);
		} else {
			Log.d("DBAdapter", "No records found");
			counts[0] = 0;
			counts[1] = 0;
		}
		cursor.close();
		return counts;
	}

}
