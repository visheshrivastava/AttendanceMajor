package com.android.attendance.bean;

public class AttendanceBean {

	private long attendance_session_id;
	private String attendance_student_id;
	private String attendance_status;
	private String student_firstname;
	private String student_lastname;
	private String attendance_session_date;
	private String faculty_name;
	private String subject;
	
	public long getAttendance_session_id() {
		return attendance_session_id;
	}
	public void setAttendance_session_id(long attendance_session_id) {
		this.attendance_session_id = attendance_session_id;
	}
	public String getAttendance_student_id() {
		return attendance_student_id;
	}
	public void setAttendance_student_id(String attendance_student_id) {
		this.attendance_student_id = attendance_student_id;
	}
	public String getAttendance_status() {
		return attendance_status;
	}
	public void setAttendance_status(String attendance_status) {
		this.attendance_status = attendance_status;
	}
	public String getStudent_firstname() {
		return student_firstname;
	}
	public void setStudent_firstname(String student_firstname) {
		this.student_firstname = student_firstname;
	}
	public String getStudent_lastname() {
		return student_lastname;
	}
	public void setStudent_lastname(String student_lastname) {
		this.student_lastname = student_lastname;
	}
	public String getAttendance_session_date() {
		return attendance_session_date;
	}
	public void setAttendance_session_date(String attendance_session_date) {
		this.attendance_session_date = attendance_session_date;
	}
	public String getFaculty_name() {
		return faculty_name;
	}
	public void setFaculty_name(String faculty_name) {
		this.faculty_name = faculty_name;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
