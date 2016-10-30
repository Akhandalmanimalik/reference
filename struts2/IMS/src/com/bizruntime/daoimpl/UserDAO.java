//UserDAO.java
package com.bizruntime.daoimpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DBConnectionFactory;
import com.bizruntime.vo.User;
public class UserDAO implements UserDAOI{
	public boolean identifyUser(String uname,String pass){
		Connection con=null;
		try{
			con=DBConnectionFactory.getConnection();
			Statement st=con.createStatement();	
			String sql="select * from USER_LOGIN where uname='"+uname+"' and pass='"+pass+"'";
			ResultSet rs=st.executeQuery(sql);

			if (rs.next()){
				System.out.println("Given user is exist");
				return true;
			}else{
				System.out.println("Given user is not Exist");
				return false;
			}
	}catch(Exception e){
	e.printStackTrace();
	throw new RuntimeException("failed in identifying user");
	}
	finally{
		try{
		con.close();
		}catch(Exception e){}
	}
}

//Getting user Details 
public User getUserEnqDetails(String user){

	Connection con=null;
	User enq=null;
	try{
			con=DBConnectionFactory.getConnection();
			Statement st=con.createStatement();
			String sql="select firstName,lastName,emailId,mobile from user_enquiry_master where uname='"+user+"'";
			
			ResultSet rs=st.executeQuery(sql);
			if(rs.next()){
				enq=new User();
				enq.firstName=rs.getString(1);
				enq.lastName=rs.getString(2);
				enq.emailId=rs.getString(3);
				enq.mobile=rs.getString(4);
				return enq;
			}else{

				return null;
			}
		}catch(Exception e){
		e.printStackTrace();
		throw new RuntimeException("failed in getUserEnqDetails");
	}
	finally{
	try{
		con.close();
	}catch(Exception e){}
	}
  }//getUserEnqDetails
};