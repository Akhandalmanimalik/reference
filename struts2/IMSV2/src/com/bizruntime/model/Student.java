package com.bizruntime.model;

public class Student {
	private int id;
	private String studentName;
	private String courseName;
	private int fee;
	private int duration;
	
	public Student() {
		super();
	}
	public Student(int id, String studentName, String courseName, int fee,
			int duration) {
		super();
		this.id = id;
		this.studentName = studentName;
		this.courseName = courseName;
		this.fee = fee;
		this.duration = duration;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
}
