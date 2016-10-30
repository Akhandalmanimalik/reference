//UserDAOI.java
package com.bizruntime.dao;
import com.bizruntime.vo.User;
public interface UserDAOI{

	public boolean identifyUser(String uname,String pass);
	public User getUserEnqDetails(String user);
}
