//UserDAOI.java
package com.bizruntime.dao;

import java.util.List;

import com.bizruntime.vo.User;

public interface UserDAOI {
	public String getUserId(int userid);

	public boolean deleteUser(int userid);

	public int createUser(User user);

	public int updateUserName(User user);

	public List getAllUsers();
}
