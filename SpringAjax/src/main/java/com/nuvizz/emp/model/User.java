package com.nuvizz.emp.model;

public class User {

	private String uname; // naming convention should be userName 
	private String pass; // naming convention should be password

	public User() {
	}

	public User(String uname, String pass) {
		super();
		this.uname = uname;
		this.pass = pass;
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
}
