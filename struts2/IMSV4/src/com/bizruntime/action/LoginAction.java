package com.bizruntime.action;

import java.util.List;

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
	private List<String> listCourses;

	public String checkUser() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in check user Action");
		try {
			if (udao.checkUser(getUser())) {
				return "success";
			} else {
				addActionError(getText("error.login"));
				return "error";

			}
		} catch (Exception e) {
			throw new RuntimeException("Something is wrong in search logic!");
		}
	}

	public String addEmployee() {
		udao = DAOFactory.getUserDAO();
		System.out.println("in addEmployee Action");
		try {
			udao.addEmployee(getEmployee());
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.register"));
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
			System.out.println("exception in addCourse");
			addActionError(getText("error.create"));
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
			addActionError(getText("error.enroll"));
			return "error";
		}
	}
	
	public String getCourses() {
		udao = DAOFactory.getUserDAO();
		System.out.println("coming");
		try {
			listCourses = udao.getCourses();
			return "success";
		} catch (Exception e) {
			addActionError(getText("error.enroll"));
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

	public List<String> getListCourses() {
		return listCourses;
	}

	public void setListCourses(List<String> listCourses) {
		this.listCourses = listCourses;
	}
	
	
	
}
