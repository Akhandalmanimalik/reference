package com.bizruntime.dao;


import java.util.List;

import com.bizruntime.model.Course;
import com.bizruntime.model.Employee;
import com.bizruntime.model.Student;
import com.bizruntime.model.User;

public interface UserDAOI {
	public boolean checkUser(User user) throws Exception;

	public int addCourse(Course course) throws Exception;
	public List getCourses();
	/*public List getStudents(Course course);*/
	public int enrollStudent(Student student) throws Exception;
	public int addEmployee(Employee employee) throws Exception;
	
	
}
