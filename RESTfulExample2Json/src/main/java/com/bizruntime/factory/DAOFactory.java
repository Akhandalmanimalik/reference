//DAOFactory.java
package com.bizruntime.factory;
import com.bizruntime.dao.UserDAOI;
import com.bizruntime.daoimpl.UserDAO;
public class DAOFactory{

	private static UserDAOI udao;
	static {
		udao=new UserDAO();
	}
	public static UserDAOI getUserDAO(){
	return udao;
	}
}