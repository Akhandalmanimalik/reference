package com.bizruntime.testdao;

import java.util.List;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.vo.User;

public class TestDao {

	public static void main(String[] args) {
		UserDAOI udao = DAOFactory.getUserDAO();
		// System.out.println(udao);
		// String username=udao.getUserId(101);
		// System.out.println(username);
		// boolean flag=udao.deleteUser(3);
		// System.out.println(flag);
		User user = new User();
		/*
		 * user.setUserId(1); user.setUserName("delete1"); if
		 * (udao.createUser(user)==1){
		 * System.out.println("Record is successfully inserted."); }else{
		 * System.out.println("Failed to insert Data"); }
		 */
//		user.setUserId(1);
//		user.setUserName("hanuman");
//		udao.updateUserName(user);
		List list=udao.getAllUsers();
		if (list!=null){
			for(int i=0;i<list.size();i++){
				System.out.println(list.get(i));
			}
		}else{
			System.out.println("Error in fetching records.");
		}

	}

}
