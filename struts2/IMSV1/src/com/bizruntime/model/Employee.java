package com.bizruntime.model;

public class Employee {
	private String uname;
	private String pass;
	private String firstName;
	private String lastName;
	private String state;
	private String city;

	public Employee(String uname, String pass, String firstName,
			String lastName, String state, String city) {
		super();
		this.uname = uname;
		this.pass = pass;
		this.firstName = firstName;
		this.lastName = lastName;
		this.state = state;
		this.city = city;
	}

	public Employee() {
		super();
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
