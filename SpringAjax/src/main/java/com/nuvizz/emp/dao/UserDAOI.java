package com.nuvizz.emp.dao;

import java.util.List;

import com.nuvizz.emp.model.EmpDetails;
import com.nuvizz.emp.model.State;
import com.nuvizz.emp.model.User;

public interface UserDAOI {
	public boolean checkUser(User user) throws Exception;

	public int addEmployee(EmpDetails empdetails) throws Exception;

	public List getCities(State state);
}
