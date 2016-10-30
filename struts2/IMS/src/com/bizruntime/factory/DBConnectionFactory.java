package com.bizruntime.factory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionFactory{
	private static Properties p1;
	private static String url;
	private static Driver d;
	
	static{
		try{
	p1=new Properties();
	Class c1=DBConnectionFactory.class;
	ClassLoader cl=c1.getClassLoader();
	InputStream is=cl.getResourceAsStream("com/bizruntime/factory/jdbc.properties");
	//InputStream is=new FileInputStream("jdbc.properties");
	p1.load(is);

	String driverClass=p1.getProperty("driver");
	Class c=Class.forName(driverClass);
	d=(Driver)c.newInstance();
	url=p1.getProperty("url");
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("failed to load DBConnection factory");
		}
	}//static
public static Connection getConnection()throws SQLException{
	return d.connect(url,p1);
}//getConnection
}//class