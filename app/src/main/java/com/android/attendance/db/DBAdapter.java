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

public class DBAdapter extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2; // Increment this

	// Database Name
	private static final String DATABASE_NAME = "Attendance";

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

	private static final String KEY_STUDENT_ID = "student_id";
	private static final String KEY_STUDENT_FIRSTNAME = "student_firstname";
	private static final String KEY_STUDENT_LASTNAME = "student_lastname";
	private static final String KEY_STUDENT_MO_NO = "student_mobilenumber";
	private static final String KEY_STUDENT_ADDRESS = "student_address";
	private static final String KEY_STUDENT_DEPARTMENT = "student_department";
	private static final String KEY_STUDENT_CLASS = "student_class";

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


	public DBAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override

	public void onCreate(SQLiteDatabase db) {
		String queryFaculty="CREATE TABLE "+ FACULTY_INFO_TABLE +" (" +
				KEY_FACULTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_FACULTY_FIRSTNAME + " TEXT, " + 
				KEY_FACULTY_LASTNAME + " TEXT, " +
				KEY_FACULTY_MO_NO + " TEXT, " +
				KEY_FACULTY_ADDRESS + " TEXT," +
				KEY_FACULTY_USERNAME + " TEXT," +
				KEY_FACULTY_PASSWORD + " TEXT " + ")";
		Log.d("queryFaculty",queryFaculty);


		String queryStudent="CREATE TABLE "+ STUDENT_INFO_TABLE +" (" +
				KEY_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_STUDENT_FIRSTNAME + " TEXT, " + 
				KEY_STUDENT_LASTNAME + " TEXT, " +
				KEY_STUDENT_MO_NO + " TEXT, " +
				KEY_STUDENT_ADDRESS + " TEXT," +
				KEY_STUDENT_DEPARTMENT + " TEXT," +
				KEY_STUDENT_CLASS + " TEXT " + ")";
		Log.d("queryStudent",queryStudent );


		String queryAttendanceSession="CREATE TABLE "+ ATTENDANCE_SESSION_TABLE +" (" +
				KEY_ATTENDANCE_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_ATTENDANCE_SESSION_FACULTY_ID + " INTEGER, " + 
				KEY_ATTENDANCE_SESSION_DEPARTMENT + " TEXT, " +
				KEY_ATTENDANCE_SESSION_CLASS + " TEXT, " +
				KEY_ATTENDANCE_SESSION_DATE + " DATE," +
				KEY_ATTENDANCE_SESSION_SUBJECT + " TEXT" + ")";
		Log.d("queryAttendanceSession",queryAttendanceSession );


		String queryAttendance="CREATE TABLE "+ ATTENDANCE_TABLE +" (" +
				KEY_SESSION_ID + " INTEGER, " +
				KEY_ATTENDANCE_STUDENT_ID + " INTEGER, " +
				KEY_ATTENDANCE_STATUS + " TEXT " + ")";
		Log.d("queryAttendance",queryAttendance );

		ensureFacultyRegistrationTableExists();

		try
		{
			db.execSQL(queryFaculty);
			db.execSQL(queryStudent);
			db.execSQL(queryAttendanceSession);
			db.execSQL(queryAttendance);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());
		}

	} 


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String queryFaculty="CREATE TABLE "+ FACULTY_INFO_TABLE +" (" +
				KEY_FACULTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_FACULTY_FIRSTNAME + " TEXT, " + 
					KEY_FACULTY_LASTNAME + " TEXT, " +
					KEY_FACULTY_MO_NO + " TEXT, " +
					KEY_FACULTY_ADDRESS + " TEXT," +
					KEY_FACULTY_USERNAME + " TEXT," +
					KEY_FACULTY_PASSWORD + " TEXT " + ")";
		Log.d("queryFaculty",queryFaculty);


		String queryStudent="CREATE TABLE "+ STUDENT_INFO_TABLE +" (" +
				KEY_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_STUDENT_FIRSTNAME + " TEXT, " + 
				KEY_STUDENT_LASTNAME + " TEXT, " +
				KEY_STUDENT_MO_NO + " TEXT, " +
				KEY_STUDENT_ADDRESS + " TEXT," +
				KEY_STUDENT_DEPARTMENT + " TEXT," +
				KEY_STUDENT_CLASS + " TEXT " + ")";
		Log.d("queryStudent",queryStudent );


		String queryAttendanceSession="CREATE TABLE "+ ATTENDANCE_SESSION_TABLE +" (" +
				KEY_ATTENDANCE_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				KEY_ATTENDANCE_SESSION_FACULTY_ID + " INTEGER, " + 
				KEY_ATTENDANCE_SESSION_DEPARTMENT + " TEXT, " +
				KEY_ATTENDANCE_SESSION_CLASS + " TEXT, " +
				KEY_ATTENDANCE_SESSION_DATE + " TEXT," +
				KEY_ATTENDANCE_SESSION_SUBJECT + " TEXT" +")";
		Log.d("queryAttendanceSession",queryAttendanceSession );


		String queryAttendance="CREATE TABLE "+ ATTENDANCE_TABLE +" (" +
				KEY_SESSION_ID + " INTEGER, " +
				KEY_ATTENDANCE_STUDENT_ID + " INTEGER, " +
				KEY_ATTENDANCE_STATUS + " TEXT " + ")";
		Log.d("queryAttendance",queryAttendance );

		try
		{
			db.execSQL(queryFaculty);
			db.execSQL(queryStudent);
			db.execSQL(queryAttendanceSession);
			db.execSQL(queryAttendance);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());
		}		
	}

	public void ensureFacultyRegistrationTableExists() {
		SQLiteDatabase db = this.getWritableDatabase();
		createFacultyRegistrationTable(db);
	}

	private void createFacultyRegistrationTable(SQLiteDatabase db) {
		String CREATE_FACULTY_REGISTRATION_TABLE = "CREATE TABLE IF NOT EXISTS " + FACULTY_REGISTRATION_TABLE + "("
				+ KEY_FACULTY_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_FACULTY_FIRSTNAME + " TEXT,"
				+ KEY_FACULTY_LASTNAME + " TEXT,"
				+ KEY_FACULTY_MOBILENUMBER + " TEXT,"
				+ KEY_FACULTY_ADDRESS + " TEXT,"
				+ KEY_FACULTY_USERNAME + " TEXT,"
				+ KEY_FACULTY_PASSWORD + " TEXT,"
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
				facultyBean.setFaculty_lastname(cursor.getString(2));
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
				faculty.setFaculty_id(cursor.getInt(0));
				faculty.setFaculty_firstname(cursor.getString(1));
				faculty.setFaculty_lastname(cursor.getString(2));
				faculty.setFaculty_mobilenumber(cursor.getString(3));
				faculty.setFaculty_address(cursor.getString(4));
				faculty.setFaculty_username(cursor.getString(5));
				faculty.setFaculty_password(cursor.getString(6));
				list.add(faculty);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void deleteFaculty(int facultyId) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "DELETE FROM faculty_table WHERE faculty_id="+facultyId ;

		Log.d("query", query);
		db.execSQL(query);
		db.close();
	}

	//student crud
	public void addStudent(StudentBean studentBean) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_STUDENT_FIRSTNAME, studentBean.getStudent_firstname());
		values.put(KEY_STUDENT_LASTNAME, studentBean.getStudent_lastname());
		values.put(KEY_STUDENT_MO_NO, studentBean.getStudent_mobilenumber());
		values.put(KEY_STUDENT_ADDRESS, studentBean.getStudent_address());
		values.put(KEY_STUDENT_DEPARTMENT, studentBean.getStudent_department());
		values.put(KEY_STUDENT_CLASS, studentBean.getStudent_class());

		db.insert(STUDENT_INFO_TABLE, null, values);
		db.close();
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
				studentBean.setStudent_id(Integer.parseInt(cursor.getString(0)));
				studentBean.setStudent_firstname(cursor.getString(1));
				studentBean.setStudent_lastname(cursor.getString(2));
				studentBean.setStudent_mobilenumber(cursor.getString(3));
				studentBean.setStudent_address(cursor.getString(4));
				studentBean.setStudent_department(cursor.getString(5));
				studentBean.setStudent_class(cursor.getString(6));
				list.add(studentBean);
			}while(cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<StudentBean> getAllStudentByBranchYear(String branch,String year)
	{
		ArrayList<StudentBean> list = new ArrayList<StudentBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM student_table where student_department='"+branch+"' and student_class='"+year+"'";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			do{
				StudentBean studentBean = new StudentBean();
				
				studentBean.setStudent_id(Integer.parseInt(cursor.getString(0)));
				studentBean.setStudent_firstname(cursor.getString(1));
				studentBean.setStudent_lastname(cursor.getString(2));
				studentBean.setStudent_mobilenumber(cursor.getString(3));
				studentBean.setStudent_address(cursor.getString(4));
				studentBean.setStudent_department(cursor.getString(5));
				studentBean.setStudent_class(cursor.getString(6));
				list.add(studentBean);
			}while(cursor.moveToNext());
		}
		return list;
	}

	public StudentBean getStudentById(int studentId)
	{
		StudentBean studentBean = new StudentBean();
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM student_table where student_id="+studentId;
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			do{
				
				studentBean.setStudent_id(Integer.parseInt(cursor.getString(0)));
				studentBean.setStudent_firstname(cursor.getString(1));
				studentBean.setStudent_lastname(cursor.getString(2));
				studentBean.setStudent_mobilenumber(cursor.getString(3));
				studentBean.setStudent_address(cursor.getString(4));
				studentBean.setStudent_department(cursor.getString(5));
				studentBean.setStudent_class(cursor.getString(6));
				
			}while(cursor.moveToNext());
		}
		return studentBean;
	}

	public void deleteStudent(int studentId) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "DELETE FROM student_table WHERE student_id="+studentId ;

		Log.d("query", query);
		db.execSQL(query);
		db.close();
	}

	//attendance session Table crud
	public int addAttendanceSession(AttendanceSessionBean attendanceSessionBean) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "INSERT INTO attendance_session_table (attendance_session_faculty_id,attendance_session_department,attendance_session_class,attendance_session_date,attendance_session_subject) values ('"+ 
				attendanceSessionBean.getAttendance_session_faculty_id()+"', '"+
				attendanceSessionBean.getAttendance_session_department()+"','"+
				attendanceSessionBean.getAttendance_session_class()+"', '"+
				attendanceSessionBean.getAttendance_session_date()+"', '"+
				attendanceSessionBean.getAttendance_session_subject()+"')";
		Log.d("query", query);
		db.execSQL(query);

		String query1= "select max(attendance_session_id) from attendance_session_table";
		Cursor cursor = db.rawQuery(query1, null);
		
		if(cursor.moveToFirst())
		{
			int sessionId = Integer.parseInt(cursor.getString(0));
			
			return sessionId;
		}
			
		
		db.close();
		return 0;
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
	
	
	public ArrayList<AttendanceBean> getAttendanceBySessionID(AttendanceSessionBean attendanceSessionBean)
	{
		int attendanceSessionId=0;
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM attendance_session_table where attendance_session_faculty_id="+attendanceSessionBean.getAttendance_session_faculty_id()+""
				+" AND attendance_session_department='"+attendanceSessionBean.getAttendance_session_department()+"' AND attendance_session_class='"+attendanceSessionBean.getAttendance_session_class()+"'" +
						" AND attendance_session_date='"+attendanceSessionBean.getAttendance_session_date()+"' AND attendance_session_subject='"+attendanceSessionBean.getAttendance_session_subject()+"'";
		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()) 
		{
			do{
				attendanceSessionId=(Integer.parseInt(cursor.getString(0)));
			}while(cursor.moveToNext());
		}
		
		String query1="SELECT * FROM attendance_table where attendance_session_id=" + attendanceSessionId+" order by attendance_student_id";
		Cursor cursor1 = db.rawQuery(query1, null);
		if(cursor1.moveToFirst()) 
		{
			do{
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_session_id(Integer.parseInt(cursor1.getString(0)));
				attendanceBean.setAttendance_student_id(Integer.parseInt(cursor1.getString(1)));
				attendanceBean.setAttendance_status(cursor1.getString(2));
				list.add(attendanceBean);

			}while(cursor1.moveToNext());
		}
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
						attendanceBean.setAttendance_student_id(Integer.parseInt(cursor1.getString(1)));
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
	
	public ArrayList<AttendanceBean> getAllAttendanceByStudent()
	{
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT attendance_student_id,count(*) FROM attendance_table where attendance_status='P' group by attendance_student_id";
		
		Log.d("query", query);
		
		Cursor cursor = db.rawQuery(query, null);
		
		

		if(cursor.moveToFirst()) 
		{
			do{
				Log.d("studentId","studentId:"+cursor.getString(0)+", Count:"+cursor.getString(1));
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_student_id(Integer.parseInt(cursor.getString(0)));
				attendanceBean.setAttendance_session_id(Integer.parseInt(cursor.getString(1)));
				list.add(attendanceBean);

			}while(cursor.moveToNext());
		}
		return list;
	}
	/*public ArrayList<AttendanceBean> getAllAttendanceBySessionID(int sessionId)
	{
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM attendance_table where attendance_session_id=" + sessionId;
		Cursor cursor = db.rawQuery(query, null);

		if(!cursor.moveToFirst()) 
		{
			do{
				AttendanceBean attendanceBean = new AttendanceBean();
				attendanceBean.setAttendance_session_id(Integer.parseInt(cursor.getString(0)));
				attendanceBean.setAttendance_student_id(Integer.parseInt(cursor.getString(1)));
				attendanceBean.setAttendance_status(cursor.getString(2));
				list.add(attendanceBean);

			}while(cursor.moveToNext());
		}
		return list;
	}*/
	
	


	// Creating Tables
	/*@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_User_Info_TABLE = "CREATE TABLE " + TABLE_INFO_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_FIRSTNAME + " TEXT, "+ KEY_LASTNAME + " TEXT, " +KEY_MO_NO +" TEXT, "
				+KEY_EMAIL +" TEXT, " +KEY_USERNAME +" TEXT, " + KEY_PASSWORD +" TEXT " + ")";

		Log.d("rupali",CREATE_User_Info_TABLE );
		db.execSQL(CREATE_User_Info_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO_USER);

		// Create tables again
		onCreate(db);
	}

	 *//**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 *//*



	void addUserInfo(UserInfo userinfo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FIRSTNAME, userinfo.getUser_Firstname()); //  Name
		values.put(KEY_LASTNAME, userinfo.getUser_Lastname()); //  Name
		values.put(KEY_MO_NO, userinfo.getUser_MobileNo()); // Contact Phone
		values.put(KEY_EMAIL, userinfo.getUser_EmailId());
		values.put(KEY_USERNAME, userinfo.getUser_Username());
		values.put(KEY_PASSWORD, userinfo.getUser_Password());

		// Inserting Row
		db.insert(TABLE_INFO_USER, null, values);
		//2nd argument is String containing nullColumnHack
		db.close(); // Closing database connection
	}


	// Getting single contact
	UserInfo getUserInfo(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_INFO_USER, new String[] { KEY_ID,
				KEY_FIRSTNAME, KEY_LASTNAME,KEY_MO_NO,  KEY_EMAIL, KEY_USERNAME, KEY_PASSWORD }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		UserInfo userinfo = new UserInfo(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6));
		// return contact
				return userinfo;
	}

	public UserInfo validateUser(String username, String password)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "Select * from User_Info_Table WHERE User_Username='"+ username +"' AND User_Password='"+password+"'";
		Log.d("Rupali", "Login QUERY:" + query);

		Cursor cursor = db.rawQuery(query, null);


		if(!cursor.moveToFirst()) 
		{
			Log.d("Rupali", "cursor is null.. returing NULL");
			return null;
		}
		Log.d("Rupali", "cursor is NOT null.. we got user data...");


		UserInfo userinfo = new UserInfo(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6));

		return userinfo;
	}

	// Updating single contact
	public int updateUserPassword(UserInfo userinfo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PASSWORD, userinfo.getUser_Password());


		// updating row
		return db.update(TABLE_INFO_USER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(userinfo.getUser_id()) });
	}

	public int updateUserContact(UserInfo userinfo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MO_NO, userinfo.getUser_MobileNo());
		values.put(KEY_EMAIL, userinfo.getUser_EmailId());


		// updating row
		return db.update(TABLE_INFO_USER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(userinfo.getUser_id()) });
	}


	//veiw details

	public UserInfo viewUserInfo(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "Select * from User_Info_Table WHERE id='"+id+"'";
		Cursor cursor = db.rawQuery(query, null);
		if(!cursor.moveToFirst()) 
		{
			Log.d("Rupali", "cursor is null.. returing NULL");
			return null;
		}
		Log.d("Rupali", "cursor is NOT null.. we got user data...");

		UserInfo userinfo = new UserInfo(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6));
		// return contact
		return userinfo;
	}



	 // Getting All users
    public List<UserInfo> getAllUserInfo() {
        List<UserInfo> userinfolist = new ArrayList<UserInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_INFO_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                UserInfo userinfo=new UserInfo();

                userinfo.setUser_id(Integer.parseInt(cursor.getString(0)));
                userinfo.setUser_Lastname(cursor.getString(2));
                userinfo.setUser_Username(cursor.getString(5));
                userinfo.setUser_Firstname(cursor.getString(1));



                // Adding contact to list
                userinfolist.add(userinfo);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userinfolist;
    }

    // Deleting single contact
    public void deleteUser(UserInfo userinfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INFO_USER, KEY_ID + " = ?",
                new String[] { String.valueOf(userinfo.getUser_id()) });
        db.close();
    }
	  */

	public ArrayList<AttendanceBean> getShortAttendanceBySessionID(AttendanceSessionBean attendanceSessionBean, String startDate, String endDate) {
		ArrayList<AttendanceBean> list = new ArrayList<AttendanceBean>();

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT a.attendance_student_id, s.student_firstname, s.student_lastname, " +
				"SUM(CASE WHEN a.attendance_status = 'P' THEN 1 ELSE 0 END) as present_count, " +
				"COUNT(*) as total_count " +
				"FROM attendance_table a " +
				"JOIN attendance_session_table ast ON a.attendance_session_id = ast.attendance_session_id " +
				"JOIN student_table s ON a.attendance_student_id = s.student_id " +
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
				attendanceBean.setAttendance_student_id(cursor.getInt(0));
				attendanceBean.setStudent_firstname(cursor.getString(1));
				attendanceBean.setStudent_lastname(cursor.getString(2));
				int presentCount = cursor.getInt(3);
				int totalCount = cursor.getInt(4);
				float attendancePercentage = (float) presentCount / totalCount * 100;
				attendanceBean.setAttendance_session_id(Math.round(attendancePercentage)); // Using this field to store percentage
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
				attendanceBean.setAttendance_student_id(cursor.getInt(0));
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
		values.put(KEY_FACULTY_MOBILENUMBER, facultyBean.getFaculty_mobilenumber());
		values.put(KEY_FACULTY_ADDRESS, facultyBean.getFaculty_address());
		values.put(KEY_FACULTY_USERNAME, facultyBean.getFaculty_username());
		values.put(KEY_FACULTY_PASSWORD, facultyBean.getFaculty_password());
		values.put(KEY_FACULTY_REG_STATUS, "pending");

		long result = db.insert(FACULTY_REGISTRATION_TABLE, null, values);
		db.close();
		return result;
	}

	public ArrayList<FacultyBean> getPendingFacultyRegistrations() {
		ArrayList<FacultyBean> list = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + FACULTY_REGISTRATION_TABLE + " WHERE " + KEY_FACULTY_REG_STATUS + "='pending'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				FacultyBean facultyBean = new FacultyBean();
				facultyBean.setFaculty_id(cursor.getInt(0));
				facultyBean.setFaculty_firstname(cursor.getString(1));
				facultyBean.setFaculty_lastname(cursor.getString(2));
				facultyBean.setFaculty_email(cursor.getString(3));
				facultyBean.setFaculty_password(cursor.getString(4));
				list.add(facultyBean);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void approveFacultyRegistration(int facultyId) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		// First, get the faculty registration data
		String selectQuery = "SELECT * FROM " + FACULTY_REGISTRATION_TABLE + 
							 " WHERE " + KEY_FACULTY_REG_ID + " = " + facultyId;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			// Create ContentValues for insertion into main faculty table
			ContentValues values = new ContentValues();
			values.put(KEY_FACULTY_FIRSTNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_FIRSTNAME)));
			values.put(KEY_FACULTY_LASTNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_LASTNAME)));
			values.put(KEY_FACULTY_MO_NO, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_MOBILENUMBER)));
			values.put(KEY_FACULTY_ADDRESS, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_ADDRESS)));
			values.put(KEY_FACULTY_USERNAME, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_USERNAME)));
			values.put(KEY_FACULTY_PASSWORD, cursor.getString(cursor.getColumnIndex(KEY_FACULTY_PASSWORD)));

			// Insert into main faculty table
			db.insert(FACULTY_INFO_TABLE, null, values);

			// Delete from registration table
			db.delete(FACULTY_REGISTRATION_TABLE, KEY_FACULTY_REG_ID + " = ?", 
					  new String[]{String.valueOf(facultyId)});
		}
		
		cursor.close();
		db.close();
	}
}
