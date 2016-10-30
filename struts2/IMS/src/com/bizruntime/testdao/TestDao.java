package com.bizruntime.testdao;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;

public class TestDao {
	public static void main(String[] args) {
		UserDAOI udao=DAOFactory.getUserDAO();
		System.out.println(udao);
		udao.identifyUser("bizruntime", "bizruntime@123");
	}

}
