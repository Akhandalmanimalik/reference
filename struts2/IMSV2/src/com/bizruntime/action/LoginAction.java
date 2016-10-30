package com.bizruntime.action;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.model.Course;
import com.bizruntime.model.Employee;
import com.bizruntime.model.Student;
import com.bizruntime.model.User;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class LoginAction extends ActionSupport implements ModelDriven {
	private static final long serialVersionUID = -6659925652584240539L;
	private User user;
	private Employee employee;
	private Course course;
	private Student student;
	private UserDAOI udao;

	public String checkUser() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in check user Action");
		System.out.println(getUser().getUname());

		try {
			udao.checkUser(getUser());
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.login"));
			return "error";
		}
	}

	public String addEmployee() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in addEmployee Action");
		try {
			udao.addEmployee(getEmployee());
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.login"));
			return "error";
		}
	}

	public String addCourse() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in addCourse Action");
		try {
			udao.addCourse(getCourse());
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.login"));
			return "error";
		}
	}

	public String enrollStudent() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in Enroll Action" + getStudent());
		try {
			udao.enrollStudent(getStudent());
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.login"));
			return "error";
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public User getModel() {
		return user;
	}

	/*
	 * @Override public Employee getModel1() { return employee; }
	 */
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
