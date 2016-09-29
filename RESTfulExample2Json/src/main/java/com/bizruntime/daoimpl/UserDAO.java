//UserDAO.java
package com.bizruntime.daoimpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DBConnectionFactory;
import com.bizruntime.vo.User;

public class UserDAO implements UserDAOI {

	public String getUserId(int userid) {

		Connection con = null;
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String sql = "select user_name from users where user_id='" + userid
					+ "'";

			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {

				String username = rs.getString(1);
				return username;
			} else {

				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("failed in getUserEnqDetails");
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}// getUser
		// DELETE USER

	public boolean deleteUser(int userid) {

		Connection con = null;
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String sql = "delete from users where user_id='" + userid + "'";
			st.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException("failed in getUserEnqDetails");
			return false;
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}// deleteUser
	
	//CREATE RECORD
	public int createUser(User user) {

		Connection con = null;
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String sql = "insert into users values('" + user.getUserId() + "','"+user.getUserName()+"')";
			st.executeUpdate(sql);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException("failed in getUserEnqDetails");
			return 0;
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}// create record
	
	//UPDATE RECORD
	public int updateUserName(User user){

		Connection con = null;
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String sql = "update users set user_name='"+user.getUserName()+ "' where user_id=" +user.getUserId();
			st.executeUpdate(sql);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException("failed in getUserEnqDetails");
			return 0;
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}// update record.
	
	
	//Will be develope!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	//GET ALL USERS RECORDS
	@Override
	public List getAllUsers() {

		Connection con = null;
		try {
			con = DBConnectionFactory.getConnection();
			Statement st = con.createStatement();
			String sql = "select * from users";
			List listUsers=new ArrayList();
			ResultSet rs=st.executeQuery(sql);
			while(rs.next()){
				listUsers.add(rs.getString(2));
			}
			return listUsers;
		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException("failed in getUserEnqDetails");
			return null;
		} finally {
			try {
				con.close();
			} catch (Exception e) {
			}
	}// getAll users
}
};