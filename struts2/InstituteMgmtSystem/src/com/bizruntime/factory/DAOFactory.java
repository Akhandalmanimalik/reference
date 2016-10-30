//DAOFactory.java
package com.bizruntime.factory;
import com.bizruntime.dao.UserDAO;
import com.bizruntime.dao.UserDAOI;
public class DAOFactory{

	private static UserDAOI udao;
	static {
		udao=new UserDAO();
	}
	public static UserDAOI getUserDAO(){
	return udao;
	}
}