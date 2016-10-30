package com.bizruntime.controller;

import com.opensymphony.xwork2.ActionSupport;

public class StudentController extends ActionSupport {

	private int studentId;
	private String studentName;
	private String email;
	private String address;
	private String gender;
	private String[] hobies;

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String[] getHobies() {
		return hobies;
	}

	public void setHobies(String[] hobies) {
		this.hobies = hobies;
	}

	@Override
	public void validate() {
		if (studentId == 0)
			addFieldError("studentId", "Id is required.");
		if (studentName.equals(""))
			addFieldError("studentName", "Name is required.");
		if (email.equals(""))
			addFieldError("email", "Email is required.");
		if (address.equals(""))
			addFieldError("address", "Address is required.");
		if (getGender() == null || ("").equals(getGender()))
			addFieldError("gender", "Gender is required.");
		if (getHobies() == null || ("".equals(getHobies())))
			addFieldError("hobies", "At least one hobie is requir need.");
		// you need to <result name="input">errormsgpage.jsp</result>
	}

	@Override
	public String execute() throws Exception {
		// Do controller operation .
		System.out.println(studentId);
		System.out.println(studentName);
		System.out.println(email);
		System.out.println(address);
		System.out.println(gender);
		System.out.println(hobies);
		return "success";
	}
}