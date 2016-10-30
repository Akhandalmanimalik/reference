package com.bizruntime.testdao;

import java.util.List;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.model.Employee;
import com.bizruntime.model.Student;

public class TestDao {
	public static void main(String[] args) throws Exception {
		UserDAOI udao = DAOFactory.getUserDAO();
		Employee employee=new Employee();
		employee.setUname("malik");
		employee.setPass("malik123");
		employee.setFirstName("Akhandalmani");
		employee.setLastName("Malik");
		employee.setState("Odisha");
		employee.setCity("Berhampur");
		udao.addEmployee(employee);
		}
		
	}

