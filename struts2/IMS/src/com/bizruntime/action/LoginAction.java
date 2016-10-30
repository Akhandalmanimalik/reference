package com.bizruntime.action;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.daoimpl.UserDAO;
import com.bizruntime.factory.DAOFactory;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
	private String username;
	private String password;

	public String execute() {
		UserDAOI udao = DAOFactory.getUserDAO();
		if (udao.identifyUser(username, password)) {
			return "success";
		} else {
			addActionError(getText("error.login"));
			return "error";

	}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
