package com.nuvizz.emp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nuvizz.emp.model.EmpDetails;
import com.nuvizz.emp.model.State;
import com.nuvizz.emp.model.User;

public class UserDAO implements UserDAOI {

	private JdbcTemplate jdbctemp;
	private final static String CHECK_USER = "SELECT * FROM USERDETAILS WHERE UNAME=? AND PASS=?";
	private final static String add_employee = "INSERT INTO EMPLOYEE_DETAILS(user_name,first_name,last_name,password,state,city) values (?,?,?,?,?,?)";

	@Override
	public int addEmployee(EmpDetails empdetails) throws Exception{
		String fn = empdetails.getFirstName();
		String ln = empdetails.getLastName();
		String un = empdetails.getUserName();
		String pwd = empdetails.getPassword();
		String stat = empdetails.getState();
		String city = empdetails.getCity();
		return jdbctemp.update(add_employee, new Object[] { un, fn, ln, pwd,
				stat, city });
	}

	@Override
	public boolean checkUser(User user1) throws Exception {

		User user = jdbctemp.queryForObject(CHECK_USER,
				new Object[] { user1.getUname(), user1.getPass() },
				new EmployeeRowMapper());
		if (user != null) {
			return true;
		} else {

			return false;
		}
	}

	private final class EmployeeRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowCount) throws SQLException {
			return new User(rs.getString("uname"), rs.getString("pass"));
		}
	}

	public List getCities(State state) {
		System.out.println(state.getStateName() + "In DAO");
		String GET_CITIES = "SELECT cityname FROM STATEDETAILS WHERE statename='"
				+ state.getStateName() + "'";
		ArrayList al = new ArrayList();
		List<Map<String, Object>> list = jdbctemp.queryForList(GET_CITIES);
		for (Map<String, Object> map : list) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				al.add(entry.getValue());
			}
		}
		return al;
	}

	public UserDAO(JdbcTemplate jdbctemp) {
		this.jdbctemp = jdbctemp;

	}
}
