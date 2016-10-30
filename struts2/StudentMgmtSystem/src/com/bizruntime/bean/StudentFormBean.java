package com.bizruntime.bean;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class StudentFormBean extends ActionForm {
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
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors ae = new ActionErrors();
		if (studentId == 0)
			ae.add("id_e", new ActionMessage("idError"));
		if (studentName.equals(""))
			ae.add("name_e", new ActionMessage("nameError"));
		if (email.equals(""))
			ae.add("email_e", new ActionMessage("emailError"));
		if (address.equals(""))
			ae.add("address_e", new ActionMessage("addressError"));
		if (getGender()==null||("").equals(getGender()))
			ae.add("gender_e", new ActionMessage("genderError"));
		if (getHobies() == null ||("".equals(getHobies())))
			ae.add("hobies_e",new ActionMessage("hobieError"));
		return ae;
	}
}
