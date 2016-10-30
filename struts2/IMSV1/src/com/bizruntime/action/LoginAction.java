package com.bizruntime.action;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.model.User;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport  {
	private String username = "biz";
	private String password = "biz123";
	private User user;

	public String execute() {
		UserDAOI udao = DAOFactory.getUserDAO();
		System.out.println(getUser().getUname());
		try {
			if (udao.checkUser(getUser())) {
				return "success";
			} else {
				addActionError(getText("error.login"));
				return "error";

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 throw new RuntimeException("Something is wrong in search logic!");
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
