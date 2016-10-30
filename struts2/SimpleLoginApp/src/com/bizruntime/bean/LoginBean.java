package com.bizruntime.bean;

import com.opensymphony.xwork2.ActionSupport;

public class LoginBean extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String userName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String execute()  {
		if (userName.equals("malik")){
			return SUCCESS;
		}
		return ERROR ;
	}
	
}
