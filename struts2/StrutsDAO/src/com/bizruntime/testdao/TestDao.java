package com.bizruntime.testdao;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.vo.User;

public class TestDao {
	
	public static void main(String[] args) {
		UserDAOI udao=DAOFactory.getUserDAO();
		System.out.println(udao);
		udao.identifyUser("bizruntime", "bizruntime@123");
		User user=udao.getUserEnqDetails("sachin");
		System.out.println(user.firstName+"\n"+user.getLastName()+"\n"+user.getEmailId()+"\n"+user.getMobile());
		
		
		
		
		
	}

}
