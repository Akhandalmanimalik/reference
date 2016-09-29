package com.bizruntime;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Registration extends HttpServlet {
	
	
	
	//protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("comming");
		PrintWriter out=response.getWriter();
		
		out.println("hello");
			
			String email=request.getParameter("email");
			String name=request.getParameter("name");
			
			//String mn=request.getParameter("mn");
			//String mj=request.getParameter("mj");
			System.out.println("email"+email);
			System.out.println("name"+name);
		System.out.println(">>>>>>>>>>>>>>>>>>>name"+name);
		System.out.println(">>>>>>>>>>>>>>>>>>>email"+email);
			
			Connection con=null;
			
			DriverManager dr=null;
			
		   PreparedStatement stmt=null;
		   
		   try{
			Class.forName("com.mysql.jdbc.Driver");
			
			String url="jdbc:mysql://localhost:3306/bizruntime?user=root&password=malik@123";
			
			con=DriverManager.getConnection(url);
			
			String s="insert into bizruntime(name,email) values(?,?)";
			
			stmt=con.prepareStatement(s);
			
			stmt.setString(1,email);
			stmt.setString(2,name);
			int rs=stmt.executeUpdate();
			out.println("data is inserted");
			
		   }
			catch(Exception e){
				e.printStackTrace();
			}

	}

		
	}

