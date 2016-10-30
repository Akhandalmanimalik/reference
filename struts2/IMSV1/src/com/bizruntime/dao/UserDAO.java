package com.bizruntime.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bizruntime.factory.DBConnectionFactory;
import com.bizruntime.model.Course;
import com.bizruntime.model.Employee;
import com.bizruntime.model.Student;
import com.bizruntime.model.User;

public class UserDAO implements UserDAOI {
	private final static String ADD_EMP = "INSERT INTO USER_DETAILS(user_name,password, first_name,	last_name,state,city) values (?,?,?,?,?,?)";
	private final static String ADD_USER = "INSERT INTO USER_LOGIN(UNAME,PASS) values (?,?)";
	Connection con = null;

	public boolean checkUser(User user) throws Exception {
		System.out.println("in check user oooooooooo");
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String CHECK_USER = "select * from USER_LOGIN where uname='"
					+ user.getUname() + "' and pass='" + user.getPass() + "'";
			ResultSet rs = st.executeQuery(CHECK_USER);

			if (rs.next()) {
				System.out.println("Given user is exist");
				return true;
			} else {
				System.out.println("Given user is not Exist");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("failed in identifying user");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}

	public int addCourse(Course course) throws Exception {
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String ADD_COURSE = "INSERT INTO COURSE_DETAILS(id,course_name,fee,duration) values ('"
					+ course.getCourseId()
					+ "', '"
					+ course.getCourseName()
					+ "', '"
					+ course.getFee()
					+ "', '"
					+ course.getDuration()
					+ "')";
			int i = st.executeUpdate(ADD_COURSE);
			return i;
		} catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException("failed in identifying user");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public List getCourses() {
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String GET_COURSES = "SELECT course_name FROM COURSE_DETAILS";
			ResultSet rs = st.executeQuery(GET_COURSES);
			List courseList = new ArrayList();
			Course course = new Course();
			while (rs.next()) {
				courseList.add(rs.getString(1));
			}
			return courseList;
		} catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException("failed in identifying user");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public int enrollStudent(Student student) throws Exception {
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();

			String ENROLL_STUDENT = "INSERT INTO STUDENT_DETAILS(id,student_name,course_name,fee,duration) values ('"
					+ student.getId()
					+ "', '"
					+ student.getStudentName()
					+ "', '"
					+ student.getCourseName()
					+ "', '"
					+ student.getFee() + "', '" + student.getDuration() + "')";
			int i = st.executeUpdate(ENROLL_STUDENT);
			System.out.println("Student enrolled succefully");
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("somthing wrong in put");
			throw new RuntimeException("failed in identifying user");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}

	}

	@Override
	public int addEmployee(Employee employee) throws Exception {
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();

			String ADD_EMP = "INSERT INTO USER_DETAILS(user_name,password, first_name,	last_name,state,city) values ('"
					+ employee.getUname()
					+ "','"
					+ employee.getPass()
					+ "','"
					+ employee.getFirstName()
					+ "','"
					+ employee.getLastName()
					+ "','"
					+ employee.getState()
					+ "','"
					+ employee.getCity()
					+ "')";
			String ADD_USER = "INSERT INTO USER_LOGIN(UNAME,PASS) values ('"
					+ employee.getUname() + "','" + employee.getPass() + "')";
			int i = st.executeUpdate(ADD_USER);

			int i2 = st.executeUpdate(ADD_EMP);
			System.out.println("New User Created Successfully.");
			return i2;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("somthing wrong in put");
			throw new RuntimeException("failed in identifying user");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}
}
