package com.bizruntime.action;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.model.User;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class LoginAction extends ActionSupport implements ModelDriven{
	private String username="biz";
	private String password="biz123";

	public String execute() {
		UserDAOI udao = DAOFactory.getUserDAO();
		User user=new User();
		user.setUname(username);
		user.setPass(password);
		try{
			udao.checkUser(user);
			return "success";
		}catch(Exception e){
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

	@Override
	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
